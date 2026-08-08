package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.domain.repo.Repo
import com.aditya.expensetracker.data.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class transactionViewModel @Inject constructor(
    private val repo : Repo,
    private val tokenManager: TokenManager
) : ViewModel() {


    fun addTransaction(transaction : Transaction){
        viewModelScope.launch {
            repo.insertTransaction(transaction)
        }

    }

    fun updateTransaction(transaction : Transaction){
        viewModelScope.launch {
            repo.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction : Transaction){
        viewModelScope.launch {
            repo.deleteTransaction(transaction)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = tokenManager.getUserId()
        .flatMapLatest { userId ->
            repo.getAllTransaction(userId ?: "")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

}