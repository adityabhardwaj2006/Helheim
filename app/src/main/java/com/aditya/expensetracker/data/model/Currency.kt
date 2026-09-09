package com.aditya.expensetracker.data.model

data class Currency(
    val code: String,          // ISO code: INR, USD, EUR
    val name: String,          // Indian Rupee
    val symbol: String,        // ₹
    val country: String,       // India
    val flag: String           // 🇮🇳
)