package com.aditya.expensetracker.data.Api

import com.aditya.expensetracker.data.model.LoginRequest
import com.aditya.expensetracker.data.model.LoginResponse
import com.aditya.expensetracker.data.model.UserDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi{
    @POST("/signin")
    suspend fun signin(
        @Body request : LoginRequest
    ) : LoginResponse

    @GET("/profile")
    suspend fun getProfile(
        @Header("Authorization") token : String
    ) : UserDTO

}