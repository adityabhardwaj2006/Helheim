package com.aditya.expensetracker.presentation.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.domain.model.ChartType
import com.aditya.expensetracker.domain.model.Transaction
import com.aditya.expensetracker.domain.model.TransactionType
import com.aditya.expensetracker.presentation.components.StatisticsChart
import java.time.LocalDate
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(navController: NavController) {
    var selectedChartType by remember { mutableStateOf(ChartType.EXPENSE) }
    
    val sampleTransactions = rememberSaveable {
        generateSampleTransactions()
    }

    Scaffold(
        topBar = {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.White)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Text(
                    text = "Statistics",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
                }
            }
        }
    ) {innerPadding->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(innerPadding)
        ) {
            // Balance Section (Total of selected type)
            val totalAmount = remember(selectedChartType, sampleTransactions) {
                sampleTransactions
                    .filter { it.type.name == selectedChartType.name }
                    .sumOf { it.amount }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "₹${String.format(Locale.getDefault(), "%,.2f", totalAmount)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Text(
                    text = "Summary of 2026",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Time Filter (Now Chart Type Filter)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White, RoundedCornerShape(24.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChartTypeFilterItem(
                    text = "Income",
                    isSelected = selectedChartType == ChartType.INCOME,
                    onClick = { selectedChartType = ChartType.INCOME },
                    modifier = Modifier.weight(1f)
                )
                ChartTypeFilterItem(
                    text = "Expense",
                    isSelected = selectedChartType == ChartType.EXPENSE,
                    onClick = { selectedChartType = ChartType.EXPENSE },
                    modifier = Modifier.weight(1f)
                )
                ChartTypeFilterItem(
                    text = "Savings",
                    isSelected = selectedChartType == ChartType.SAVINGS,
                    onClick = { selectedChartType = ChartType.SAVINGS },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Custom Line Chart
            StatisticsChart(
                transactions = sampleTransactions,
                chartType = selectedChartType,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                lineColor = when(selectedChartType) {
                    ChartType.INCOME -> Color(0xFF4CAF50)
                    ChartType.EXPENSE -> Color(0xFFF44336)
                    ChartType.SAVINGS -> Color(0xFF2196F3)
                },
                gradientColor = when(selectedChartType) {
                    ChartType.INCOME -> Color(0xFF4CAF50)
                    ChartType.EXPENSE -> Color(0xFFF44336)
                    ChartType.SAVINGS -> Color(0xFF2196F3)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Top Spending Section
            Text(
                text = "Top Spending",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reuse items or map from sample
            sampleTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .take(3)
                .forEach { transaction ->
                    SpendingItem(
                        title = transaction.id.toString(),
                        date = transaction.date.toString(),
                        amount = "-₹${String.format(Locale.getDefault(), "%,.2f", transaction.amount)}",
                        icon = Icons.Default.Add
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

            Spacer(modifier = Modifier.height(100.dp)) // Padding for Bottom Bar
        }


    }



}

@Composable
fun ChartTypeFilterItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        color = if (isSelected) Color.Black else Color.Transparent,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SpendingItem(title: String, date: String, amount: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF0F0F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = date, color = Color.Gray, fontSize = 12.sp)
        }
        Text(text = amount, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun generateSampleTransactions(): List<Transaction> {
    val list = mutableListOf<Transaction>()
    val types = TransactionType.entries.toTypedArray()
    
    for (month in 1..12) {
        for (i in 1..3) {
            list.add(
                Transaction(
                    id = 123,
                    amount = (1000..5000).random().toDouble(),
                    date = LocalDate.of(2026, month, (1..28).random()),
                    type = types.random()
                )
            )
        }
    }
    return list
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun StatisticsScreenPreview() {
    StatisticsScreen(rememberNavController())
}
