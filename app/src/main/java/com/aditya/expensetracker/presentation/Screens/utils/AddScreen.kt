package com.aditya.expensetracker.presentation.Screens.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.AuthViewModel
import com.aditya.expensetracker.data.ViewModel.PaymentViewModel
import com.aditya.expensetracker.data.ViewModel.TransactionVm
import com.aditya.expensetracker.data.model.TransactionType
import com.aditya.expensetracker.presentation.components.shimmer
import androidx.compose.ui.draw.clip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Colors picked to match the design
private val GreenDark = Color(0xFF184D3F)
private val RowGray = Color(0xFFF5F5F5)


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onDismiss : ()->Unit,
    transactionVm: TransactionVm = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel()
) {

    //Calling repo
    val addScreenState by transactionVm.transactionState.collectAsState()
    val preferredPaymentMethod by paymentViewModel.preferredPaymentMethod.collectAsState()
    val snackbarHostState = remember{ SnackbarHostState() }



    var amount by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var paidTo by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>(null) }
    var paymentMethod by remember { mutableStateOf("Cash") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(preferredPaymentMethod) {
        if (preferredPaymentMethod != null) {
            paymentMethod = preferredPaymentMethod!!
        }
    }


    LaunchedEffect(addScreenState) {
        when(val state = addScreenState){
            is ResultState.Success -> {
                if (amount.isNotEmpty()) {
                    transactionVm.resetTransactionState()
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }
            }
            is ResultState.Error -> {
                snackbarHostState.showSnackbar("${state.message}")
                transactionVm.resetTransactionState()
            }
            else -> {}
        }
    }


        ModalBottomSheet(
            tonalElevation = 0.dp,
            onDismissRequest = {
                scope.launch{
                    transactionVm.resetTransactionState()
                    sheetState.hide()
                    onDismiss()
                }
            },
            sheetState = sheetState,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ){
            Scaffold(
                containerColor = MaterialTheme.colorScheme.surface,
                snackbarHost = {SnackbarHost(snackbarHostState)},
                bottomBar = {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)) {
                        Button(
                            onClick = {
                                val amountVal = amount.toDoubleOrNull()
                                if(amountVal != null){
                                    transactionVm.saveTransaction(
                                        amount = amountVal,
                                        date = dateMillis,
                                        type = "EXPENSE",
                                        paidTo = paidTo,
                                        category = category,
                                        savedFor = "",
                                        receivedFrom = "",
                                        paymentMethod = paymentMethod
                                    )
                                }else{
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Invalid amount")
                                    }
                                }
                            },
                            enabled = addScreenState !is ResultState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            if(addScreenState is ResultState.Loading){
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            }else{
                                Text("Add Expense") }
                            }

                    }
                }
            ) {innerPadding->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    var showDatePicker by remember { mutableStateOf(false) }
                    val dateText = dateMillis?.let { convertMillisToDate(it) } ?: "Today"
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Enter Spent Amount", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()){

                        }
                        BasicTextField(
                            value = amount,
                            onValueChange = {
                                if (it.all(Char::isDigit)) amount = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            decorationBox = { innerTextField ->
                                Row(modifier = Modifier.wrapContentWidth()){
                                    if (amount.isEmpty()) {
                                        Text(
                                            "₹ 0",
                                            fontSize = 40.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                            textAlign = TextAlign.Center
                                        )
                                    }else{
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
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.height(16.dp), tint = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dateMillis?.let { convertMillisToDate(it) } ?: "Today",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (showDatePicker) {
                        val state = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    state.selectedDateMillis?.let {
                                        dateMillis = it
                                    }
                                    showDatePicker = false
                                })
                                {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                            }
                        ) {
                            DatePicker(state = state, showModeToggle = false)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = paidTo,
                        onValueChange = { paidTo = it },
                        label = { Text("Paid To") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    var categoryMenuOpen by remember { mutableStateOf(false) }
                    val categories = listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Health", "Other")
                    Box {
                        SimpleRow(
                            leftText = category ?: "Category"
                        ) {
                            Column() {
                                Icon(
                                    Icons.Default.Add, contentDescription = "Choose category",
                                    modifier = Modifier.clickable {
                                        categoryMenuOpen = true
                                    },
                                )
                                DropdownMenu(expanded = categoryMenuOpen,
                                    onDismissRequest = { categoryMenuOpen = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(12.dp),
                                    shadowElevation = 8.dp) {
                                    categories.forEach {
                                        DropdownMenuItem(
                                            modifier = Modifier.background(Color.Transparent),
                                            text = {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ) },
                                            onClick = {
                                            category = it
                                            categoryMenuOpen = false
                                        }, colors = MenuItemColors
                                                (
                                                textColor = MaterialTheme.colorScheme.onSurface,
                                                leadingIconColor = MaterialTheme.colorScheme.onSurface,
                                                trailingIconColor = MaterialTheme.colorScheme.onSurface,
                                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                                            ))
                                    }
                                }
                            }
                        }


                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    var paymentMenuOpen by remember { mutableStateOf(false) }
                    val paymentOptions = listOf("Cash", "Credit Card","Debit Card", "UPI", "Bank Transfer","Phone Pay","Wallet")
                    Box {
                        SimpleRow(
                            leftText = "Payment"
                        ) {
                            Column()
                            {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { paymentMenuOpen = true }) {
                                    Text(paymentMethod, color = MaterialTheme.colorScheme.onSurface)
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Change payment method", tint = MaterialTheme.colorScheme.onSurface)
                                }
                                DropdownMenu(expanded = paymentMenuOpen, onDismissRequest = { paymentMenuOpen = false }) {
                                    paymentOptions.forEach {
                                        DropdownMenuItem(text = { Text(it, color = MaterialTheme.colorScheme.onSurface) }, onClick = {
                                            paymentMethod = it
                                            paymentMenuOpen = false
                                        })
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
private fun SimpleRow(
    leftText: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(leftText, color = MaterialTheme.colorScheme.onSurface)
        trailing()
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MMMM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddScreenPreview() {
    AddScreen(onDismiss = {})
}
