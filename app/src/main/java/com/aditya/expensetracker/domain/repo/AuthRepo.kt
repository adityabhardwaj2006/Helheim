package com.aditya.expensetracker.domain.repo

import com.aditya.expensetracker.data.model.LoginResponse

interface AuthRepo {
    suspend fun login(username: String, password: String): LoginResponse
}