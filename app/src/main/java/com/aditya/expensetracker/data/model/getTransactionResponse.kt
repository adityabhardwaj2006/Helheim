package com.aditya.expensetracker.data.model

data class getTransactionResponse(
    val success : Boolean,
    val transactions : List<TransactionItem>,
)
data class TransactionItem(
    val id : Int?,
    val amount : Double,
    val date : Long?,
    val type : String,
    val paidTo : String,
    val category : String?,
    val savedFor : String,
    val receivedFrom : String,
    val paymentMethod : String
)