package com.aditya.expensetracker.data.model

data class SignUpRequest(
    val firstName : String,
    val lastName : String,
    val username : String,
    val password : String
)
