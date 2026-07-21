package com.aditya.expensetracker.data

import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.domain.Repo.Repo
import com.aditya.expensetracker.domain.model.Transaction
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class RepoImpl @Inject constructor(
    private val dao : TransactionDao
) : Repo {
    override fun getAllTransaction(): Flow<List<Transaction>> {
        return dao.getAllTransaction()
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
}