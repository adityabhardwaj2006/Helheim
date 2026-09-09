package com.aditya.expensetracker.domain.repo

import com.aditya.expensetracker.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface Repo  {

    fun getAllTransaction (userId: String) : Flow<List<Transaction>>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)


}