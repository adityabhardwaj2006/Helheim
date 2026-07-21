package com.aditya.expensetracker.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.aditya.expensetracker.domain.model.Expense
import com.aditya.expensetracker.domain.model.ExpenseType
import com.aditya.expensetracker.presentation.components.BalanceCard
import com.aditya.expensetracker.presentation.components.TransactionItem
import java.util.*

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}
@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Hello,",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            fontSize = 25.sp
        )
        Text(
            text = "Aditya Bhardwaj",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        BalanceCard(
            balance = 12500.0,
            income = 15000.0,
            expense = 2500.0
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            TextButton(onClick = { }) {
                Text("See All")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val dummyExpenses = listOf(
            Expense(1, "Salary", 5000.0, Date(), "Work", ExpenseType.INCOME),
            Expense(2, "Groceries", 150.0, Date(), "Food", ExpenseType.EXPENSE),
            Expense(3, "Rent", 1200.0, Date(), "Housing", ExpenseType.EXPENSE),
            Expense(4, "Freelance", 800.0, Date(), "Work", ExpenseType.INCOME)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(dummyExpenses) { expense ->
                TransactionItem(expense = expense)
            }
        }
    }
}