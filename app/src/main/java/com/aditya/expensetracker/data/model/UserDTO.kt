package com.aditya.expensetracker.data.model

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("id")
    var id : String,
    var firstName : String,
    var lastName : String,
    var username : String,
    var email: String? = null,
    var profilePictureUrl: String? = null
)