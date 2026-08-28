package com.aditya.expensetracker.domain.repo

import com.aditya.expensetracker.data.model.SignUpResponse

interface RatingRepo {
    suspend fun giveRating(
        rating : Int,
        feedback : String
    ) : SignUpResponse
}