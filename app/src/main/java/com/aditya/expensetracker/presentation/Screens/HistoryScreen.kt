package com.aditya.expensetracker.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.TransactionVm
import com.aditya.expensetracker.data.ViewModel.TransactionViewModel
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.model.TransactionRequest
import com.aditya.expensetracker.data.model.getTransactionResponse
import com.aditya.expensetracker.presentation.components.TransactionHistoryItem

@Composable
fun HistoryScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    navController : NavController,
    transactionVm : TransactionVm = hiltViewModel()
) {
    val state by transactionVm.getTransactionState.collectAsState()
    var transactions: List<TransactionItem>? = null
    LaunchedEffect(Unit) {
        transactionVm.getTransaction()
    }

    Scaffold(
        topBar = {

            // Top Bar
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
                    .fillMaxHeight()
                    , verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center)
                {

                    Text(
                        text = "Transaction History",
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
                transactions = state.data.transactions.distinctBy { it._id ?: it.hashCode() }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = transactions.reversed(),
                        key = { it._id ?: it.hashCode() }
                    ) { transaction ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    transactionVm.deleteTransaction(transaction)
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            modifier = Modifier.animateItem(),
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White
                                    )
                                }
                            },
                            content = {
                                TransactionHistoryItem(transaction = transaction)
                            }
                        )
                    }
                }
            }
            is ResultState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message,
                        fontSize = 18.sp)
                }
            }
            else -> {}
        }


    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun historyPreview(){
    HistoryScreen( hiltViewModel(),rememberNavController())
}