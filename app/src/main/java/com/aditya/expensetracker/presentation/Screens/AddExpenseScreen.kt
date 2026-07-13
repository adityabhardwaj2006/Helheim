package com.aditya.expensetracker.presentation.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aditya.expensetracker.domain.model.ExpenseType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(onBackClick: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ExpenseType.EXPENSE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Type", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilterChip(
                    selected = type == ExpenseType.EXPENSE,
                    onClick = { type = ExpenseType.EXPENSE },
                    label = { Text("Expense") }
                )
                FilterChip(
                    selected = type == ExpenseType.INCOME,
                    onClick = { type = ExpenseType.INCOME },
                    label = { Text("Income") }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* TODO: Save */ },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save Transaction")
            }
        }
    }
}
