package com.aditya.expensetracker.data.model

data class NotificationResponse(
    val success: Boolean,
    val notifications: List<NotificationItem>
)

data class NotificationItem(
    val _id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)
