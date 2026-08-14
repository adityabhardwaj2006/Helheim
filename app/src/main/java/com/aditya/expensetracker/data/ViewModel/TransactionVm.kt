package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.getTransactionResponse
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
    private val localRepo : Repo
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
                            id = it.id,
                            amount = it.amount,
                            date = it.date,
                            type = it.type,
                            paidTo = it.paidTo,
                            category = it.category,
                            savedFor = it.savedFor,
                            receivedFrom = it.receivedFrom,
                            paymentMethod = it.paymentMethod
                        )
                    }
                    _getTransactionState.value = ResultState.Success(getTransactionResponse(
                        success = true,
                        transactions = newList
                    ))
                }else{
                    val token = tokenManager.getToken().first() ?: ""
                    val response = repo.getTransactions("Bearer $token")
                    _getTransactionState.value = ResultState.Success(response)

                    response.transactions.forEach {
                        localRepo.insertTransaction(
                            Transaction(
                                id = it.id,
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
                _transactionState.value = ResultState.Success(response)
            }catch (error : Exception){
                _transactionState.value = ResultState.Error(error.message?:"Transaction couldn't be saved")
            }
        }

    }

}