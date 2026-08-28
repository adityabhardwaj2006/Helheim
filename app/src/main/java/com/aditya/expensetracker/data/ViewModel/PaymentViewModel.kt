package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.data.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    val preferredPaymentMethod: StateFlow<String?> = tokenManager.getPaymentMethod()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Cash"
        )

    fun updatePaymentMethod(method: String) {
        viewModelScope.launch {
            tokenManager.savePaymentMethod(method)
        }
    }
}
