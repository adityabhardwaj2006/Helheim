package com.aditya.expensetracker.data.model

data class profileResponse(
    val success : Boolean,
    val data : profileData
)

data class profileData(
    val firstName : String,
    val lastName : String,
    val username : String,
)