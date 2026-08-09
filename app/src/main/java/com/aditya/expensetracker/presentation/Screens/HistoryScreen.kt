package com.aditya.expensetracker.presentation.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.getTransactionVm
import com.aditya.expensetracker.data.ViewModel.transactionViewModel
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionRequest
import com.aditya.expensetracker.data.model.getTransactionResponse
import com.aditya.expensetracker.presentation.components.TransactionHistoryItem

@Composable
fun HistoryScreen(
    viewModel: transactionViewModel = hiltViewModel(),
    getTransactionVm : getTransactionVm = hiltViewModel()
) {
    val state by getTransactionVm.transactionState.collectAsState()
    var transactions: List<TransactionItem>? = null
    LaunchedEffect(Unit) {
        getTransactionVm.getTransaction()
    }
    when(val state = state){

        is ResultState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
        }
        is ResultState.Success -> {
            transactions = state.data.transactions
        }
        else -> {}
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Transaction History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        if (transactions == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No transactions found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(transactions.reversed()) { transaction ->
                    TransactionHistoryItem(transaction = transaction)
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun historyPreview(){
    HistoryScreen(hiltViewModel())
}