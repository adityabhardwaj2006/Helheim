package com.aditya.expensetracker.data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aditya.expensetracker.Converters
import com.aditya.expensetracker.data.DAO.NotificationDao
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.DAO.UserDao
import com.aditya.expensetracker.data.model.LocalNotification
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.UserEntity


@Database(
    entities = [UserEntity::class,Transaction::class, LocalNotification::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RoomsDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun userDao() : UserDao
    abstract fun notificationDao(): NotificationDao
}
