package com.aditya.expensetracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionType
import com.aditya.expensetracker.ui.theme.responsiveSp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistoryItem(transaction: TransactionItem,symbol : String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (icon, color) = getTransactionInfo(transaction.type)
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                val title = when (transaction.type) {
                    "INCOME" -> "Income from ${transaction.receivedFrom}"
                    "EXPENSE" -> "Paid to ${transaction.paidTo}"
                    "SAVINGS" -> "Saved for ${transaction.savedFor}"
                    else -> {"Income from ${transaction.receivedFrom}"}
                }
                
                Text(
                    text = title,
                    fontSize = responsiveSp(16),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                val dateStr = transaction.date?.let {
                    SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(it))
                } ?: ""
                
                Text(
                    text = "${transaction.category ?: "No Category"} • $dateStr",
                    fontSize = responsiveSp(12),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            val prefix = when (transaction.type) {
                "INCOME" -> "+"
                "EXPENSE" -> "-"
                "SAVINGS" -> "•"
                else -> ""
            }
            
            Text(
                text = "$prefix $symbol${String.format(Locale.getDefault(), "%.2f", transaction.amount)}",
                fontSize = responsiveSp(16),
                fontWeight = FontWeight.ExtraBold,
                color = if (transaction.type == "INCOME") Color(0xFF4CAF50) 
                        else if (transaction.type == "EXPENSE") Color(0xFFF44336)
                        else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getTransactionInfo(type: String): Pair<ImageVector, Color> {
    return when (type) {
        "INCOME" -> Icons.AutoMirrored.Filled.TrendingUp to Color(0xFF4CAF50)
        "EXPENSE" -> Icons.Default.ShoppingCart to Color(0xFFF44336)
        "SAVINGS" -> Icons.Default.Savings to Color(0xFF2196F3)
        else -> {
            Icons.Default.ShoppingCart to Color(0xFFF44336)
        }
    }
}
