package com.aditya.expensetracker.data.repoImpl

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.Api.AuthApi
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.LoginRequest
import com.aditya.expensetracker.data.model.LoginResponse
import com.aditya.expensetracker.data.model.SignUpRequest
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.data.model.UserDTO
import com.aditya.expensetracker.data.model.profileResponse
import com.aditya.expensetracker.domain.repo.AuthRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authApi : AuthApi,
    private val tokenManager : TokenManager
) : AuthRepo{

    override suspend fun login(
        username: String,
        password: String
    ): LoginResponse {
        val response = authApi.signin(
            LoginRequest(
                username,
                password
            )
        )
        tokenManager.saveToken(response.token)
        tokenManager.saveUserId(response.user.id)

        return response
    }

    override suspend fun signup(
        firstName: String,
        lastName: String,
        username: String,
        password: String
    ): SignUpResponse {
        val response = authApi.signup(SignUpRequest(
            firstName,lastName,username,password
        ))

        return response

    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }

    override suspend fun getProfile(): profileResponse {
        val token = tokenManager.getToken().first() ?: throw Exception("Token not found")
        return authApi.getProfile("Bearer $token")
    }


}