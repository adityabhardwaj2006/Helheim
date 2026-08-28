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

/**
 * A premium credit card-style component to display the total balance.
 * Includes card details, progress bar, and a sleek dark design.
 */
@Composable
fun BalanceCard(
    balance: Double,
    modifier: Modifier = Modifier,
    spendingProgress: Float = 0.4f // Dummy progress for the UI
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1C1E)) // Dark background for premium look
        ) {
            // Subtle decorative circles to mimic the design's background
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = 150.dp.toPx(),
                    center = center.copy(x = size.width * 0.9f, y = size.height * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = 80.dp.toPx(),
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
                            text = "₹ ${String.format("%,.2f", balance)}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Balance",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                }

                // Progress Bar - indicating spending limit or similar
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

                // Bottom section with card number and logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "****  ****  426",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 18.sp,
                        letterSpacing = 2.sp
                    )
                    
                    // Mastercard logo placeholder or actual image
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
