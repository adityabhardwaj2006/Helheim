package com.aditya.expensetracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aditya.expensetracker.domain.model.Expense
import com.aditya.expensetracker.domain.model.ExpenseType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = if (expense.type == ExpenseType.INCOME) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
        val iconColor = if (expense.type == ExpenseType.INCOME) Color(0xFF4CAF50) else Color(0xFFF44336)
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = expense.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            val prefix = if (expense.type == ExpenseType.INCOME) "+" else "-"
            Text(
                text = "$prefix$${String.format("%.2f", expense.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (expense.type == ExpenseType.INCOME) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            Text(
                text = formatter.format(expense.date),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionItemPreview() {
    TransactionItem(
        expense = Expense(
            title = "Groceries",
            amount = 50.0,
            date = Date(),
            category = "Food",
            type = ExpenseType.EXPENSE
        )
    )
}
