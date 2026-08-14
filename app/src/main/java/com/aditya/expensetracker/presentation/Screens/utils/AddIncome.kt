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
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.AuthViewModel
import com.aditya.expensetracker.data.ViewModel.TransactionVm
import com.aditya.expensetracker.data.ViewModel.TransactionViewModel
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private val GreenDark = Color(0xFF184D3F)
private val RowGray = Color(0xFFF5F5F5)


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncome(
    onDismiss : ()->Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    //Calling repo
    val viewModel : TransactionViewModel = hiltViewModel()
    val userId by authViewModel.userId.collectAsState()
    val transactionVm : TransactionVm = hiltViewModel()
    val addIncomeState by transactionVm.transactionState.collectAsState()

    var amount by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var receivedFrom by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>(null) }
    var paymentMethod by remember { mutableStateOf("Cash") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember{ SnackbarHostState() }
    LaunchedEffect(addIncomeState) {
        when(val state = addIncomeState){
            is ResultState.Success -> {
                viewModel.addTransaction(Transaction(
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    date = dateMillis,
                    type = TransactionType.INCOME.toString(),
                    paidTo = "",
                    receivedFrom = receivedFrom,
                    category = category,
                    paymentMethod = paymentMethod,
                    savedFor = "",
                    userId = userId ?: ""
                ))
                scope.launch{
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
        tonalElevation = 0.dp,
        onDismissRequest = {
            scope.launch{
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ){
        Scaffold(
            bottomBar = {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                    Button(
                        enabled = addIncomeState !is ResultState.Loading,
                        onClick = {
                            val currAmount = amount.toDoubleOrNull()
                            if(currAmount!=null){
                                transactionVm.saveTransaction(
                                    amount = amount.toDouble(),
                                    date = dateMillis,
                                    type = "INCOME",
                                    paidTo = "",
                                    category = category,
                                    savedFor = "",
                                    receivedFrom = receivedFrom,
                                    paymentMethod = paymentMethod
                                )
                            }else{
                                scope.launch {
                                    snackbarHostState.showSnackbar("Invalid amount")
                                }

                            }

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        if(addIncomeState is ResultState.Loading){
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        }else{
                            Text("Add Income") }
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
                    Text("Enter Income Amount", color = Color.Gray)
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
                            color = Color.Black
                        ),
                        decorationBox = { innerTextField ->
                            Row(modifier = Modifier.wrapContentWidth()){
                                if (amount.isEmpty()) {
                                    Text(
                                        "₹ 0",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
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
                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(dateText, color = Color.Gray, fontSize = 13.sp)
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

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = receivedFrom,
                    onValueChange = { receivedFrom = it },
                    label = { Text("Received From") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(5.dp))

                var categoryMenuOpen by remember { mutableStateOf(false) }
                val categories = listOf("Salary", "Freelance", "Investment", "Gift", "Other")
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
                                border = BorderStroke(1.dp, Color.LightGray),
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 8.dp) {
                                categories.forEach {
                                    DropdownMenuItem(
                                        modifier = Modifier.background(Color.Transparent),
                                        text = {
                                            Text(
                                                text = it,
                                                color = Color.Black
                                            ) },
                                        onClick = {
                                            category = it
                                            categoryMenuOpen = false
                                        }, colors = MenuItemColors
                                            (
                                            textColor = Color.Black,
                                            leadingIconColor = Color.Black,
                                            trailingIconColor = Color.Black,
                                            disabledTextColor = Color.Black,
                                            disabledLeadingIconColor = Color.Black,
                                            disabledTrailingIconColor = Color.Black
                                        ))
                                }
                            }
                        }
                    }


                }
                Spacer(modifier = Modifier.height(5.dp))

                var paymentMenuOpen by remember { mutableStateOf(false) }
                val paymentOptions = listOf("Cash", "Card", "UPI", "Bank Transfer")
                Box {
                    SimpleRow(
                        leftText = "Payment"
                    ) {
                        Column()
                        {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { paymentMenuOpen = true }) {
                                Text(paymentMethod)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Change payment method")
                            }
                            DropdownMenu(expanded = paymentMenuOpen, onDismissRequest = { paymentMenuOpen = false }) {
                                paymentOptions.forEach {
                                    DropdownMenuItem(text = { Text(it) }, onClick = {
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
            .background(RowGray, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddIncomePreview() {
    AddIncome(onDismiss = {})
}
