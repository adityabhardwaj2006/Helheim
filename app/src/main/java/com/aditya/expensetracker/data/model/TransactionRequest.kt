package com.aditya.expensetracker.data.model

data class TransactionRequest(
    val amount: Double,
    val date: Long?,
    val type: String,
    val paidTo: String?,
    val category: String?,
    val savedFor : String?,
    val receivedFrom : String?,
    val paymentMethod: String
)
