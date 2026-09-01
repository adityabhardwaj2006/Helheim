package com.aditya.expensetracker.domain.repo

import com.aditya.expensetracker.data.model.HistoryMessage
import com.aditya.expensetracker.data.model.chatResponse

interface ChatRepo {
    suspend fun getChat(
        token: String,
        message: String,
        history: List<HistoryMessage> = emptyList()
    ): chatResponse
}
