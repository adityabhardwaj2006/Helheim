package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.NotificationResponse
import com.aditya.expensetracker.domain.repo.NotificationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repo: NotificationRepo,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _notificationState = MutableStateFlow<ResultState<NotificationResponse>>(ResultState.Idle)
    val notificationState = _notificationState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _refreshSuccess = MutableSharedFlow<Unit>()
    val refreshSuccess = _refreshSuccess.asSharedFlow()

    init {
        getNotifications()
    }

    fun getNotifications() {
        if (_notificationState.value is ResultState.Loading) return
        
        _notificationState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken().first() ?: ""
                val response = repo.getNotifications("Bearer $token")
                val distinctNotifications = response.notifications.distinctBy { it._id }
                _notificationState.value = ResultState.Success(response.copy(notifications = distinctNotifications))
            } catch (e: Exception) {
                _notificationState.value = ResultState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun refreshNotifications() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val token = tokenManager.getToken().first() ?: ""
                // First generate new notifications
                repo.generateNotifications("Bearer $token")
                
                // Add a small delay for better "live" feedback
                kotlinx.coroutines.delay(1000)

                // Then fetch the updated list
                val response = repo.getNotifications("Bearer $token")
                val distinctNotifications = response.notifications.distinctBy { it._id }
                _notificationState.value = ResultState.Success(response.copy(notifications = distinctNotifications))
                
                // Signal success
                _refreshSuccess.emit(Unit)
            } catch (e: Exception) {
                // If refresh fails, we might still want to show the error but stop the refreshing animation
                // For now, let's update the main state if it was empty/error, otherwise just stop refreshing
                if (_notificationState.value !is ResultState.Success) {
                    _notificationState.value = ResultState.Error(e.message ?: "Refresh failed")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        val currentState = _notificationState.value
        if (currentState is ResultState.Success) {
            val updatedNotifications = currentState.data.notifications.filter { it._id != notificationId }
            _notificationState.value = ResultState.Success(
                currentState.data.copy(notifications = updatedNotifications)
            )
            
            // Perform backend deletion
            viewModelScope.launch {
                try {
                    val token = tokenManager.getToken().first() ?: ""
                    repo.deleteNotification("Bearer $token", notificationId)
                } catch (e: Exception) {
                    // If it fails, you might want to show a message or refresh
                    // For now, we'll just log or keep the local deletion
                }
            }
        }
    }
}
