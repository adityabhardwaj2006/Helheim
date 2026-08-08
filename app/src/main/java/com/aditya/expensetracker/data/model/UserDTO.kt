package com.aditya.expensetracker.data.model

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("_id")
    var id : String,
    var firstName : String,
    var lastName : String,
    var username : String
)