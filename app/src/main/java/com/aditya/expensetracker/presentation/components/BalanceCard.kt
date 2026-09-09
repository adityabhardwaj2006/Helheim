package com.aditya.expensetracker.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import com.aditya.expensetracker.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aditya.expensetracker.ui.theme.responsiveSp
import java.util.Locale

/**
 * A premium credit card-style component to display the total balance.
 * Includes card details, progress bar, and a sleek dark design.
 */
@Composable
fun BalanceCard(
    balance: Double,
    modifier: Modifier = Modifier,
    spendingProgress: Float = 0.4f,
    symbol: String = "₹"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1C1E)) // Keep premium dark background
        ) {
            // Subtle decorative circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = 150.dp.toPx(),
                    center = center.copy(x = size.width * 0.9f, y = size.height * 0.2f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header with balance and menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "$symbol ${String.format(Locale.getDefault(), "%,.2f", balance)}",
                            color = Color.White,
                            fontSize = responsiveSp(32),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Balance",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = responsiveSp(14)
                        )
                    }

                }

                // Progress Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = { spendingProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFF9A825), // Yellow/Gold color
                        trackColor = Color.White.copy(alpha = 0.1f),
                        strokeCap = StrokeCap.Round
                    )
                }

                // Bottom section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "****  ****  426",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = responsiveSp(18),
                        letterSpacing = 2.sp
                    )
                    
                    Icon(
                        painter = painterResource(id = R.drawable.mastercard),
                        contentDescription = "Mastercard",
                        tint = Color.Unspecified,
                        modifier = Modifier.height(30.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BalanceCardPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        BalanceCard(balance = 5480.00)
    }
}
