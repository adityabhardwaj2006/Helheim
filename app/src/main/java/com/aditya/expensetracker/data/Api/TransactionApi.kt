package com.aditya.expensetracker.data.Api

import com.aditya.expensetracker.data.model.TransactionRequest
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.getTransactionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TransactionApi {

    @POST("/addTransaction")
    suspend fun saveTransaction(
        @Body request : TransactionRequest,
        @Header("Authorization") token : String
    ) : TransactionResponse

    @GET("/getTransactions")
    suspend fun getTransactions(
        @Header("Authorization") token : String
    ) : getTransactionResponse


}