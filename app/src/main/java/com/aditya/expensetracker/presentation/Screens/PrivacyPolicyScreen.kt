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
            Text(
                text = "Last Updated: September 13, 2026",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "HelHeim is your intelligent financial companion, designed to simplify expense tracking with AI-driven insights and automated features. We are committed to ensuring your financial data is handled with the highest level of privacy and security.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            PolicySection(
                title = "1. Information We Collect",
                content = "To provide a seamless experience, we collect the following:\n\n" +
                          "• Account Information: Email, Name, and Profile Picture to sync your data across devices.\n" +
                          "• Financial Records: Transaction amounts, categories, dates, and payment methods you enter or that are auto-detected.\n" +
                          "• AI Interactions: Messages sent to our AI chatbot to help categorize expenses or provide financial advice."
            )

            PolicySection(
                title = "2. Permissions & Automation",
                content = "• Notification Listener Service: HelHeim requests permission to read notifications. This is used exclusively to detect transaction alerts from banking and payment apps (e.g., Google Pay, PhonePe, SMS) to auto-fill your expense logs. We do not store non-financial notification data.\n\n" +
                          "• Internet Access: Required to sync your data to our secure servers and to communicate with the AI service."
            )

            PolicySection(
                title = "3. Data Storage & Security",
                content = "• Local Storage: All transactions are stored locally on your device for fast, offline access.\n" +
                          "• Cloud Sync: Data is securely transmitted via HTTPS and stored on our protected servers for multi-device access.\n" +
                          "• Encryption: Sensitive information and passwords are encrypted using industry-standard protocols."
            )

            PolicySection(
                title = "4. Third-Party Services",
                content = "We do not sell your personal or financial data. We only interact with trusted services like Google (for authentication) and secure AI providers for processing chatbot queries."
            )

            PolicySection(
                title = "5. Children's Privacy",
                content = "Our application is not intended for anyone under the age of 13. We do not knowingly collect personal information from children."
            )

            PolicySection(
                title = "6. Contact Us",
                content = "If you have any questions about this Privacy Policy, please contact us at:\n" +
                          "Email: adityabhardwaj2032@gmail.com"
            )

            Spacer(modifier = Modifier.height(24.dp))
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
