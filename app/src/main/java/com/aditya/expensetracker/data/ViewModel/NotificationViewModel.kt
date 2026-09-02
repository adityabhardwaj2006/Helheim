package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.LocalNotification
import com.aditya.expensetracker.data.model.NotificationItem
import com.aditya.expensetracker.data.model.NotificationResponse
import com.aditya.expensetracker.domain.repo.NotificationRepo
import com.aditya.expensetracker.domain.repo.TransactionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repo: NotificationRepo,
    private val transactionRepo: TransactionRepo,
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
                val userId = tokenManager.getUserId().first() ?: ""
                
                // 1. Fetch remote
                val remoteResponse = repo.getNotifications("Bearer $token")
                
                // 2. Fetch transactions and generate missing alerts
                val transactionsResponse = transactionRepo.getTransactions("Bearer $token")
                repo.generateClientSideAlerts(userId, transactionsResponse.transactions)

                // 3. Fetch all local and remote for final list
                val localList = repo.getLocalNotifications(userId).first()
                val localItems = localList.map { 
                    NotificationItem(
                        _id = "local_${it.id}",
                        userId = it.userId,
                        title = it.title,
                        message = it.message,
                        type = it.type.substringBefore("_"),
                        isRead = it.isRead,
                        createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date(it.date)),
                        updatedAt = "",
                        __v = 0
                    )
                }
                
                val combined = (remoteResponse.notifications + localItems).sortedByDescending { it.createdAt }.distinctBy { it._id }
                _notificationState.value = ResultState.Success(remoteResponse.copy(notifications = combined))
                
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
                // Generate remote
                repo.generateNotifications("Bearer $token")
                
                // Add a small delay
                kotlinx.coroutines.delay(1000)

                // Refetch all
                getNotifications()
                
                _refreshSuccess.emit(Unit)
            } catch (e: Exception) {
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
            
            viewModelScope.launch {
                try {
                    val token = tokenManager.getToken().first() ?: ""
                    if (notificationId.startsWith("local_")) {
                        val localId = notificationId.removePrefix("local_").toIntOrNull()
                        localId?.let { repo.deleteLocalNotification(it) }
                    } else {
                        repo.deleteNotification("Bearer $token", notificationId)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}
