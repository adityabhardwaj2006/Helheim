package com.aditya.expensetracker.presentation.Screens.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.AuthViewModel
import com.aditya.expensetracker.data.ViewModel.TransactionVm
import com.aditya.expensetracker.data.ViewModel.TransactionViewModel
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val GreenDark = Color(0xFF184D3F)
private val RowGray = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaving(
    onDismiss : ()->Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val viewModel : TransactionViewModel = hiltViewModel()
    val transactionVm : TransactionVm = hiltViewModel()
    val userId by authViewModel.userId.collectAsState()
    val addSavingState by transactionVm.transactionState.collectAsState()

    var amount by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var savedFor by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>(null) }
    var paymentMethod by remember { mutableStateOf("Cash") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(addSavingState) {
        when(val state = addSavingState){
            is ResultState.Success -> {
                viewModel.addTransaction(Transaction(
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    date = dateMillis,
                    type = TransactionType.SAVINGS.toString(),
                    paidTo = "",
                    savedFor = savedFor,
                    category = category,
                    paymentMethod = paymentMethod,
                    receivedFrom = "",
                    userId = userId ?: ""
                ))
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            }
            is ResultState.Error -> {
                snackbarHostState.showSnackbar(state.message)
            }
            else -> {}
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ){
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        enabled = addSavingState !is ResultState.Loading,
                        onClick = {
                            val currAmount = amount.toDoubleOrNull()
                            if(currAmount != null){
                                transactionVm.saveTransaction(
                                    amount = currAmount,
                                    date = dateMillis,
                                    type = "SAVINGS",
                                    paidTo = "",
                                    category = category,
                                    savedFor = savedFor,
                                    receivedFrom = "",
                                    paymentMethod = paymentMethod
                                )
                            } else {
                                scope.launch { snackbarHostState.showSnackbar("Invalid amount") }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        if(addSavingState is ResultState.Loading){
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Add Saving")
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var showDatePicker by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Enter Saving Amount", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    BasicTextField(
                        value = amount,
                        onValueChange = { if (it.all(Char::isDigit)) amount = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Color.Black),
                        decorationBox = { innerTextField ->
                            Row(modifier = Modifier.wrapContentWidth()){
                                if (amount.isEmpty()) {
                                    Text("₹ 0", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center)
                                } else {
                                    innerTextField()
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showDatePicker = true }
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = dateMillis?.let { convertMillisToDate(it) } ?: "Today", color = Color.Gray, fontSize = 13.sp)
                    }
                }

                if (showDatePicker) {
                    val state = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                dateMillis = state.selectedDateMillis
                                showDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = state, showModeToggle = false)
                    }
                }

                OutlinedTextField(
                    value = savedFor,
                    onValueChange = { savedFor = it },
                    label = { Text("Saved For") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                var categoryMenuOpen by remember { mutableStateOf(false) }
                val categories = listOf("Emergency Fund", "Retirement", "Vacation", "Car", "Other")
                Box {
                    SimpleRow(leftText = category ?: "Category") {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.clickable { categoryMenuOpen = true })
                        DropdownMenu(expanded = categoryMenuOpen, onDismissRequest = { categoryMenuOpen = false }) {
                            categories.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; categoryMenuOpen = false })
                            }
                        }
                    }
                }

                var paymentMenuOpen by remember { mutableStateOf(false) }
                val paymentOptions = listOf("Cash", "Card", "UPI", "Bank Transfer")
                Box {
                    SimpleRow(leftText = "Payment") {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { paymentMenuOpen = true }) {
                            Text(paymentMethod)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }
                        DropdownMenu(expanded = paymentMenuOpen, onDismissRequest = { paymentMenuOpen = false }) {
                            paymentOptions.forEach { opt ->
                                DropdownMenuItem(text = { Text(opt) }, onClick = { paymentMethod = opt; paymentMenuOpen = false })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleRow(leftText: String, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(RowGray, RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(leftText, color = Color.Black)
        trailing()
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
