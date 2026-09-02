package com.aditya.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class LocalNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val message: String,
    val type: String,
    val date: Long,
    val isRead: Boolean = false,
    val userId: String
)
