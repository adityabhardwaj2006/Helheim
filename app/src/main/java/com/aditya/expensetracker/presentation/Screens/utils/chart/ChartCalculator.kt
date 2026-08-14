package com.aditya.expensetracker.presentation.Screens.utils.chart

import android.os.Build
import androidx.annotation.RequiresApi
import com.aditya.expensetracker.data.model.ChartPoint
import com.aditya.expensetracker.data.model.ChartType
import com.aditya.expensetracker.data.model.TimeFilter
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionType
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

object ChartCalculator {
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateChartPoints(
        transactions: List<TransactionItem>,
        chartType: ChartType,
        timeFilter: TimeFilter,
        cumulative: Boolean = true
    ): List<ChartPoint> {
        val targetType = when (chartType) {
            ChartType.INCOME -> TransactionType.INCOME.name
            ChartType.EXPENSE -> TransactionType.EXPENSE.name
            ChartType.SAVINGS -> TransactionType.SAVINGS.name
        }

        val filtered = transactions.filter { it.type == targetType }
        val now = LocalDate.now()

        val points = when (timeFilter) {
            TimeFilter.WEEK -> calculateWeekly(filtered, now)
            TimeFilter.MONTH -> calculateMonthly(filtered, now)
            TimeFilter.YEAR -> calculateYearly(filtered, now)
        }

        return if (cumulative) {
            var runningTotal = 0f
            points.map { it.copy(value = (runningTotal + it.value).also { runningTotal = it }) }
        } else {
            points
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateWeekly(transactions: List<TransactionItem>, now: LocalDate): List<ChartPoint> {
        val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = startOfWeek.plusDays(6)

        val grouped = transactions.filter {
            val date = toLocalDate(it.date?:0L)
            !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)
        }.groupBy { toLocalDate(it.date?:0L).dayOfWeek }

        return DayOfWeek.entries.map { day ->
            val label = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val total = grouped[day]?.sumOf { it.amount }?.toFloat() ?: 0f
            ChartPoint(label, total)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateMonthly(transactions: List<TransactionItem>, now: LocalDate): List<ChartPoint> {
        val firstDay = now.with(TemporalAdjusters.firstDayOfMonth())
        val lastDay = now.with(TemporalAdjusters.lastDayOfMonth())

        val grouped = transactions.filter {
            val date = toLocalDate(it.date?:0L)
            !date.isBefore(firstDay) && !date.isAfter(lastDay)
        }.groupBy { toLocalDate(it.date?:0L).dayOfMonth }

        return (1..lastDay.dayOfMonth).map { day ->
            val total = grouped[day]?.sumOf { it.amount }?.toFloat() ?: 0f
            ChartPoint(day.toString(), total)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateYearly(transactions: List<TransactionItem>, now: LocalDate): List<ChartPoint> {
        val currentYear = now.year

        val grouped = transactions.filter {
            toLocalDate(it.date?:0L).year == currentYear
        }.groupBy { toLocalDate(it.date?:0L).month }

        return Month.entries.map { month ->
            val label = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val total = grouped[month]?.sumOf { it.amount }?.toFloat() ?: 0f
            ChartPoint(label, total)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toLocalDate(timestamp: Long): LocalDate {
        val millis = if (timestamp < 10000000000L) timestamp * 1000 else timestamp
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    }
}
