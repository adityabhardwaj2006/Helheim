package com.aditya.expensetracker.data.manager

import android.util.Log
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.domain.repo.NotificationRepo
import com.aditya.expensetracker.domain.repo.Repo
import com.aditya.expensetracker.domain.repo.TransactionRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionManager @Inject constructor(
    private val repo: TransactionRepo,
    private val tokenManager: TokenManager,
    private val localRepo: Repo,
    private val notificationRepo: NotificationRepo
) {

    suspend fun saveTransaction(
        amount: Double,
        date: Long?,
        type: String,
        paidTo: String?,
        category: String?,
        savedFor: String?,
        receivedFrom: String?,
        paymentMethod: String,
        userId: String
    ): TransactionResponse = withContext(Dispatchers.IO) {
        val token = tokenManager.getToken().first()
        if (token.isNullOrEmpty()) {
            return@withContext TransactionResponse(success = false, message = "Authentication token is missing", transaction = null)
        }
        val bearerToken = "Bearer $token"
        
        val response = repo.saveTransaction(
            amount, date, type, paidTo, category, savedFor, receivedFrom, paymentMethod,
            token = bearerToken
        )

        // If backend save is successful, save to local database with the backend _id
        if (response.success && response.transaction != null) {
            localRepo.insertTransaction(
                Transaction(
                    backendId = response.transaction._id,
                    amount = response.transaction.amount,
                    date = response.transaction.date,
                    type = response.transaction.type,
                    paidTo = response.transaction.paidTo,
                    category = response.transaction.category,
                    savedFor = response.transaction.savedFor,
                    receivedFrom = response.transaction.receivedFrom,
                    paymentMethod = response.transaction.paymentMethod,
                    userId = userId
                )
            )
            // Refresh the shared transaction list
            repo.getTransactions(bearerToken)
            
            // Trigger notifications and background tasks
            try {
                notificationRepo.generateNotifications(bearerToken)
                val transactionsResponse = repo.getTransactions(bearerToken)
                notificationRepo.generateClientSideAlerts(userId, transactionsResponse.transactions)
            } catch (e: Exception) {
                Log.e("TransactionManager", "Background tasks failed: ${e.message}")
            }
        }
        
        response
    }
}
