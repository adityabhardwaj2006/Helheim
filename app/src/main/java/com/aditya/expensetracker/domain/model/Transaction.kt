package com.aditya.expensetracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "transactions"
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val amount: Double,
    val date: Long?,
    val type: TransactionType,
    val paidTo: String,
    val category: String?,
    val savedFor : String,
    val receivedFrom : String,
    val paymentMethod: String
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
