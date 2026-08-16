package com.aditya.expensetracker.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.AuthViewModel
import com.aditya.expensetracker.data.ViewModel.TransactionVm
import com.aditya.expensetracker.data.model.getTransactionResponse
import com.aditya.expensetracker.data.model.profileResponse
import com.aditya.expensetracker.presentation.components.BalanceCard
import com.aditya.expensetracker.presentation.components.IncomeExpenseSummary
import com.aditya.expensetracker.presentation.components.TransactionItem
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import kotlin.collections.emptyList

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel : AuthViewModel = hiltViewModel(),
    transactionVm : TransactionVm = hiltViewModel()) {
    LaunchedEffect(Unit) {
        authViewModel.getProfile()
        transactionVm.getTransaction()
    }
    val userState : ResultState<profileResponse>? by authViewModel.userState.collectAsState()
    val name = when(val state = userState){
        is ResultState.Loading, ResultState.Idle -> {
            "..."
        }
        is ResultState.Success ->{
            state.data.data?.firstName + " " + state.data.data?.lastName
        }
        else -> {
            "Unknown"
        }
    }

    val transactionState by transactionVm.getTransactionState.collectAsState()
    
    var totalIncome = 0.0
    var totalSavings = 0.0
    var totalExpense = 0.0
    
    var currentIncome = 0.0
    var currentSavings = 0.0
    var currentExpense = 0.0
    
    var prevIncome = 0.0
    var prevSavings = 0.0
    var prevExpense = 0.0

    val currentMonth = YearMonth.now()
    val prevMonth = currentMonth.minusMonths(1)

    var listState = getTransactionResponse(
        success = false,
        transactions = emptyList()
    )
    
    when(val state = transactionState){
        is ResultState.Success -> {
            listState = state.data
            listState.transactions.forEach { trans ->
                val amount = trans.amount
                val type = trans.type.lowercase()
                
                // Total calculation for balance
                when(type){
                    "income" -> totalIncome += amount
                    "expense" -> totalExpense += amount
                    "savings" -> totalSavings += amount
                }
                
                // Month based calculation for growth
                val transDate = Instant.ofEpochMilli(trans.date ?: 0L)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val transYearMonth = YearMonth.from(transDate)
                
                if (transYearMonth == currentMonth) {
                    when(type){
                        "income" -> currentIncome += amount
                        "expense" -> currentExpense += amount
                        "savings" -> currentSavings += amount
                    }
                } else if (transYearMonth == prevMonth) {
                    when(type){
                        "income" -> prevIncome += amount
                        "expense" -> prevExpense += amount
                        "savings" -> prevSavings += amount
                    }
                }
            }
        }
        else -> {}
    }

    fun calculateGrowth(current: Double, previous: Double): String {
        if (previous == 0.0) return if (current > 0) "+100%" else "0%"
        val growth = ((current - previous) / previous) * 100
        val sign = if (growth >= 0) "+" else ""
        return "$sign${growth.toInt()}%"
    }

    val incomeChange = calculateGrowth(currentIncome, prevIncome)
    val expenseChange = calculateGrowth(currentExpense, prevExpense)
    val savingsChange = calculateGrowth(currentSavings, prevSavings)


    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        val isNotification = true
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {

            // 1. Personalized Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome!",
                        color = Color.Gray,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // Notification icon with circular background
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if(isNotification){
                        BadgedBox(
                            badge = {
                                Badge()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    }else{
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = Color.Black
                        )
                    }


                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Premium Balance Card
            BalanceCard(balance = (totalIncome - totalExpense ))

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Transactions Section Title
            Text(
                text = "Transactions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Income & Expense Summary Cards
            IncomeExpenseSummary(
                incomeChange = incomeChange,
                expenseChange = expenseChange,
                savingsChange = savingsChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 100.dp) // Extra padding for bottom navigation
            ) {
                items(listState.transactions.reversed()) { expense ->
                    TransactionItem(expense = expense)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}
