package com.aditya.expensetracker.data.Api

import com.aditya.expensetracker.data.model.GenerateNotificationResponse
import com.aditya.expensetracker.data.model.NotificationResponse
import com.aditya.expensetracker.data.model.TransactionResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApi {
    @GET("/getNotifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): NotificationResponse

    @POST("/generate")
    suspend fun generateNotifications(
        @Header("Authorization") token: String
    ): GenerateNotificationResponse

    @DELETE("/deleteNotification/{id}")
    suspend fun deleteNotification(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): TransactionResponse
}
