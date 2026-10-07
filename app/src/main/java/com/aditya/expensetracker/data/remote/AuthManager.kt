package com.aditya.expensetracker.data.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.aditya.expensetracker.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

data class GoogleUserData(
    val googleIdToken: String,
    val email: String,
    val displayName: String?,
    val firstName: String?,
    val lastName: String?,
    val profilePictureUrl: Uri?
)

class AuthManager(private val context: Context) {

    private val TAG = "AuthDebug"
    private val credentialManager = CredentialManager.create(context)

    fun getAccountSettingsIntent(): Intent {
        return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
    }

    private fun getGoogleIdOption(context: Context): GetGoogleIdOption {
        val webClientId = context.getString(R.string.web_client_id)
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId(webClientId)
            .build()
    }

    suspend fun doGoogleSignIn(
        context: Context,
        onAccountAddRequired: () -> Unit = {},
        onSuccess: (GoogleUserData) -> Unit,
        onError: (String) -> Unit
    ) {
        val googleIdOption = getGoogleIdOption(context)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(context = context, request = request)
            handleSignInResponse(result, onSuccess, onError)
        } catch (e: GetCredentialCancellationException) {
            Log.i(TAG, "User cancelled Google Sign-In process.")
            onError("Sign-in cancelled")
        } catch (e: NoCredentialException) {
            Log.w(TAG, "NoCredentialException: type=${e.type}, message=${e.message}", e)
            onError("No Google credential returned. Please check if your Play Store App Signing SHA-1 fingerprint is added in Firebase Console.")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException: type=${e.type}, message=${e.message}", e)
            onError(e.message ?: "Google Sign-In failed (${e.type})")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign-In: ${e.message}", e)
            onError(e.message ?: "Something went wrong during Google Sign-In")
        }
    }

    private fun handleSignInResponse(
        result: GetCredentialResponse,
        onSuccess: (GoogleUserData) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val userData = GoogleUserData(
                    googleIdToken = googleIdTokenCredential.idToken,
                    email = googleIdTokenCredential.id,
                    displayName = googleIdTokenCredential.displayName,
                    firstName = googleIdTokenCredential.givenName,
                    lastName = googleIdTokenCredential.familyName,
                    profilePictureUrl = googleIdTokenCredential.profilePictureUri
                )
                onSuccess(userData)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing GoogleIdTokenCredential", e)
                onError("Failed to parse Google account credentials")
            }
        } else {
            Log.e(TAG, "Unexpected credential type: ${credential.type}")
            onError("Unexpected credential type received")
        }
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing credential state: ${e.message}", e)
        }
    }
}
