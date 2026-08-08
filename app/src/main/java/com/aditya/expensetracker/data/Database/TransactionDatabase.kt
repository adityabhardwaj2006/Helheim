package com.aditya.expensetracker.data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aditya.expensetracker.Converters
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.model.Transaction


@Database(
    entities = [Transaction::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
}