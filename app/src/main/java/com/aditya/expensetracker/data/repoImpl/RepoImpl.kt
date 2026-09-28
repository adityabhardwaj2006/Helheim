package com.aditya.expensetracker.data.repoImpl

import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepoImpl @Inject constructor(
    private val dao : TransactionDao
) : Repo {
    override fun getAllTransaction(userId: String): Flow<List<Transaction>> {
        return dao.getAllTransaction(userId)
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        return dao.insertTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        return dao.updateTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        return dao.deleteTransaction(transaction)
    }

    override fun getUnsyncedTransaction(userId: String): Flow<List<Transaction>> {
        return dao.getUnsyncedTransaction(userId)
    }

    // Check if duplicate transaction exists in local database
    override suspend fun hasMatchingTransaction(userId: String, amount: Double, type: String, date: Long): Boolean {
        return dao.countMatchingTransactions(userId, amount, type, date) > 0
    }

}