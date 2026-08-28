package com.aditya.expensetracker.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AboutAppScreen(navController: NavController) {
    Scaffold(
        containerColor = Color(0xFFF7F7F9),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.White)
                    .padding(all = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start)
                {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center)
                {
                    Text(
                        text = "About App",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight()){
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Icon Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF184D3F), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Expense Tracker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Version 1.0.0",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Track your finances with ease. Our app helps you manage your income, expenses, and savings in one place, providing insightful statistics to help you reach your financial goals.",
                fontSize = 16.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(24.dp))

            AboutSection(title = "Key Features", content = "• Real-time balance tracking\n• Category-wise transaction management\n• Interactive financial charts\n• Secure data synchronization\n• Easy profile management")

            AboutSection(title = "Technologies Used", content = "Built with modern Android technologies including Jetpack Compose, Hilt Dependency Injection, Room Database for offline storage, and a secure Node.js backend.")

            AboutSection(title = "Developer", content = "Developed by Aditya Bhardwaj. Focused on providing a clean and premium user experience for financial management.")

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "© 2026 Expense Tracker App",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AboutSection(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 15.sp,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )
    }
}
