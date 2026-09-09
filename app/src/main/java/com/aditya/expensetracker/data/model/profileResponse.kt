package com.aditya.expensetracker.data.model

import com.google.gson.annotations.SerializedName

data class profileResponse(
    val success : Boolean,
    val data : profileData?
)

data class profileData(
    @SerializedName("userId")
    val userId : String?,
    val firstName : String?,
    val lastName : String?,
    val username : String?,
    val email: String? = null,
    val profilePictureUrl: String? = null
)
