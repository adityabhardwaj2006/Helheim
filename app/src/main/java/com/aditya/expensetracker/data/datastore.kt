package com.aditya.expensetracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val Context.dataStore by preferencesDataStore("auth")

    companion object {

        val TOKEN = stringPreferencesKey("token")

    }

    suspend fun saveToken(token: String) {

        context.dataStore.edit {
            it[TOKEN] = token
        }

    }

    fun getToken(): Flow<String?> {

        return context.dataStore.data.map {
            it[TOKEN]
        }

    }

    suspend fun clearToken() {

        context.dataStore.edit {
            it.remove(TOKEN)
        }

    }

}