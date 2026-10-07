package com.aditya.expensetracker.data.manager

import android.util.Log
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.connectivity.ConnectivityObserver
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.domain.repo.NotificationRepo
import com.aditya.expensetracker.domain.repo.Repo
import com.aditya.expensetracker.domain.repo.TransactionRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

    private val managerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
            return@withContext TransactionResponse(
                success = false,
                message = "Authentication token is missing",
                transaction = null
            )
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

        // 1. Save locally in Room DB immediately (<10ms)
        val localTx = Transaction(
            amount = amount,
            date = txDate,
            type = type,
            paidTo = paidTo ?: "",
            category = category,
            savedFor = savedFor ?: "",
            receivedFrom = receivedFrom ?: "",
            paymentMethod = paymentMethod,
            userId = userId,
            isSynced = false
        )
        val localId = localRepo.insertTransaction(localTx)

        // 2. Launch backend synchronization asynchronously in the background
        managerScope.launch {
            syncTransactionToBackend(localTx, localId, bearerToken, userId)
        }

        // 3. Return success response instantly to dismiss UI right away
        TransactionResponse(
            success = true,
            message = "Transaction saved locally",
            transaction = null
        )
    }

    private suspend fun syncTransactionToBackend(
        localTx: Transaction,
        localId: Long,
        bearerToken: String,
        userId: String
    ) {
        try {
            val isOnline = connectivityObserver.isConnected.first()
            if (!isOnline) {
                Log.d("TransactionManager", "Offline: Saved locally, will sync when online.")
                return
            }

            val response = repo.saveTransaction(
                amount = localTx.amount,
                date = localTx.date,
                type = localTx.type,
                paidTo = localTx.paidTo,
                category = localTx.category,
                savedFor = localTx.savedFor,
                receivedFrom = localTx.receivedFrom,
                paymentMethod = localTx.paymentMethod,
                token = bearerToken
            )

            if (response.success && response.transaction != null) {
                val updatedTx = localTx.copy(
                    id = localId.toInt(),
                    backendId = response.transaction._id,
                    isSynced = true
                )
                localRepo.updateTransaction(updatedTx)

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
        } catch (e: Exception) {
            Log.e("TransactionManager", "Background sync error: ${e.message}. Saved locally as unsynced.")
        }
    }
}
