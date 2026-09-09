package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.data.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    val preferredPaymentMethod: StateFlow<String> = tokenManager.getCurrencyMethod()
        .map { it ?: "₹" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "₹")

    fun setPreferredPaymentMethod(method: String) {
        viewModelScope.launch {
            tokenManager.saveCurrency(method)
        }
    }

    val preferredPaymentCode: StateFlow<String> = tokenManager.getCurrencyCode()
        .map { it ?: "INR" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "INR")

    fun setPreferredCurrencyCode(code: String) {
        viewModelScope.launch {
            tokenManager.saveCurrencyCode(code)
        }
    }
}
