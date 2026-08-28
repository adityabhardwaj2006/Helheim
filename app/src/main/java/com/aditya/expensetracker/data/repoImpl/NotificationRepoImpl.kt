package com.aditya.expensetracker.data.repoImpl

import com.aditya.expensetracker.data.Api.NotificationApi
import com.aditya.expensetracker.data.model.GenerateNotificationResponse
import com.aditya.expensetracker.data.model.NotificationResponse
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.domain.repo.NotificationRepo
import javax.inject.Inject

class NotificationRepoImpl @Inject constructor(
    private val api: NotificationApi
) : NotificationRepo {
    override suspend fun getNotifications(token: String): NotificationResponse {
        return api.getNotifications(token)
    }

    override suspend fun generateNotifications(token: String): GenerateNotificationResponse {
        return api.generateNotifications(token)
    }

    override suspend fun deleteNotification(token: String, id: String): TransactionResponse {
        return api.deleteNotification(token, id)
    }
}
