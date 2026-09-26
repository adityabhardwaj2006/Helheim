package com.aditya.expensetracker.presentation.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aditya.expensetracker.ui.theme.responsiveSp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.PaymentViewModel
import com.aditya.expensetracker.data.ViewModel.TransactionVm
import com.aditya.expensetracker.data.ViewModel.getTransactionVm
import com.aditya.expensetracker.data.model.ChartType
import com.aditya.expensetracker.data.model.TimeFilter
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionType
import com.aditya.expensetracker.data.model.getTransactionResponse
import com.aditya.expensetracker.presentation.components.ChatBubble
import com.aditya.expensetracker.presentation.components.StatisticsChart
import com.aditya.expensetracker.presentation.Screens.utils.chart.ChartCalculator
import com.aditya.expensetracker.presentation.components.shimmer
import com.aditya.expensetracker.presentation.navigation.Routes
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlin.toString

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(
    navController: NavController,
    transactionVm : TransactionVm,
    paymentViewModel: PaymentViewModel = hiltViewModel()
) {
    var selectedChartType by remember { mutableStateOf(ChartType.EXPENSE) }
    var selectedTimeFilter by remember { mutableStateOf(TimeFilter.YEAR) }
    val state by transactionVm.getTransactionState.collectAsState()

    val symbol by paymentViewModel.preferredPaymentMethod.collectAsState()

    LaunchedEffect(Unit) {
        transactionVm.getTransaction()
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            val isScrollingDown by remember {
                derivedStateOf {
                    scrollState.isScrollInProgress && scrollState.value > 0
                }
            }
            AnimatedVisibility(
                visible = !isScrollingDown,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                ChatBubble(
                    onClick = {
                        navController.navigate(Routes.Chat)
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        },
        topBar = {
            // Top Bar - Flexible height with status bar padding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Statistics",
                    fontSize = responsiveSp(22),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Box(modifier = Modifier.size(48.dp))
            }
        }
    ) {innerPadding->
        val resultState = state
        when(resultState){
            is ResultState.Loading -> {
                ShimmerStatisticsScreen(innerPadding)
            }
            is ResultState.Error -> {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp)
                    .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = resultState.message,
                        fontSize = responsiveSp(18),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { transactionVm.getTransaction() }) {
                        Text("Retry")
                    }
                }

            }
            is ResultState.Success -> {
               val response = resultState.data
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
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp)
                ) {
                    // Balance Section (Total of selected type)
                    val totalAmount = remember(selectedChartType, transactions, selectedTimeFilter) {
                        ChartCalculator.calculateChartPoints(transactions, selectedChartType, selectedTimeFilter, cumulative = false)
                            .sumOf { it.value.toDouble() }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$symbol${String.format(Locale.getDefault(), "%,.2f", totalAmount)}",
                            fontSize = responsiveSp(32),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Summary of ${selectedTimeFilter.name.lowercase().replaceFirstChar { it.uppercase() }}",
                            fontSize = responsiveSp(14),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Time Filter (Week, Month, Year)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)),
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
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp)),
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
                        },
                        symbol = symbol
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Dynamic Section Title
                    Text(
                        text = when(selectedChartType) {
                            ChartType.INCOME -> "Top Income"
                            ChartType.EXPENSE -> "Top Spending"
                            ChartType.SAVINGS -> "Top Savings"
                        },
                        fontSize = responsiveSp(20),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        transactions
                            .filter { it.type == selectedChartType.name }
                            .sortedByDescending { it.amount }
                            .take(5) // Increased to show more if needed
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
                                    amount = "$amountPrefix $symbol${String.format(Locale.getDefault(), "%,.2f", transaction.amount)}",
                                    icon = when(selectedChartType) {
                                        ChartType.INCOME -> Icons.AutoMirrored.Filled.TrendingUp
                                        ChartType.EXPENSE -> Icons.Default.ShoppingCart
                                        ChartType.SAVINGS -> Icons.Default.Savings
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                    }
                    Spacer(modifier = Modifier.height(24.dp))


                }


            }

            else -> {

            }
        }

        }






}

@Composable
fun ShimmerStatisticsScreen(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .shimmer()
            .padding(horizontal = 24.dp)
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmer()
        )
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
        color = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = responsiveSp(13)
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
        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = responsiveSp(14)
            )
        }
    }
}

@Composable
fun SpendingItem(title: String, date: String, amount: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = responsiveSp(16), color = MaterialTheme.colorScheme.onSurface)
            Text(text = date, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = responsiveSp(12))
        }
        Text(text = amount, fontWeight = FontWeight.ExtraBold, fontSize = responsiveSp(16), color = MaterialTheme.colorScheme.onSurface)
    }
}

