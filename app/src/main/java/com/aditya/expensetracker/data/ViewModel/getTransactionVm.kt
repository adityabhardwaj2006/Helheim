package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.getTransactionResponse
import com.aditya.expensetracker.domain.repo.TransactionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class getTransactionVm @Inject constructor(
    private val repo : TransactionRepo,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _transactionState = MutableStateFlow<ResultState<getTransactionResponse>>(ResultState.Idle)
    val transactionState = _transactionState.asStateFlow()




}