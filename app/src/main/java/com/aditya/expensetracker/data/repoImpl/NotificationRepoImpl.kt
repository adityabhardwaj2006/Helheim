package com.aditya.expensetracker.data.repoImpl

import com.aditya.expensetracker.data.Api.NotificationApi
import com.aditya.expensetracker.data.DAO.NotificationDao
import com.aditya.expensetracker.data.model.GenerateNotificationResponse
import com.aditya.expensetracker.data.model.LocalNotification
import com.aditya.expensetracker.data.model.NotificationResponse
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.domain.repo.NotificationRepo
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class NotificationRepoImpl @Inject constructor(
    private val api: NotificationApi,
    private val dao: NotificationDao
) : NotificationRepo {
    override suspend fun getNotifications(token: String): NotificationResponse {
        return api.getNotifications(token)
    }

    override suspend fun generateNotifications(token: String): GenerateNotificationResponse {
        return api.generateNotifications(token)
    }

    override suspend fun deleteNotification(token: String, id: String): TransactionResponse {
        return api.deleteNotification(token, id)
    }

    override suspend fun insertLocalNotification(notification: LocalNotification) {
        dao.insertNotification(notification)
    }

    override fun getLocalNotifications(userId: String): Flow<List<LocalNotification>> {
        return dao.getAllNotifications(userId)
    }

    override suspend fun deleteLocalNotification(id: Int) {
        dao.deleteNotification(id)
    }

    override suspend fun getLocalNotificationByType(type: String, userId: String): LocalNotification? {
        return dao.getNotificationByType(type, userId)
    }

    override suspend fun markLocalAsRead(id: Int) {
        dao.markAsRead(id)
    }

    override suspend fun generateClientSideAlerts(userId: String, transactions: List<TransactionItem>) {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        // 1. Unusual Expense (> 10k)
        transactions.filter { it.type == "EXPENSE" && it.amount > 10000 }.forEach { trans ->
            val type = "UNUSUAL_EXPENSE_${trans._id ?: trans.localId}"
            if (dao.getNotificationByType(type, userId) == null) {
                dao.insertNotification(LocalNotification(
                    title = "Unusual Expense Detected",
                    message = "You spent ₹${String.format("%.2f", trans.amount)} on ${trans.category}. Keep an eye on big spends!",
                    type = type,
                    date = trans.date ?: now,
                    userId = userId
                ))
            }
        }

        // 2. Weekly Spike
        val thisWeekExpenses = transactions.filter { it.type == "EXPENSE" && (it.date ?: 0) > now - (7 * oneDayMillis) }.sumOf { it.amount }
        val lastWeekExpenses = transactions.filter { it.type == "EXPENSE" && (it.date ?: 0) in (now - (14 * oneDayMillis))..(now - (7 * oneDayMillis)) }.sumOf { it.amount }
        
        if (thisWeekExpenses > lastWeekExpenses * 1.5 && lastWeekExpenses > 0) {
            val type = "SPENDING_SPIKE_${SimpleDateFormat("yyyy_ww", Locale.getDefault()).format(Date())}"
            if (dao.getNotificationByType(type, userId) == null) {
                dao.insertNotification(LocalNotification(
                    title = "Weekly Spending Spike",
                    message = "Your spending this week is significantly higher than last week.",
                    type = type,
                    date = now,
                    userId = userId
                ))
            }
        }

        // 3. Monthly Budget (Fixed 20k for now)
        val currentMonthFmt = SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())
        val thisMonthExpenses = transactions.filter { 
            it.type == "EXPENSE" && it.date?.let { d -> SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date(d)) } == currentMonthFmt 
        }.sumOf { it.amount }

        if (thisMonthExpenses > 20000) {
            val type = "BUDGET_EXCEEDED_$currentMonthFmt"
            if (dao.getNotificationByType(type, userId) == null) {
                dao.insertNotification(LocalNotification(
                    title = "Monthly Budget Exceeded",
                    message = "You've spent ₹${String.format("%.2f", thisMonthExpenses)} this month, exceeding your ₹20,000 limit.",
                    type = type,
                    date = now,
                    userId = userId
                ))
            }
        }
    }
}
