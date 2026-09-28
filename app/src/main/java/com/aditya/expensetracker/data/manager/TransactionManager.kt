package com.aditya.expensetracker.data.manager

import android.util.Log
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.connectivity.ConnectivityObserver
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
    private val notificationRepo: NotificationRepo,
    private val connectivityObserver: ConnectivityObserver
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

        val txDate = date ?: System.currentTimeMillis()

        // Deduplication Check: Prevent saving if an identical transaction exists in Room DB
        if (localRepo.hasMatchingTransaction(userId, amount, type, txDate)) {
            Log.d("TransactionManager", "Duplicate transaction detected ($amount $type). Skipping save.")
            return@withContext TransactionResponse(
                success = true,
                message = "Duplicate transaction skipped",
                transaction = null
            )
        }

        val isOnline = connectivityObserver.isConnected.first()

        if (!isOnline) {
            // Save locally with isSynced = false
            localRepo.insertTransaction(
                Transaction(
                    amount = amount,
                    date = date,
                    type = type,
                    paidTo = paidTo ?: "",
                    category = category,
                    savedFor = savedFor ?: "",
                    receivedFrom = receivedFrom ?: "",
                    paymentMethod = paymentMethod,
                    userId = userId,
                    isSynced = false
                )
            )
            return@withContext TransactionResponse(
                success = true,
                message = "Offline: Transaction saved locally and will sync when online.",
                transaction = null
            )
        }

        return@withContext try {
            val response = repo.saveTransaction(
                amount, date, type, paidTo, category, savedFor, receivedFrom, paymentMethod,
                token = bearerToken
            )

            // If backend save is successful, save to local database with the backend _id and isSynced = true
            if (response.success && response.transaction != null) {
                localRepo.insertTransaction(
                    Transaction(
                        backendId = response.transaction._id,
                        amount = response.transaction.amount,
                        date = response.transaction.date,
                        type = response.transaction.type,
                        paidTo = response.transaction.paidTo.toString(),
                        category = response.transaction.category,
                        savedFor = response.transaction.savedFor.toString(),
                        receivedFrom = response.transaction.receivedFrom.toString(),
                        paymentMethod = response.transaction.paymentMethod,
                        userId = userId,
                        isSynced = true
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
        } catch (e: Exception) {
            // If network call fails, save locally as fallback
            localRepo.insertTransaction(
                Transaction(
                    amount = amount,
                    date = date,
                    type = type,
                    paidTo = paidTo ?: "",
                    category = category,
                    savedFor = savedFor ?: "",
                    receivedFrom = receivedFrom ?: "",
                    paymentMethod = paymentMethod,
                    userId = userId,
                    isSynced = false
                )
            )
            TransactionResponse(
                success = true,
                message = "Network error: Saved locally and will sync later.",
                transaction = null
            )
        }
    }
}
