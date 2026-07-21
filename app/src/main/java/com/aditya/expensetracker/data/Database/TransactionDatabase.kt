package com.aditya.expensetracker.data.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aditya.expensetracker.Converters
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.domain.model.Transaction
import kotlin.jvm.java


@Database(
    entities = [Transaction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
}