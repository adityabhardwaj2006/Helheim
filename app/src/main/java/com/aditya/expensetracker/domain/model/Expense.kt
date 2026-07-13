package com.aditya.expensetracker.domain.model

import java.util.Date

enum class ExpenseType {
    INCOME,
    EXPENSE
}

data class Expense(
    val id: Int? = null,
    val title: String,
    val amount: Double,
    val date: Date,
    val category: String,
    val type: ExpenseType
)
