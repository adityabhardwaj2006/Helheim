package com.aditya.expensetracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionType
import com.aditya.expensetracker.ui.theme.GrayDark
import com.aditya.expensetracker.ui.theme.GrayMedium
import com.aditya.expensetracker.ui.theme.responsiveSp
import java.text.SimpleDateFormat
import java.util.*

/**
 * A clean transaction item row with icon, title, date and amount.
 * Matches the theme colors and uses responsive typography.
 */
@Composable
fun TransactionItem(expense: TransactionItem,symbol : String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction icon with circular background
            val iconColor = MaterialTheme.colorScheme.onSurface
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(expense.type),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Transaction details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category?:"",
                    fontSize = responsiveSp(16),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val formatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                Text(
                    text = formatter.format(Date(expense.date ?: 0L)),
                    fontSize = responsiveSp(12),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            // Transaction amount
            val prefix = if (expense.type == TransactionType.INCOME.name) "+"
            else if (expense.type == TransactionType.EXPENSE.name)"-" else
                "•"
            Text(
                text = "$symbol${String.format("%.2f", expense.amount)}",
                fontSize = responsiveSp(16),
                fontWeight = FontWeight.ExtraBold,
                color = if (expense.type == TransactionType.INCOME.name) Color(0xFF4CAF50) 
                        else if (expense.type == TransactionType.EXPENSE.name) Color(0xFFF44336)
                        else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getCategoryIcon(type: String): ImageVector{
    return when (type.lowercase()) {
        "income" -> Icons.AutoMirrored.Filled.TrendingUp
        "expense" -> Icons.Default.ShoppingCart
        "savings" -> Icons.Default.Savings
        else -> {
            Icons.Default.ShoppingCart
        }
    }
}

// Helper to determine color based on category
private fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "work" -> GrayDark
        "food", "groceries" -> GrayMedium
        else -> GrayMedium
    }
}

