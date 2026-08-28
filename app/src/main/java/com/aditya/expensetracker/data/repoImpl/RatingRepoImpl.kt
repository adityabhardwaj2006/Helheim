package com.aditya.expensetracker.data.repoImpl

import com.aditya.expensetracker.data.Api.AuthApi
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.RatingRequest
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.domain.repo.RatingRepo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RatingRepoImpl @Inject constructor(
    private val api : AuthApi,
    private val tokenManager : TokenManager
) : RatingRepo{
    
    override suspend fun giveRating(rating: Int, feedback: String) : SignUpResponse{
        val token = tokenManager.getToken().first() ?: ""
        return api.giveRating(
            token = "Bearer $token",
            request = RatingRequest(
                rating,feedback
            )
        )
    }

}
