package com.aditya.expensetracker.domain.repo

import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.model.LoginResponse
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.data.model.UserDTO
import com.aditya.expensetracker.data.model.profileResponse

interface AuthRepo {
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun signup(firstName:String,lastName:String,username: String,password: String) : SignUpResponse
    suspend fun logout()
    suspend fun getProfile() : profileResponse
    suspend fun updateProfile(firstName: String, lastName: String, username: String)
    suspend fun updatePassword(newPassword : String) : SignUpResponse
}