package com.aditya.expensetracker.data.Api

import com.aditya.expensetracker.data.model.ChangePasswordRequest
import com.aditya.expensetracker.data.model.LoginRequest
import com.aditya.expensetracker.data.model.LoginResponse
import com.aditya.expensetracker.data.model.RatingRequest
import com.aditya.expensetracker.data.model.SignUpRequest
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.data.model.UserDTO
import com.aditya.expensetracker.data.model.profileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi{
    @POST("/signin")
    suspend fun signin(
        @Body request : LoginRequest
    ) : LoginResponse


    @POST("/signup")
    suspend fun signup(
        @Body request : SignUpRequest
    ) : SignUpResponse

    @GET("/profile")
    suspend fun getProfile(
        @Header("Authorization") token : String
    ) : profileResponse

    @POST("/updateProfile")
    suspend fun updateProfile(
        @Header("Authorization") token : String,
        @Body request : UserDTO
    ) : SignUpResponse

    @POST("/updatePassword")
    suspend fun updatePassword(
        @Header("Authorization") token : String,
        @Body request : ChangePasswordRequest
    ) : SignUpResponse

    @POST("/giveRating")
    suspend fun giveRating(
        @Header("Authorization") token : String,
        @Body request : RatingRequest
        ) : SignUpResponse


}
