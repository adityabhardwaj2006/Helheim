package com.aditya.expensetracker.common

import com.aditya.expensetracker.data.model.LoginResponse

sealed class ResultState<out T> {
    object Idle : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val message: String) : ResultState<Nothing>()
}