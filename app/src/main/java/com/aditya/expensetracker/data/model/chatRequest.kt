package com.aditya.expensetracker.data.model

data class chatRequest(
    val message : String,
    val history: List<HistoryMessage> = emptyList(),
    val currency: String
)

data class HistoryMessage(
    val role: String, // "user" or "assistant"
    val content: String
)
