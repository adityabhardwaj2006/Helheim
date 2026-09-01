package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.getTransactionResponse
import com.aditya.expensetracker.domain.repo.NotificationRepo
import com.aditya.expensetracker.domain.repo.Repo
import com.aditya.expensetracker.domain.repo.TransactionRepo
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
    private val localRepo : Repo,
    private val notificationRepo: NotificationRepo
) : ViewModel(){

    private val _transactionState = MutableStateFlow<ResultState<TransactionResponse>>(ResultState.Idle)
    val transactionState = _transactionState.asStateFlow()

    private val _getTransactionState = MutableStateFlow<ResultState<getTransactionResponse>>(
        ResultState.Idle)
    val getTransactionState = _getTransactionState.asStateFlow()
    fun getTransaction() {
        _getTransactionState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: ""
                val list = localRepo.getAllTransaction(userId).first()

                if(list.isNotEmpty()){
                    val newList = list.map {
                        TransactionItem(
                            _id = it.backendId,
                            localId = it.id,
                            amount = it.amount,
                            date = it.date,
                            type = it.type,
                            paidTo = it.paidTo,
                            category = it.category,
                            savedFor = it.savedFor,
                            receivedFrom = it.receivedFrom,
                            paymentMethod = it.paymentMethod
                        )
                    }.distinctBy { it._id ?: it.localId }
                    _getTransactionState.value = ResultState.Success(getTransactionResponse(
                        success = true,
                        transactions = newList
                    ))
                }else{
                    val token = tokenManager.getToken().first() ?: ""
                    val response = repo.getTransactions("Bearer $token")
                    val distinctTransactions = response.transactions.distinctBy { it._id ?: it.localId }
                    _getTransactionState.value = ResultState.Success(response.copy(transactions = distinctTransactions))

                    distinctTransactions.forEach {
                        localRepo.insertTransaction(
                            Transaction(
                                backendId = it._id,
                                amount = it.amount,
                                date = it.date,
                                type = it.type,
                                paidTo = it.paidTo,
                                category = it.category,
                                savedFor = it.savedFor,
                                receivedFrom = it.receivedFrom,
                                paymentMethod = it.paymentMethod,
                                userId = userId
                            )
                        )
                    }
                }
            } catch (error: Exception) {
                _getTransactionState.value =
                    ResultState.Error(error.message ?: "Cannot get the transactions")
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
            try{
                val token = tokenManager.getToken().first() ?: ""
                val response = repo.saveTransaction(
                    amount,date,type,paidTo,category,savedFor,receivedFrom,paymentMethod,
                    token = "Bearer $token"
                )
                
                // If backend save is successful, save to local database with the backend _id
                if (response.success && response.transaction != null) {
                    val userId = tokenManager.getUserId().first() ?: ""
                    localRepo.insertTransaction(
                        Transaction(
                            backendId = response.transaction._id,
                            amount = response.transaction.amount,
                            date = response.transaction.date,
                            type = response.transaction.type,
                            paidTo = response.transaction.paidTo,
                            category = response.transaction.category,
                            savedFor = response.transaction.savedFor,
                            receivedFrom = response.transaction.receivedFrom,
                            paymentMethod = response.transaction.paymentMethod,
                            userId = userId
                        )
                    )
                }

                // Automatically trigger notification generation after a successful transaction
                try {
                    notificationRepo.generateNotifications("Bearer $token")
                } catch (e: Exception) {
                    // Fail silently or log - we don't want to break the transaction flow if notification generation fails
                }

                _transactionState.value = ResultState.Success(response)
            }catch (error : Exception){
                _transactionState.value = ResultState.Error(error.message?:"Transaction couldn't be saved")
            }
        }

    }

    fun resetTransactionState() {
        _transactionState.value = ResultState.Idle
    }

    fun deleteTransaction(transactionItem: TransactionItem) {
        val currentState = _getTransactionState.value
        if (currentState is ResultState.Success) {
            val originalList = currentState.data.transactions
            // Optimistic UI Update
            val updatedList = originalList.filter { it._id != transactionItem._id }
            _getTransactionState.value = ResultState.Success(
                currentState.data.copy(transactions = updatedList)
            )

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
                                // Fallback for legacy items without backendId
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
                        _getTransactionState.value = ResultState.Success(
                            currentState.data.copy(transactions = originalList)
                        )
                    }
                } catch (e: Exception) {
                    // Rollback UI if network error occurred
                    _getTransactionState.value = ResultState.Success(
                        currentState.data.copy(transactions = originalList)
                    )
                }
            }
        }
    }

}
