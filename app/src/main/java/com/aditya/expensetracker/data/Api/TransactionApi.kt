package com.aditya.expensetracker.data.Api

import com.aditya.expensetracker.data.model.TransactionRequest
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.getTransactionResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

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

    @DELETE("/deleteTransaction/{id}")
    suspend fun deleteTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): TransactionResponse
}