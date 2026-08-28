package com.aditya.expensetracker.domain.repo

import com.aditya.expensetracker.data.model.GenerateNotificationResponse
import com.aditya.expensetracker.data.model.NotificationResponse
import com.aditya.expensetracker.data.model.TransactionResponse

interface NotificationRepo {
    suspend fun getNotifications(token: String): NotificationResponse
    suspend fun generateNotifications(token: String): GenerateNotificationResponse
    suspend fun deleteNotification(token: String, id: String): TransactionResponse
}
