package com.aditya.expensetracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
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
import com.aditya.expensetracker.data.model.Expense
import com.aditya.expensetracker.data.model.ExpenseType
import java.text.SimpleDateFormat
import java.util.*

/**
 * A clean transaction item row with icon, title, date and amount.
 * Matches the white card style in the new design.
 */
@Composable
fun TransactionItem(expense: Expense) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction icon with circular background
            val iconColor = getCategoryColor(expense.category)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(expense.category),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Transaction details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                val formatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                Text(
                    text = formatter.format(expense.date),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Transaction amount
            val prefix = if (expense.type == ExpenseType.INCOME) "+" else "-"
            Text(
                text = "$prefix$${String.format("%.2f", expense.amount)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
    }
}

// Helper to determine icon based on category
private fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "work" -> Icons.Default.Work
        "food", "groceries" -> Icons.Default.ShoppingCart
        else -> Icons.Default.ShoppingCart
    }
}

// Helper to determine color based on category
private fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "work" -> Color(0xFFE91E63) // Pinkish
        "food", "groceries" -> Color(0xFF2196F3) // Blue
        else -> Color(0xFF673AB7) // Purple
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun TransactionItemPreview() {
    TransactionItem(
        expense = Expense(
            title = "Dribbble Pro",
            amount = 145.0,
            date = Date(),
            category = "Work",
            type = ExpenseType.EXPENSE
        )
    )
}
