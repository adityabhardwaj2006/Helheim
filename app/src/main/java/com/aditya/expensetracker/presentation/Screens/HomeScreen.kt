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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.data.model.Expense
import com.aditya.expensetracker.data.model.ExpenseType
import com.aditya.expensetracker.presentation.components.BalanceCard
import com.aditya.expensetracker.presentation.components.IncomeExpenseSummary
import com.aditya.expensetracker.presentation.components.TransactionItem
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        val isNotification = true
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = innerPadding.calculateTopPadding()/2)
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
                        text = "Aditya Bhardwaj",
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
            BalanceCard(balance = 5480.00)

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
            IncomeExpenseSummary()

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Transaction List Section
            val dummyExpenses = listOf(
                Expense(1, "Dribbble Pro", 145.0, Date(), "Work", ExpenseType.EXPENSE),
                Expense(2, "Figma", 46.0, Date(), "Work", ExpenseType.EXPENSE),
                Expense(3, "Salary", 5000.0, Date(), "Work", ExpenseType.INCOME),
                Expense(4, "Groceries", 150.0, Date(), "Food", ExpenseType.EXPENSE)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 100.dp) // Extra padding for bottom navigation
            ) {
                items(dummyExpenses) { expense ->
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
