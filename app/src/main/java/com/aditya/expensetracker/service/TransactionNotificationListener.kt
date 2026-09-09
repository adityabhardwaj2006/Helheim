package com.aditya.expensetracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aditya.expensetracker.R
import com.aditya.expensetracker.data.DAO.NotificationDao
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.manager.TransactionManager
import com.aditya.expensetracker.data.model.LocalNotification
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class TransactionNotificationListener : NotificationListenerService() {
    private val CHANNEL_ID = "transaction_monitoring_channel"
    private val NOTIFICATION_ID = 1001

    // Simple de-duplication cache: Stores "Amount|Type|Merchant" -> Timestamp
    private val processedCache = mutableMapOf<String, Long>()
    private val DUPLICATION_WINDOW_MS = 5000L // 5 seconds

    @Inject
    lateinit var transactionDao: TransactionDao

    @Inject
    lateinit var transactionManager: TransactionManager


    @Inject
    lateinit var notificationDao: NotificationDao

    @Inject
    lateinit var tokenManager: TokenManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val amountRegex = Pattern.compile("(?:Rs|INR|₹|RE|RES)\\.?\\s*([\\d,]+(?:\\.\\d{2})?)", Pattern.CASE_INSENSITIVE)
    private val debitKeywords = listOf("debited", "spent", "paid", "sent", "transferred to", "transfer to", "payment of", "paying", "deducted", "withdrawal")
    private val creditKeywords = listOf("credited", "received", "added to", "transferred from", "transfer from", "cashback", "refunded", "deposited")

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListener", "Service connected and listening")
        
        createNotificationChannel()
        startMonitoringForeground()
        
        logDebugMessage("Service started and listening for transactions")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Transaction Monitoring"
            val descriptionText = "Ensures the app accurately detects expenses from notifications"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startMonitoringForeground() {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Expense Monitoring Active")
            .setContentText("Listening for transaction notifications...")
            .setSmallIcon(R.mipmap.helhime)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        val notification = sbn?.notification ?: return
        val extras = notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val bigText = extras.getCharSequence("android.bigText")?.toString() ?: ""
        
        val fullText = "$title $text $bigText"

        Log.d("NotificationListener", "----------------------------------------")
        Log.d("NotificationListener", "Incoming: from ${sbn.packageName}")
        Log.d("NotificationListener", "Content: $fullText")
        
        parseTransaction(fullText, sbn.packageName)
    }

    private fun parseTransaction(text: String, packageName: String) {
        val matcher = amountRegex.matcher(text)
        val amounts = mutableListOf<Double>()
        while (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "")
            amountStr?.toDoubleOrNull()?.let { 
                amounts.add(it)
                Log.d("NotificationListener", "Found Amount: $it")
            }
        }

        if (amounts.isEmpty()) {
            Log.d("NotificationListener", "Ignored: No amount found in text.")
            return
        }

        val isDebit = debitKeywords.any { text.contains(it, ignoreCase = true) }
        val isCredit = creditKeywords.any { text.contains(it, ignoreCase = true) }

        Log.d("NotificationListener", "Status: isDebit=$isDebit, isCredit=$isCredit")

        if (isDebit || isCredit) {
            val transactionAmount = amounts.first() 
            val type = if (isCredit) TransactionType.INCOME.name else TransactionType.EXPENSE.name
            val merchant = extractMerchant(text, packageName)

            // DE-DUPLICATION LOGIC
            val cacheKey = "$transactionAmount|$type|$merchant"
            val currentTime = System.currentTimeMillis()
            val lastProcessedTime = processedCache[cacheKey] ?: 0L

            if (currentTime - lastProcessedTime < DUPLICATION_WINDOW_MS) {
                Log.d("NotificationListener", "DUPLICATE: Already processed $cacheKey recently. Skipping.")
                return
            }
            
            processedCache[cacheKey] = currentTime
            // Clean up cache periodically (remove items older than 1 minute)
            if (processedCache.size > 50) {
                processedCache.entries.removeIf { currentTime - it.value > 60000L }
            }

            Log.d("NotificationListener", "MATCH: Detected $type of ₹$transactionAmount from $merchant")
            logDebugMessage("Detected $type of ₹$transactionAmount from $merchant")
            
            saveTransaction(
                transactionAmount, 
                type, 
                if (isDebit) merchant else "", 
                if (isCredit) merchant else "", 
                packageName,
                text
            )
        } else {
            Log.d("NotificationListener", "Ignored: No transaction keywords matched.")
        }
    }

    private fun logDebugMessage(message: String) {
        serviceScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: "unknown"
                notificationDao.insertNotification(
                    LocalNotification(
                        title = "System Debug",
                        message = message,
                        type = "DEBUG",
                        date = System.currentTimeMillis(),
                        userId = userId
                    )
                )
            } catch (e: Exception) {
                Log.e("NotificationListener", "Failed to log debug message", e)
            }
        }
    }

    private fun extractMerchant(text: String, packageName: String): String {
        val appName = when (packageName) {
            "com.google.android.apps.nbu.paisa.user" -> "Google Pay"
            "com.phonepe.app" -> "PhonePe"
            "net.one97.paytm" -> "Paytm"
            "com.fampay.app" -> "FamPay"
            else -> null
        }

        val separators = listOf(" to ", " at ", " from ", " for ", " towards ")
        var extracted = ""
        
        for (sep in separators) {
            val index = text.indexOf(sep, ignoreCase = true)
            if (index != -1) {
                extracted = text.substring(index + sep.length).trim().split(" ").take(3).joinToString(" ")
                break
            }
        }

        extracted = extracted.split("thru", "via", "using", ".", ",", " successful", " on ").first().trim()

        if (extracted.isEmpty()) {
            val lastDash = text.lastIndexOf("-")
            if (lastDash != -1 && lastDash < text.length - 1) {
                val potentialBank = text.substring(lastDash + 1).trim()
                if (potentialBank.length in 2..6) {
                    extracted = potentialBank
                }
            }
        }

        return when {
            extracted.isNotEmpty() -> extracted
            appName != null -> appName
            else -> "Automatic Detection"
        }
    }

    private fun saveTransaction(amount: Double, type: String, paidTo: String, receivedFrom: String, packageName: String, fullText: String) {
        serviceScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch
                
                val paymentMethod = when {
                    fullText.contains("UPI", ignoreCase = true) -> "UPI"
                    fullText.contains("IMPS", ignoreCase = true) -> "IMPS"
                    fullText.contains("NEFT", ignoreCase = true) -> "NEFT"
                    fullText.contains("Debit Card", ignoreCase = true) -> "Debit Card"
                    else -> "Notification ($packageName)"
                }

                transactionManager.saveTransaction(
                    amount = amount,
                    date = System.currentTimeMillis(),
                    type = type,
                    paidTo = paidTo,
                    category = "Auto-tracked",
                    savedFor = "",
                    receivedFrom = receivedFrom,
                    paymentMethod = paymentMethod,
                    userId = userId
                )
            } catch (e: Exception) {
                Log.e("NotificationListener", "Failed to save transaction: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
