package com.aditya.expensetracker.domain.repo

import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.TransactionType
import com.aditya.expensetracker.data.model.getTransactionResponse

interface TransactionRepo {
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

}