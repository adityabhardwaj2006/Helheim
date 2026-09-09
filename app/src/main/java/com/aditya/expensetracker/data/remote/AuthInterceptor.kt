package com.aditya.expensetracker.data.remote

import android.util.Log
import com.aditya.expensetracker.data.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    private val TAG = "AuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401) {
            val path = request.url.encodedPath
            if (path.contains("/signin") || path.contains("/signup")) {
                return response
            }

            // Global handling for Unauthorized errors
            // This will clear the token, and since AuthViewModel observes isLoggedIn,
            // the app should reactively redirect to Login screen.
            runBlocking {
                tokenManager.clearToken()
            }
        }
        return response
    }
}
