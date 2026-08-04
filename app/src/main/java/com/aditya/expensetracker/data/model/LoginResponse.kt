package com.aditya.expensetracker.data.model

data class LoginResponse(
    val success : Boolean,
    val message : String,
    val token : String,
    val user : UserDTO
)