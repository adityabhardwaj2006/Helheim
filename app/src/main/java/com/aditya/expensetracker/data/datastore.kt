package com.aditya.expensetracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("auth")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    companion object {

        val TOKEN = stringPreferencesKey("token")
        val USER_ID = stringPreferencesKey("user_id")

    }

    suspend fun saveToken(token: String) {

        context.dataStore.edit {
            it[TOKEN] = token
        }

    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit {
            it[USER_ID] = userId
        }
    }

    fun getToken(): Flow<String?> {

        return context.dataStore.data.map {
            it[TOKEN]
        }

    }

    fun getUserId(): Flow<String?> {
        return context.dataStore.data.map {
            it[USER_ID]
        }
    }

    suspend fun clearToken() {

        context.dataStore.edit {
            it.remove(TOKEN)
            it.remove(USER_ID)
        }

    }

}