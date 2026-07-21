package com.aditya.expensetracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "transactions"
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int ? = 0,

    val amount: Double,
    val date: LocalDate,
    val type: TransactionType
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
