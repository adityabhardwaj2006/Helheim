package com.aditya.expensetracker.presentation.Screens.utils

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aditya.expensetracker.data.ViewModel.AuthViewModel
import com.aditya.expensetracker.data.ViewModel.PaymentViewModel
import com.aditya.expensetracker.data.ViewModel.TransactionVm
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaving(
    onDismiss : ()->Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel(),
    navController: NavController
) {
    val symbol by paymentViewModel.preferredPaymentMethod.collectAsState()
    val transactionVm : TransactionVm = hiltViewModel()
    val preferredPaymentMethod by paymentViewModel.preferredPaymentMethod.collectAsState()

    var amount by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var savedFor by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>("Other") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(preferredPaymentMethod) {
        if (preferredPaymentMethod != null) {
            paymentMethod = preferredPaymentMethod!!
        }
    }

    ModalBottomSheet(
        tonalElevation = 0.dp,
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ){
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
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
                                scope.launch {
                                    sheetState.hide()
                                    onDismiss()
                                }
                            } else {
                                scope.launch { snackbarHostState.showSnackbar("Invalid amount") }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Add Saving")
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
                    Text("Enter Saving Amount", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(8.dp))
                    BasicTextField(
                        value = amount,
                        onValueChange = { if (it.all(Char::isDigit)) amount = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface),
                        decorationBox = { innerTextField ->
                            if (amount.isEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$symbol 0",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    )
                                }
                            } else {
                                innerTextField()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showDatePicker = true }
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.height(16.dp), tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = dateMillis?.let { convertMillisToDate(it) } ?: "Today", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 13.sp)
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
                    SimpleRow(
                        leftText = category ?: "Other",
                        onClick = { categoryMenuOpen = true }
                    ) {
                        Box {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Choose category",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            DropdownMenu(
                                expanded = categoryMenuOpen,
                                onDismissRequest = { categoryMenuOpen = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 8.dp
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            category = cat
                                            categoryMenuOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                var paymentMenuOpen by remember { mutableStateOf(false) }
                val paymentOptions = listOf("Cash", "Credit Card","Debit Card", "UPI", "Bank Transfer","Phone Pay","Wallet")
                Box {
                    SimpleRow(
                        leftText = "Payment",
                        onClick = { paymentMenuOpen = true }
                    ) {
                        Box {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(paymentMethod, color = MaterialTheme.colorScheme.onSurface)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                            }
                            DropdownMenu(expanded = paymentMenuOpen, onDismissRequest = { paymentMenuOpen = false }) {
                                paymentOptions.forEach { opt ->
                                    DropdownMenuItem(text = { Text(opt, color = MaterialTheme.colorScheme.onSurface) }, onClick = { paymentMethod = opt; paymentMenuOpen = false })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleRow(leftText: String, onClick: () -> Unit, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(leftText, color = MaterialTheme.colorScheme.onSurface)
        trailing()
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
