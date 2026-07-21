package com.aditya.expensetracker.domain.model

import java.time.LocalDate

data class Transaction(
    val id: String,
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
