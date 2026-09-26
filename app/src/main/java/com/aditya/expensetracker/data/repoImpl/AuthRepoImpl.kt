package com.aditya.expensetracker.data.repoImpl

import android.util.Log
import com.aditya.expensetracker.data.Api.AuthApi
import com.aditya.expensetracker.data.DAO.NotificationDao
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.DAO.UserDao
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.connectivity.ConnectivityObserver
import com.aditya.expensetracker.data.model.ChangePasswordRequest
import com.aditya.expensetracker.data.model.GoogleLoginRequest
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
    private val transactionDao: TransactionDao,
    private val notificationDao: NotificationDao,
    private val connectivityObserver: ConnectivityObserver
) : AuthRepo{
    private val TAG = "AuthDebug"

    override suspend fun login(
        username: String,
        password: String
    ): LoginResponse {
        val isOnline = connectivityObserver.isConnected
        if(!isOnline.first()){
            throw Exception("No internet connection")
        }
        try {

            val response = authApi.signin(
                LoginRequest(
                    username,
                    password
                )
            )
            return handleLoginResponse(response)
        } catch (e: HttpException) {
            throw Exception(parseError(e))
        }
    }

    override suspend fun googleLogin(idToken: String, email: String?, photoUrl: String?): LoginResponse {
        Log.d(TAG, "Calling backend /signin/google with token length: ${idToken.length}")
        try {
            val isOnline = connectivityObserver.isConnected
            if(!isOnline.first()){
                throw Exception("No internet connection")
            }
            val response = authApi.googleSignin(
                GoogleLoginRequest(idToken)
            )
            Log.d(TAG, "Backend response received: ${response.success}")
            return handleLoginResponse(response, email, photoUrl)
        } catch (e: HttpException) {
            val errorMsg = parseError(e)
            Log.e(TAG, "Google login failed: $errorMsg")
            throw Exception(errorMsg)
        }
    }

    private suspend fun handleLoginResponse(
        response: LoginResponse,
        fallbackEmail: String? = null,
        fallbackPhotoUrl: String? = null
    ): LoginResponse {
        if (response.success) {
            Log.d(TAG, "Saving token and user info to local storage")
            tokenManager.saveToken(response.token)
            tokenManager.saveUserId(response.user.id)

            // Cache user info immediately for instant UI display
            // Use Google data as fallback if backend doesn't provide it yet
            userDao.insertUser(UserEntity(
                userId = response.user.id,
                firstName = response.user.firstName,
                lastName = response.user.lastName,
                username = response.user.username,
                email = response.user.email ?: fallbackEmail,
                profilePictureUrl = response.user.profilePictureUrl ?: fallbackPhotoUrl
            ))
        } else {
            Log.w(TAG, "Login response was not successful: ${response.message}")
        }

        return response
    }

    override suspend fun signup(
        firstName: String,
        lastName: String,
        username: String,
        password: String
    ): SignUpResponse {
        try {
            val isOnline = connectivityObserver.isConnected
            if(!isOnline.first()){
                throw Exception("No internet connection")
            }
            val response = authApi.signup(SignUpRequest(
                firstName,lastName,username,password
            ))
            return response
        } catch (e: HttpException) {
            throw Exception(parseError(e))
        }
    }

    override suspend fun logout() {

        tokenManager.clearToken()
        transactionDao.deleteAllTransaction()
        userDao.deleteUser()
        notificationDao.deleteAllNotifications()

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
                    username = localUser.username,
                    email = localUser.email,
                    profilePictureUrl = localUser.profilePictureUrl
                )
            )
        } else {
            val isConnected = connectivityObserver.isConnected.first()
            if (!isConnected) {
                return profileResponse(success = true, data =
                    profileData(
                        userId = "1",
                        firstName = "HelHiem User",
                        lastName = "",
                        username = "helHeim",
                        email = "support@helheim.com",
                        profilePictureUrl = ""
                    ))
            }

            val token = tokenManager.getToken().first() ?: return profileResponse(success = false, data = null)
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
                    username = responseData.username ?: "",
                    email = responseData.email,
                    profilePictureUrl = responseData.profilePictureUrl
                ))
                Log.d(TAG, "getProfile: saved to local storage")
                
                return networkResponse
            } catch (e: HttpException) {
                Log.e(TAG, "getProfile: HTTP error ${e.code()}: ${e.message()}")
                if (e.code() == 401) {
                    tokenManager.clearToken()
                    throw e
                }
                return profileResponse(success = true, data =
                    profileData(
                        userId = "1",
                        firstName = "HelHiem User",
                        lastName = "",
                        username = "helHeim",
                        email = "support@helheim.com",
                        profilePictureUrl = ""
                    ))
            } catch (e: Exception) {
                Log.e(TAG, "getProfile: network fetch failed: ${e.message}")
                return profileResponse(success = true, data =
                    profileData(
                        userId = "1",
                        firstName = "HelHiem User",
                        lastName = "",
                        username = "helHeim",
                        email = "support@helheim.com",
                        profilePictureUrl = ""
                    ))
            }
        }
    }

    override suspend fun updateProfile(
        firstName: String,
        lastName: String,
        username: String
    ) {
        try {
            val token = tokenManager.getToken().first() ?: throw Exception("Login required to update profile")
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
            val localUser = userDao.getUser()
            val currFirstName = localUser?.firstName ?: ""
            val currLastName = localUser?.lastName ?: ""
            val currUsername = localUser?.username ?: ""
            val currEmail = localUser?.email
            val currProfilePictureUrl = localUser?.profilePictureUrl

            userDao.insertUser(UserEntity(
                userId = storedUserId,
                firstName = if (firstName == "") currFirstName else firstName,
                lastName = if (lastName == "") currLastName else lastName,
                username = if (username == "") currUsername else username,
                email = currEmail,
                profilePictureUrl = currProfilePictureUrl
            ))
        } catch (e: HttpException) {
            Log.e(TAG, "updateProfile: HTTP error ${e.code()}")
            if (e.code() == 401) {
                tokenManager.clearToken()
            }
            throw e
        } catch (err: Exception) {
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

        } catch (e: HttpException) {
            throw Exception(parseError(e))
        } catch (err: Exception) {
            throw err
        }
    }

    private fun parseError(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, SignUpResponse::class.java)
            errorResponse.message
        } catch (_: Exception) {
            e.message() ?: "Something went wrong"
        }
    }


}
