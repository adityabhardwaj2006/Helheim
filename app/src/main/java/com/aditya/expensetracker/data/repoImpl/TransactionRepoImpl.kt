package com.aditya.expensetracker.data.repoImpl

import com.aditya.expensetracker.data.Api.TransactionApi
import com.aditya.expensetracker.data.model.TransactionRequest
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.domain.repo.TransactionRepo
import javax.inject.Inject


class TransactionRepoImpl @Inject constructor(
    private val api : TransactionApi
) : TransactionRepo {
    override suspend fun saveTransaction(
        amount: Double,
        date: Long?,
        type: String,
        paidTo: String?,
        category: String?,
        savedFor: String?,
        receivedFrom: String?,
        paymentMethod: String,
        token: String
    ): TransactionResponse {
        return api.saveTransaction(
            TransactionRequest(
                amount = amount,
                date = date,
                type = type,
                paidTo = paidTo,
                category = category,
                savedFor = savedFor,
                receivedFrom = receivedFrom,
                paymentMethod = paymentMethod
            ),
            token = token
        )
    }

}