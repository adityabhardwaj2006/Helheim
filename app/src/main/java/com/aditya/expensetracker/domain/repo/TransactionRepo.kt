package com.aditya.expensetracker.domain.repo

import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.TransactionType
import com.aditya.expensetracker.data.model.getTransactionResponse
import kotlinx.coroutines.flow.StateFlow

interface TransactionRepo {
    val transactions: StateFlow<ResultState<getTransactionResponse>>
    suspend fun saveTransaction(
        amount: Double,
        date: Long?,
        type: String,
        paidTo: String?,
        category: String?,
        savedFor : String?,
        receivedFrom : String?,
        paymentMethod: String,
        token: String,
    ) : TransactionResponse

    suspend fun getTransactions(
        token : String
    ) : getTransactionResponse

    suspend fun deleteTransaction(
        token: String,
        id: String
    ): TransactionResponse

    fun updateLocalState(response: getTransactionResponse)

    suspend fun syncOfflineTransaction(
        token: String
    ) : TransactionResponse

}
