package com.aditya.expensetracker.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Privacy Policy",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Box(modifier = Modifier.size(48.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            PolicySection(
                title = "1. Introduction",
                content = "Welcome to Expense Tracker. We value your privacy and are committed to protecting your personal data. This Privacy Policy explains how we collect, use, and safeguard your information when you use our mobile application."
            )

            PolicySection(
                title = "2. Information We Collect",
                content = "• Account Information: When you sign up, we collect your name, email/username, and encrypted password.\n" +
                          "• Financial Data: We collect information about your income, expenses, and savings transactions including amounts, dates, categories, and payment methods.\n" +
                          "• Device Information: We may collect basic device information for crash reporting and app performance optimization."
            )

            PolicySection(
                title = "3. How We Use Your Information",
                content = "• To provide and maintain app functionality.\n" +
                          "• To generate financial statistics and insights.\n" +
                          "• To synchronize your data across devices using our secure cloud backup.\n" +
                          "• To notify you about updates or security alerts."
            )

            PolicySection(
                title = "4. Data Storage and Security",
                content = "Your data is stored locally in a Room database for fast offline access. When connected to the internet, data is synced to our secure Node.js backend. We use industry-standard encryption (Bcrypt) for passwords and secure HTTP (HTTPS) for data transmission."
            )

            PolicySection(
                title = "5. Your Rights",
                content = "You have the right to:\n" +
                          "• Access and update your profile information.\n" +
                          "• Change your account password at any time.\n" +
                          "• Request deletion of your account and all associated transaction history."
            )

            PolicySection(
                title = "6. Changes to This Policy",
                content = "We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page."
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Last Updated: August 20, 2026",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            lineHeight = 22.sp
        )
    }
}
