package com.aditya.expensetracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.model.getTransactionResponse

/**
 * A component that displays two summary cards for Income and Expense.
 * Each card shows an icon, percentage/value, and a label.
 */
@Composable
fun IncomeExpenseSummary(
    modifier: Modifier = Modifier,
    incomeChange: String = "0%",
    expenseChange: String = "0%",
    savingsChange: String = "0%"
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Income Summary
        val incomeIsPositive = !incomeChange.startsWith("-")
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = if (incomeIsPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
            iconColor = if (incomeIsPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
            value = incomeChange,
            label = "Income"
        )

        // Expense Summary
        val expenseIsIncreased = expenseChange.startsWith("+")
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = if (expenseIsIncreased) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
            iconColor = if (expenseIsIncreased) Color(0xFFF44336) else Color(0xFF4CAF50), // Red if expenses go up
            value = expenseChange,
            label = "Expense"
        )

        // Savings Summary
        val savingsIsPositive = !savingsChange.startsWith("-")
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = if (savingsIsPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
            iconColor = if (savingsIsPositive) Color(0xFF2196F3) else Color(0xFFF44336), // Blue for savings
            value = savingsChange,
            label = "Savings"
        )
    }
}

@Composable
private fun SummaryCard(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Circular icon background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = value,
                    color = iconColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = label,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun IncomeExpenseSummaryPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        IncomeExpenseSummary(incomeChange = "+15%", savingsChange = "+5%", expenseChange = "-2%")
    }
}
