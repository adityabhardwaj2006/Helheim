package com.aditya.expensetracker.data.repoImpl

import com.aditya.expensetracker.data.Api.ChatApi
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.HistoryMessage
import com.aditya.expensetracker.data.model.chatRequest
import com.aditya.expensetracker.data.model.chatResponse
import com.aditya.expensetracker.domain.repo.ChatRepo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ChatRepoImpl @Inject constructor(
    private val api: ChatApi,
    private val tokenManager: TokenManager
) : ChatRepo {
    override suspend fun getChat(
        token: String,
        message: String,
        history: List<HistoryMessage>
    ): chatResponse {
        val currency = tokenManager.getCurrencyMethod().first() ?: "₹"
        return api.chatBot(
            token = token,
            request = chatRequest(
                message = message,
                history = history,
                currency = currency
            )
        )
    }

}
