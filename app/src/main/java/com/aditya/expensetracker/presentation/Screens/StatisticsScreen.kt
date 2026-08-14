package com.aditya.expensetracker.presentation.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.TransactionVm
import com.aditya.expensetracker.data.ViewModel.getTransactionVm
import com.aditya.expensetracker.data.model.ChartType
import com.aditya.expensetracker.data.model.TimeFilter
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionType
import com.aditya.expensetracker.data.model.getTransactionResponse
import com.aditya.expensetracker.presentation.components.StatisticsChart
import com.aditya.expensetracker.presentation.Screens.utils.chart.ChartCalculator
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(
    navController: NavController,
    transactionVm : TransactionVm = hiltViewModel()
) {
    var selectedChartType by remember { mutableStateOf(ChartType.EXPENSE) }
    var selectedTimeFilter by remember { mutableStateOf(TimeFilter.YEAR) }
    val state by transactionVm.getTransactionState.collectAsState()

    LaunchedEffect(Unit) {
        transactionVm.getTransaction()
    }

    Scaffold(
        topBar = {

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.White)
                    .padding(all = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start)
                {

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

                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight()
                    , verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center)
                {

                Text(
                    text = "Statistics",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight()){

                }



            }
        }
    ) {innerPadding->
        when(state){
            is ResultState.Loading -> {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7))
                    .padding(horizontal = 24.dp)
                    .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                }
            }
            is ResultState.Error -> {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7))
                    .padding(horizontal = 24.dp)
                    .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = (state as ResultState.Error).message,
                        fontSize = 18.sp
                    )
                }

            }
            is ResultState.Success -> {
               val response = (state as ResultState.Success<getTransactionResponse>).data
                val transactions = response.transactions

                // Calculate shared max value for consistent scaling across tabs
                val sharedMax = remember(transactions, selectedTimeFilter) {
                    val incomeMax = ChartCalculator.calculateChartPoints(transactions, ChartType.INCOME, selectedTimeFilter, false).maxOfOrNull { it.value } ?: 0f
                    val expenseMax = ChartCalculator.calculateChartPoints(transactions, ChartType.EXPENSE, selectedTimeFilter, false).maxOfOrNull { it.value } ?: 0f
                    val savingsMax = ChartCalculator.calculateChartPoints(transactions, ChartType.SAVINGS, selectedTimeFilter, false).maxOfOrNull { it.value } ?: 0f
                    maxOf(incomeMax, expenseMax, savingsMax)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF7F7F7))
                        .padding(horizontal = 24.dp)
                        .padding(innerPadding)
                ) {
                    // Balance Section (Total of selected type)
                    val totalAmount = remember(selectedChartType, transactions, selectedTimeFilter) {
                        ChartCalculator.calculateChartPoints(transactions, selectedChartType, selectedTimeFilter, cumulative = false)
                            .sumOf { it.value.toDouble() }
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
                            text = "Summary of ${selectedTimeFilter.name.lowercase().replaceFirstChar { it.uppercase() }}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Time Filter (Week, Month, Year)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimeFilterItem(
                            text = "Week",
                            isSelected = selectedTimeFilter == TimeFilter.WEEK,
                            onClick = { selectedTimeFilter = TimeFilter.WEEK },
                            modifier = Modifier.weight(1f)
                        )
                        TimeFilterItem(
                            text = "Month",
                            isSelected = selectedTimeFilter == TimeFilter.MONTH,
                            onClick = { selectedTimeFilter = TimeFilter.MONTH },
                            modifier = Modifier.weight(1f)
                        )
                        TimeFilterItem(
                            text = "Year",
                            isSelected = selectedTimeFilter == TimeFilter.YEAR,
                            onClick = { selectedTimeFilter = TimeFilter.YEAR },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Type Filter (Income, Expense, Savings)
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
                        transactions = transactions,
                        chartType = selectedChartType,
                        timeFilter = selectedTimeFilter,
                        sharedMaxValue = sharedMax,
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

                    // Dynamic Section Title
                    Text(
                        text = when(selectedChartType) {
                            ChartType.INCOME -> "Top Income"
                            ChartType.EXPENSE -> "Top Spending"
                            ChartType.SAVINGS -> "Top Savings"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxSize()
                    ){
                        transactions
                            .filter { it.type == selectedChartType.name }
                            .sortedByDescending { it.amount }
                            .take(3)
                            .forEach { transaction ->
                                val amountPrefix = when(selectedChartType) {
                                    ChartType.INCOME -> "+"
                                    ChartType.EXPENSE -> "-"
                                    ChartType.SAVINGS -> ""
                                }
                                SpendingItem(
                                    title = when(selectedChartType) {
                                        ChartType.INCOME -> transaction.receivedFrom ?: "Unknown"
                                        ChartType.EXPENSE -> transaction.paidTo ?: "Unknown"
                                        ChartType.SAVINGS -> transaction.savedFor ?: "Unknown"
                                    },
                                    date = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(transaction.date?:0L)),
                                    amount = "$amountPrefix₹${String.format(Locale.getDefault(), "%,.2f", transaction.amount)}",
                                    icon = when(selectedChartType) {
                                        ChartType.INCOME -> Icons.AutoMirrored.Filled.TrendingUp
                                        ChartType.EXPENSE -> Icons.Default.ShoppingCart
                                        ChartType.SAVINGS -> Icons.Default.Savings
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                    }


                }


            }

            else -> {

            }
        }

        }






}

@Composable
fun TimeFilterItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        color = if (isSelected) Color(0xFFEEEEEE) else Color.Transparent,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) Color.Black else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            )
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
fun SpendingItem(title: String, date: String, amount: String, icon: ImageVector) {
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


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun StatisticsScreenPreview() {
    StatisticsScreen(rememberNavController())
}
