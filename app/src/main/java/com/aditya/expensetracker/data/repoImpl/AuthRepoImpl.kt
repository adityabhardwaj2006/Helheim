package com.aditya.expensetracker.data.repoImpl

import android.util.Log
import com.aditya.expensetracker.data.Api.AuthApi
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.DAO.UserDao
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.ChangePasswordRequest
import com.aditya.expensetracker.data.model.LoginRequest
import com.aditya.expensetracker.data.model.LoginResponse
import com.aditya.expensetracker.data.model.SignUpRequest
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.data.model.UserDTO
import com.aditya.expensetracker.data.model.UserEntity
import com.aditya.expensetracker.data.model.profileData
import com.aditya.expensetracker.data.model.profileResponse
import com.aditya.expensetracker.domain.repo.AuthRepo
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authApi : AuthApi,
    private val tokenManager : TokenManager,
    private val userDao : UserDao,
    private val transactionDao: TransactionDao
) : AuthRepo{

    override suspend fun login(
        username: String,
        password: String
    ): LoginResponse {
        val response = authApi.signin(
            LoginRequest(
                username,
                password
            )
        )
        tokenManager.saveToken(response.token)
        tokenManager.saveUserId(response.user.id)

        return response
    }

    override suspend fun signup(
        firstName: String,
        lastName: String,
        username: String,
        password: String
    ): SignUpResponse {
        val response = authApi.signup(SignUpRequest(
            firstName,lastName,username,password
        ))

        return response

    }

    override suspend fun logout() {

        tokenManager.clearToken()
        transactionDao.deleteAllTransaction()
        userDao.deleteUser()

    }

    override suspend fun getProfile(): profileResponse {
        val localUser = userDao.getUser()
        
        if (localUser != null) {
            return profileResponse(
                success = true,
                data = profileData(
                    userId = localUser.userId,
                    firstName = localUser.firstName,
                    lastName = localUser.lastName,
                    username = localUser.username
                )
            )
        } else {
            val token = tokenManager.getToken().first() ?: throw Exception("Token not found")
            val storedUserId = tokenManager.getUserId().first() ?: ""
            
            try {
                val networkResponse = authApi.getProfile("Bearer $token")
                val responseData = networkResponse.data
                
                if (responseData == null) {
                    throw Exception("Profile data is null")
                }


                // Use storedUserId as fallback if network response doesn't have it
                val finalUserId = responseData.userId ?: storedUserId



                // Save to Room
                userDao.insertUser(UserEntity(
                    userId = finalUserId,
                    firstName = responseData.firstName ?: "",
                    lastName = responseData.lastName ?: "",
                    username = responseData.username ?: ""
                ))
                android.util.Log.d("AuthRepo", "getProfile: saved to local storage")
                
                return networkResponse
            } catch (e: Exception) {
                android.util.Log.e("AuthRepo", "getProfile: network fetch failed: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun updateProfile(
        firstName: String,
        lastName: String,
        username: String
    ) {
        try{
            val token = tokenManager.getToken().first() ?: throw Exception("Token not found")
            authApi.updateProfile(
                token = "Bearer $token",
                request = UserDTO(
                    id = "0",
                    firstName = firstName,
                    lastName = lastName,
                    username = username
                )
            )

            val storedUserId = tokenManager.getUserId().first() ?: ""
            val currFirstName = userDao.getUser()!!.firstName
            val currLastName = userDao.getUser()!!.lastName
            val currUsername = userDao.getUser()!!.username

            userDao.insertUser(UserEntity(
                userId = storedUserId,
                firstName =
                    if(firstName=="")currFirstName
                else firstName,
                lastName =
                    if(lastName=="")currLastName
                    else lastName,
                username =
                    if(username=="")currUsername
                    else username,
            ))
        }catch(err: Exception){
            throw err
        }

    }

    override suspend fun updatePassword(newPassword: String): SignUpResponse {
        try {
            val token = tokenManager.getToken().first()
            val response = authApi.updatePassword(
                token = "Bearer $token",
                request = ChangePasswordRequest(
                    password = newPassword
                )
            )
            return response

        } catch (err: HttpException) {
            val errorBody = err.response()?.errorBody()?.string()
            val message = try {
                val errorResponse = Gson().fromJson(errorBody, SignUpResponse::class.java)
                errorResponse.message
            } catch (_: Exception) {
                err.message() ?: "Conflict occurred"
            }
            throw Exception(message)
        } catch (err: Exception) {
            throw err
        }
    }


}
