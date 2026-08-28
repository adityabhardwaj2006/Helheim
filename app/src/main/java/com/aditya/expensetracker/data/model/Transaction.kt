package com.aditya.expensetracker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["backendId"], unique = true)]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val backendId: String? = null,
    val amount: Double,
    val date: Long?,
    val type: String,
    val paidTo: String,
    val category: String?,
    val savedFor : String,
    val receivedFrom : String,
    val paymentMethod: String,
    val userId: String = ""
)

enum class TransactionType {
    INCOME,
    EXPENSE,
    SAVINGS
}

enum class ChartType {
    INCOME,
    EXPENSE,
    SAVINGS
}

enum class TimeFilter {
    WEEK,
    MONTH,
    YEAR
}
