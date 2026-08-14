package com.aditya.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user"
)
data class UserEntity(
    @PrimaryKey
    val userId : String = "",
    val firstName : String = "",
    val lastName : String = "",
    val username : String = ""
)
