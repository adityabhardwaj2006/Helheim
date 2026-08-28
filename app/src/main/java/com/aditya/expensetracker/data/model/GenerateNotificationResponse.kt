package com.aditya.expensetracker.data.model

data class GenerateNotificationResponse(
    val success: Boolean,
    val message: String,
    val count: Int? = null
)
