package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.domain.repo.Repo
import com.aditya.expensetracker.data.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class transactionViewModel @Inject constructor(
    private val repo : Repo
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

    val transactions = repo.getAllTransaction()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

}