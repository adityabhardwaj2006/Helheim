package com.aditya.expensetracker.presentation.Screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aditya.expensetracker.ui.theme.responsiveSp
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
import com.aditya.expensetracker.presentation.components.ChatBubble
import com.aditya.expensetracker.presentation.components.TransactionHistoryItem
import com.aditya.expensetracker.presentation.components.shimmer
import com.aditya.expensetracker.presentation.navigation.Routes

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

    val historyListState = rememberLazyListState()
    val isScrollingDown by remember {
        derivedStateOf {
            historyListState.isScrollInProgress && historyListState.firstVisibleItemIndex > 0
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isScrollingDown,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                ChatBubble(
                    onClick = {
                        navController.navigate(Routes.Chat)
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        },
        topBar = {

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
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
                    text = "Transaction History",
                    fontSize = responsiveSp(20),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Spacer to balance the back button
                Box(modifier = Modifier.size(48.dp))
            }
        }
    ) { innerPadding ->

        when(val state = state){

            is ResultState.Loading -> {
                LazyColumn(
                    state = historyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(10) {
                        ShimmerHistoryItem()
                    }
                }
            }
            is ResultState.Success -> {
                transactions = state.data.transactions.distinctBy { it._id ?: it.localId }
                LazyColumn(
                    state = historyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = transactions.reversed(),
                        key = { it._id ?: it.localId ?: it.hashCode() }
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
                                if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                    val color = Color.Red
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

@Composable
fun ShimmerHistoryItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .shimmer()
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun historyPreview(){
    HistoryScreen( hiltViewModel(),rememberNavController())
}