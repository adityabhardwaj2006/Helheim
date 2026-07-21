package com.aditya.expensetracker.domain.Repo

import com.aditya.expensetracker.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface Repo  {

    fun getAllTransaction () : Flow<List<Transaction>>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)
}