package com.aditya.expensetracker.data.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnected : Flow<Boolean>
}