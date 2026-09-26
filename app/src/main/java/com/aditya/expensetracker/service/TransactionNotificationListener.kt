package com.aditya.expensetracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.provider.Telephony
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aditya.expensetracker.R
import com.aditya.expensetracker.data.DAO.NotificationDao
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.DAO.UserDao
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.manager.TransactionManager
import com.aditya.expensetracker.data.model.LocalNotification
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
    private val DUPLICATION_WINDOW_MS = 60000L // 60 seconds

    @Inject
    lateinit var transactionDao: TransactionDao

    @Inject
    lateinit var transactionManager: TransactionManager

    @Inject
    lateinit var notificationDao: NotificationDao

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var userDao: UserDao

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListener", "Service connected and listening")
        
        createNotificationChannel()
        startMonitoringForeground()
        
        logDebugMessage("Service started and listening for SMS transaction notifications")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Transaction Monitoring"
            val descriptionText = "Ensures the app accurately detects expenses from SMS notifications"
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
        try {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Expense Monitoring Active")
                .setContentText("Listening for transaction SMS notifications...")
                .setSmallIcon(R.mipmap.helhime)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build()

            var startForegroundSuccess = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
                    startForegroundSuccess = true
                } catch (e: Exception) {
                    Log.w("NotificationListener", "startForeground with dataSync failed: ${e.message}")
                }
            }
            if (!startForegroundSuccess) {
                try {
                    startForeground(NOTIFICATION_ID, notification)
                    startForegroundSuccess = true
                } catch (e: Exception) {
                    Log.w("NotificationListener", "startForeground failed: ${e.message}")
                }
            }

            // Always ensure notification is posted to status bar
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e("NotificationListener", "Failed to start notification monitoring: ${e.message}")
        }
    }

    private fun isMessagingApp(packageName: String, fullText: String = ""): Boolean {
        val pkgLower = packageName.lowercase()

        // 1. Explicit payment & non-SMS apps blacklist
        val excludedPackages = setOf(
            "com.google.android.apps.nbu.paisa.user", // Google Pay
            "com.phonepe.app",                       // PhonePe
            "net.one97.paytm",                        // Paytm
            "com.fampay.app",                         // FamPay
            "in.org.npci.upiapp",                     // BHIM UPI
            "com.mobikwik_new",                       // MobiKwik
            "com.freecharge.android",                 // Freecharge
            "com.whatsapp",                           // WhatsApp
            "com.whatsapp.w4b"                        // WhatsApp Business
        )
        if (excludedPackages.contains(pkgLower)) {
            return false
        }

        // 2. Default SMS package check
        val defaultSmsPkg = try {
            Telephony.Sms.getDefaultSmsPackage(this)
        } catch (_: Exception) {
            null
        }
        if (defaultSmsPkg != null && pkgLower == defaultSmsPkg.lowercase()) {
            return true
        }

        // 3. Known popular SMS / Messaging packages
        val knownSmsPackages = setOf(
            "com.google.android.apps.messaging",
            "com.samsung.android.messaging",
            "com.android.mms",
            "com.oneplus.mms",
            "com.xiaomi.mms",
            "com.miui.smsextra",
            "com.coloros.mms",
            "com.oppo.engineermode",
            "com.vivo.mms",
            "com.truecaller"
        )
        if (knownSmsPackages.contains(pkgLower)) {
            return true
        }

        // 4. Fallback check for messaging package naming convention
        if (pkgLower.endsWith(".mms") || pkgLower.endsWith(".messaging") || pkgLower.contains("sms") || pkgLower.contains("message")) {
            return true
        }

        // 5. Fallback for transaction notifications: Check if content matches transaction patterns
        if (fullText.isNotBlank()) {
            val lowerText = fullText.lowercase()
            val hasCurrency = lowerText.contains("rs") || lowerText.contains("inr") || lowerText.contains("₹") || lowerText.contains("amt")
            val hasAction = lowerText.contains("debited") || lowerText.contains("credited") || lowerText.contains("spent") || lowerText.contains("paid") || lowerText.contains("transferred") || lowerText.contains("received")
            if (hasCurrency && hasAction) {
                return true
            }
        }

        return false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        val sbnPackage = sbn?.packageName ?: return

        val notification = sbn.notification ?: return
        val extras = notification.extras

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString() ?: ""
        val summaryText = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString() ?: ""
        val ticker = notification.tickerText?.toString() ?: ""
        val textLines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString(" ") ?: ""
        
        val fullText = listOf(title, text, bigText, subText, summaryText, ticker, textLines)
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(" ")

        if (fullText.isBlank()) return

        if (!isMessagingApp(sbnPackage, fullText)) {
            Log.d("NotificationListener", "Ignored notification from non-messaging app: $sbnPackage")
            return
        }

        Log.d("NotificationListener", "----------------------------------------")
        Log.d("NotificationListener", "Incoming SMS from $sbnPackage")
        Log.d("NotificationListener", "Content: $fullText")
        
        parseTransaction(fullText, sbnPackage)
    }

    private data class AmountMatch(
        val amount: Double,
        val startIndex: Int,
        val endIndex: Int,
        val isBalance: Boolean,
        val isActionAssociated: Boolean
    )

    private fun extractTransactionAmount(text: String): Double? {
        val patterns = listOf(
            Pattern.compile("(?i)(?:Rs|INR|₹|RE|RES|Amt|Amount)\\.?\\s*([\\d,]+(?:\\.\\d{1,2})?)"),
            Pattern.compile("(?i)([\\d,]+(?:\\.\\d{1,2})?)\\s*(?:Rs|INR|₹|RE|RES)"),
            Pattern.compile("(?i)(?:debited|credited|spent|paid|received|deposited|transferred|withdrawn|charged)\\s+(?:by|for|with|of)?\\s*(?:Rs|INR|₹|RE|RES)?\\.?\\s*([\\d,]+(?:\\.\\d{1,2})?)")
        )

        val balanceKeywords = listOf("bal", "balance", "avail bal", "avail", "limit", "clear bal", "clr bal", "avl bal", "effective bal")
        val actionKeywords = listOf("debited", "credited", "spent", "paid", "received", "deposited", "transferred", "withdrawn", "charged", "purchase", "txn of", "transaction of")

        val matches = mutableListOf<AmountMatch>()

        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val amountStr = matcher.group(1)?.replace(",", "") ?: continue
                val amountVal = amountStr.toDoubleOrNull() ?: continue
                if (amountVal <= 0.0) continue

                val start = matcher.start()
                val end = matcher.end()

                val windowStart = (start - 35).coerceAtLeast(0)
                val windowEnd = (end + 35).coerceAtMost(text.length)
                val surroundingText = text.substring(windowStart, windowEnd).lowercase()

                val isBal = balanceKeywords.any { surroundingText.contains(it) }
                val isAction = actionKeywords.any { surroundingText.contains(it) }

                matches.add(AmountMatch(amountVal, start, end, isBal, isAction))
            }
        }

        if (matches.isEmpty()) return null

        // 1. Prefer action-associated non-balance match
        val actionMatch = matches.firstOrNull { it.isActionAssociated && !it.isBalance }
        if (actionMatch != null) return actionMatch.amount

        // 2. Second preference: Any non-balance match
        val nonBalanceMatch = matches.firstOrNull { !it.isBalance }
        if (nonBalanceMatch != null) return nonBalanceMatch.amount

        // 3. Fallback to first matched amount
        return matches.first().amount
    }

    private fun classifyTransactionType(text: String): TransactionType? {
        // Sanitize text to avoid "credit card", "credit limit", "available credit" false positives
        val sanitizedText = text
            .replace("credit card", "", ignoreCase = true)
            .replace("credit limit", "", ignoreCase = true)
            .replace("credit line", "", ignoreCase = true)
            .replace("available credit", "", ignoreCase = true)
            .replace("credit score", "", ignoreCase = true)
            .replace("credit ac", "", ignoreCase = true)
            .replace("credit account", "", ignoreCase = true)

        val lowerText = sanitizedText.lowercase()

        val explicitDebitKeywords = listOf("debited", "debit", "spent", "paid", "sent to", "deducted", "withdrawal", "withdrawn", "charged", "purchase", "purchased", "used at", "spent at")
        val explicitCreditKeywords = listOf("credited", "credit of", "is credited", "has been credited", "credited with", "credited to", "received", "cashback", "refunded", "deposited")

        val transferDebitKeywords = listOf("transferred from your", "transfer from your", "transferred from a/c", "transferred from ac", "transfer from a/c")
        val transferCreditKeywords = listOf("transferred to your", "transfer to your", "transferred to a/c", "transferred to ac", "transfer to a/c")

        val isDebit = explicitDebitKeywords.any { lowerText.contains(it) } || transferDebitKeywords.any { lowerText.contains(it) }
        val isCredit = explicitCreditKeywords.any { lowerText.contains(it) } || transferCreditKeywords.any { lowerText.contains(it) }

        if (isCredit && !isDebit) return TransactionType.INCOME
        if (isDebit && !isCredit) return TransactionType.EXPENSE

        if (isCredit) {
            val hasExplicitCredited = lowerText.contains("credited")
            val hasExplicitDebited = lowerText.contains("debited")
            if (hasExplicitCredited && !hasExplicitDebited) return TransactionType.INCOME
            if (hasExplicitDebited && !hasExplicitCredited) return TransactionType.EXPENSE
            return TransactionType.EXPENSE
        }

        if (lowerText.contains("debit")) return TransactionType.EXPENSE
        if (lowerText.contains("credit")) return TransactionType.INCOME

        return null
    }

    private fun parseTransaction(text: String, packageName: String) {
        val transactionAmount = extractTransactionAmount(text)
        if (transactionAmount == null) {
            Log.d("NotificationListener", "Ignored: No valid transaction amount found in text.")
            return
        }

        val typeEnum = classifyTransactionType(text)
        if (typeEnum == null) {
            Log.d("NotificationListener", "Ignored: No transaction type keywords matched.")
            return
        }

        val type = typeEnum.name
        val merchant = extractMerchant(text)

        // DE-DUPLICATION LOGIC
        val cacheKey = "$transactionAmount|$type|$merchant"
        val currentTime = System.currentTimeMillis()
        val lastProcessedTime = processedCache[cacheKey] ?: 0L

        if (currentTime - lastProcessedTime < DUPLICATION_WINDOW_MS) {
            Log.d("NotificationListener", "DUPLICATE: Already processed $cacheKey recently. Skipping.")
            return
        }
        
        processedCache[cacheKey] = currentTime
        if (processedCache.size > 50) {
            processedCache.entries.removeIf { currentTime - it.value > DUPLICATION_WINDOW_MS }
        }

        Log.d("NotificationListener", "MATCH: Detected $type of ₹$transactionAmount from/to $merchant")
        logDebugMessage("Detected $type of ₹$transactionAmount from/to $merchant")
        
        val paidTo = if (typeEnum == TransactionType.EXPENSE) merchant else ""
        val receivedFrom = if (typeEnum == TransactionType.INCOME) merchant else ""

        saveTransaction(
            transactionAmount, 
            type, 
            paidTo, 
            receivedFrom, 
            packageName,
            text
        )
    }

    private fun logDebugMessage(message: String) {
        serviceScope.launch {
            try {
                var userId = tokenManager.getUserId().first()
                if (userId.isNullOrEmpty()) {
                    userId = userDao.getUser()?.userId ?: "unknown"
                }
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

    private fun extractMerchant(text: String): String {
        val separators = listOf(" to ", " at ", " from ", " for ", " towards ")
        var extracted = ""
        
        for (sep in separators) {
            val index = text.indexOf(sep, ignoreCase = true)
            if (index != -1) {
                extracted = text.substring(index + sep.length).trim().split(" ").take(3).joinToString(" ")
                break
            }
        }

        extracted = extracted.split("thru", "via", "using", ".", ",", " successful", " on ", " ref", " info").first().trim()

        val noiseWords = listOf("a/c", "account", "vpa", "upi", "ref", "txn", "bal", "balance")
        if (noiseWords.any { extracted.equals(it, ignoreCase = true) }) {
            extracted = ""
        }

        if (extracted.isEmpty()) {
            val upiPattern = Pattern.compile("(?i)(?:vpa|upi[/-])\\s*([a-zA-Z0-9@._-]+)")
            val upiMatcher = upiPattern.matcher(text)
            if (upiMatcher.find()) {
                extracted = upiMatcher.group(1) ?: ""
            }
        }

        return if (extracted.isNotBlank()) extracted else "Auto-tracked"
    }

    private fun saveTransaction(amount: Double, type: String, paidTo: String, receivedFrom: String, packageName: String, fullText: String) {
        serviceScope.launch {
            try {
                var userId = tokenManager.getUserId().first()
                if (userId.isNullOrEmpty()) {
                    userId = userDao.getUser()?.userId
                }
                if (userId.isNullOrEmpty()) {
                    Log.e("NotificationListener", "Failed to save transaction: User ID is null or empty.")
                    logDebugMessage("Failed auto-track: No logged in user found.")
                    return@launch
                }
                
                val paymentMethod = when {
                    fullText.contains("UPI", ignoreCase = true) -> "UPI"
                    fullText.contains("IMPS", ignoreCase = true) -> "IMPS"
                    fullText.contains("NEFT", ignoreCase = true) -> "NEFT"
                    fullText.contains("Credit Card", ignoreCase = true) -> "Credit Card"
                    fullText.contains("Debit Card", ignoreCase = true) -> "Debit Card"
                    else -> "SMS Notification"
                }

                val response = transactionManager.saveTransaction(
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
                Log.d("NotificationListener", "Auto-tracked transaction saved: ${response.message}")
            } catch (e: Exception) {
                Log.e("NotificationListener", "Failed to save transaction: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
