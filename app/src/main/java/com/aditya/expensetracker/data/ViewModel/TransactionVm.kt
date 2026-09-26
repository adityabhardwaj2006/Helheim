package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.getTransactionResponse
import com.aditya.expensetracker.domain.repo.NotificationRepo
import com.aditya.expensetracker.domain.repo.Repo
import com.aditya.expensetracker.domain.repo.TransactionRepo
import com.aditya.expensetracker.data.manager.TransactionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

//Nodejs backend viewmodel
@HiltViewModel
class TransactionVm @Inject constructor(
    private val repo : TransactionRepo,
    private val tokenManager: TokenManager,
    private val localRepo: Repo,
    private val transactionManager: TransactionManager
) : ViewModel(){

    private val _transactionState = MutableStateFlow<ResultState<TransactionResponse>>(ResultState.Idle)
    val transactionState = _transactionState.asStateFlow()

    val getTransactionState = repo.transactions

    fun resetTransactionState() {
        _transactionState.value = ResultState.Idle
    }

    fun getTransaction() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken().first()
                if (token.isNullOrEmpty()) {
                    repo.getTransactions("Bearer ")
                    return@launch
                }
                repo.getTransactions("Bearer $token")
            } catch (error: Exception) {
                // If the repository didn't update the StateFlow, we could potentially handle it here,
                // but usually the Repo is responsible for updating repo.transactions.
            }
        }
    }

    fun saveTransaction(
        amount : Double,
        date : Long?,
        type : String,
        paidTo : String?,
        category : String?,
        savedFor : String?,
        receivedFrom : String?,
        paymentMethod : String
    ){
        _transactionState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: ""
                val response = transactionManager.saveTransaction(
                    amount, date, type, paidTo, category, savedFor, receivedFrom, paymentMethod, userId
                )
                _transactionState.value = ResultState.Success(response)
                // Refresh list after save (online or offline)
                getTransaction()
            } catch (error: Exception) {
                _transactionState.value = ResultState.Error(error.message ?: "Transaction couldn't be saved")
            }
        }
    }

    fun syncOfflineTransactions() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken().first()
                if (!token.isNullOrEmpty()) {
                    repo.syncOfflineTransaction("Bearer $token")
                    getTransaction()
                }
            } catch (e: Exception) {
                Log.e("TransactionVm", "Failed to sync offline transactions: ${e.message}")
            }
        }
    }

    fun deleteTransaction(transactionItem: TransactionItem) {
        val currentState = getTransactionState.value
        if (currentState is ResultState.Success<*>) {
            val data = currentState.data as getTransactionResponse
            val originalList = data.transactions
            // Optimistic UI Update in shared repository
            val updatedList = originalList.filter { it._id != transactionItem._id }
            repo.updateLocalState(data.copy(transactions = updatedList))

            viewModelScope.launch {
                try {
                    val token = tokenManager.getToken().first() ?: ""
                    val userId = tokenManager.getUserId().first() ?: ""

                    // 1. Delete from Backend
                    val response = transactionItem._id?.let { id ->
                        repo.deleteTransaction("Bearer $token", id)
                    }

                    if (response?.success == true) {
                        // 2. Delete from Room only if backend deletion was successful
                        val localTransactions = localRepo.getAllTransaction(userId).first()
                        val transactionToDelete = localTransactions.find {
                            if (it.backendId != null && transactionItem._id != null) {
                                it.backendId == transactionItem._id
                            } else {
                                it.amount == transactionItem.amount &&
                                it.date == transactionItem.date &&
                                it.type == transactionItem.type &&
                                it.paidTo == transactionItem.paidTo
                            }
                        }

                        transactionToDelete?.let {
                            localRepo.deleteTransaction(it)
                        }
                    } else {
                        // Rollback UI if backend reported failure
                        repo.updateLocalState(data)
                    }
                } catch (e: Exception) {
                    // Rollback UI if network error occurred
                    repo.updateLocalState(data)
                }
            }
        }
    }

}
