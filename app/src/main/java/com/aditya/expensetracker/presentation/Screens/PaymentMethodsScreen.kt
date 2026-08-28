package com.aditya.expensetracker.presentation.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aditya.expensetracker.data.ViewModel.PaymentViewModel

@Composable
fun PaymentMethodsScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val preferredMethod by viewModel.preferredPaymentMethod.collectAsState()

    val paymentMethods = listOf(
        PaymentOption("Cash", Icons.Default.Payments),
        PaymentOption("Credit Card", Icons.Default.CreditCard),
        PaymentOption("Debit Card", Icons.Default.CreditCard),
        PaymentOption("UPI", Icons.Default.QrCodeScanner),
        PaymentOption("Bank Transfer", Icons.Default.AccountBalance),
        PaymentOption("Phone Pay", Icons.Default.Payments),
        PaymentOption("Wallet", Icons.Default.AccountBalanceWallet)
    )

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
                        text = "Payment Methods",
                        fontSize = 20.sp,
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Select Preferred Method",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            paymentMethods.forEach { method ->
                PaymentMethodCard(
                    option = method,
                    isSelected = preferredMethod == method.name,
                    onClick = {
                        viewModel.updatePaymentMethod(method.name)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class PaymentOption(val name: String, val icon: ImageVector)

@Composable
fun PaymentMethodCard(
    option: PaymentOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF184D3F).copy(alpha = 0.05f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF184D3F)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (isSelected) Color(0xFF184D3F) else Color(0xFFF0F0F0),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = option.name,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 16.sp,
                color = if (isSelected) Color(0xFF184D3F) else Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF184D3F),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
