package com.aditya.expensetracker.domain.repo

import com.aditya.expensetracker.data.model.GenerateNotificationResponse
import com.aditya.expensetracker.data.model.LocalNotification
import com.aditya.expensetracker.data.model.NotificationResponse
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepo {
    suspend fun getNotifications(token: String): NotificationResponse
    suspend fun generateNotifications(token: String): GenerateNotificationResponse
    suspend fun deleteNotification(token: String, id: String): TransactionResponse
    
    // Local methods
    suspend fun insertLocalNotification(notification: LocalNotification)
    fun getLocalNotifications(userId: String): Flow<List<LocalNotification>>
    suspend fun deleteLocalNotification(id: Int)
    suspend fun getLocalNotificationByType(type: String, userId: String): LocalNotification?
    suspend fun markLocalAsRead(id: Int)
    suspend fun generateClientSideAlerts(userId: String, transactions: List<TransactionItem>)
}
