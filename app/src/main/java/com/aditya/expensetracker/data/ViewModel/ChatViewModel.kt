package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.HistoryMessage
import com.aditya.expensetracker.data.model.chatResponse
import com.aditya.expensetracker.domain.repo.ChatRepo
import com.aditya.expensetracker.presentation.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val repo: ChatRepo,
    val tokenManager: TokenManager
) : ViewModel() {
    private val _chatState = MutableStateFlow<ResultState<chatResponse>>(ResultState.Idle)
    val chatState = _chatState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    fun getChat(message: String) {
        if (message.isBlank()) return

        // Prepare history BEFORE adding the new user message to the UI list
        // (The AI needs the context *before* the current question)
        val history = _messages.value.map {
            HistoryMessage(
                role = if (it.isUser) "user" else "assistant",
                content = it.text
            )
        }

        // Add user message to UI list
        val userMsg = ChatMessage(text = message, isUser = true)
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            _chatState.value = ResultState.Loading
            val token = tokenManager.getToken().first() ?: ""
            try {
                val response = repo.getChat(
                    token = "Bearer $token",
                    message = message,
                    history = history
                )
                _chatState.value = ResultState.Success(response)

                // Add bot response to list
                if (response.success) {
                    val botMsg = ChatMessage(text = response.message, isUser = false)
                    _messages.value = _messages.value + botMsg
                }
            } catch (err: Exception) {
                _chatState.value = ResultState.Error(err.message ?: "Something went wrong")
                val errMsg =
                    ChatMessage(text = "Error: ${err.message ?: "Something went wrong"}", isUser = false)
                _messages.value = _messages.value + errMsg
            }
        }
    }

}
