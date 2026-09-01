package com.aditya.expensetracker.data.Api

import com.aditya.expensetracker.data.model.chatRequest
import com.aditya.expensetracker.data.model.chatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatApi {
    @POST("/chat")
    suspend fun chatBot(
        @Header("Authorization") token : String,
        @Body request : chatRequest
    ) : chatResponse
}