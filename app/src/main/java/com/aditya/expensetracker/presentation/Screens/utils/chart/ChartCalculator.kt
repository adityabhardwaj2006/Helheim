package com.aditya.expensetracker.presentation.Screens.utils.chart

import android.os.Build
import androidx.annotation.RequiresApi
import com.aditya.expensetracker.domain.model.ChartPoint
import com.aditya.expensetracker.domain.model.ChartType
import com.aditya.expensetracker.domain.model.Transaction
import com.aditya.expensetracker.domain.model.TransactionType
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

object ChartCalculator {
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateMonthlyTotals(
        transactions: List<Transaction>,
        chartType: ChartType
    ): List<ChartPoint> {
        val targetType = when (chartType) {
            ChartType.INCOME -> TransactionType.INCOME
            ChartType.EXPENSE -> TransactionType.EXPENSE
            ChartType.SAVINGS -> TransactionType.SAVINGS
        }

        val filtered = transactions.filter { it.type == targetType }
        
        // Group by month value (1-12)
        val groupedByMonth = filtered.groupBy {
            Instant.ofEpochMilli(it.date!!)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .monthValue }
        
        return Month.entries.map { month ->
            val monthName = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val total = groupedByMonth[month.value]?.sumOf { it.amount }?.toFloat() ?: 0f
            ChartPoint(monthName, total)
        }
    }
}
