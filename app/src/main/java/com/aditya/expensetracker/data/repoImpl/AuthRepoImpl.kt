package com.aditya.expensetracker.data.repoImpl

import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.Api.AuthApi
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.LoginRequest
import com.aditya.expensetracker.data.model.LoginResponse
import com.aditya.expensetracker.data.model.SignUpRequest
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.domain.repo.AuthRepo
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

}