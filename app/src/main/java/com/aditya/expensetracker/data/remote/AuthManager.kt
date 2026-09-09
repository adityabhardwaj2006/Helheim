package com.aditya.expensetracker.data.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.aditya.expensetracker.BuildConfig

data class GoogleUserData(
    val googleIdToken: String,
    val email: String,
    val displayName: String?,
    val firstName: String?,
    val lastName: String?,
    val profilePictureUrl: Uri?
)

class AuthManager(private val context : Context) {

    private val TAG = "AuthDebug"

    private val googleSignInClient : GoogleSignInClient by lazy {
        Log.d(TAG, "Initializing GoogleSignInClient")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        Log.d(TAG, "Getting sign-in intent")
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(data: Intent?): Result<GoogleUserData> {
        Log.d(TAG, "Handling sign-in result")
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken ?: throw Exception("ID Token is null")
            Log.d(TAG,"PHOTOT URL : ${account.photoUrl}")

            Result.success(
                GoogleUserData(
                    googleIdToken = idToken,
                    email = account.email ?: "",
                    displayName = account.displayName,
                    firstName = account.givenName,
                    lastName = account.familyName,
                    profilePictureUrl = account.photoUrl
                )
            )

        } catch (e: ApiException) {
            Result.failure(Exception("Google Sign-in failed: ${e.statusCode}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}
