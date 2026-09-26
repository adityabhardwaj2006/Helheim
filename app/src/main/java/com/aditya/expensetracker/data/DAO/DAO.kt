package com.aditya.expensetracker.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aditya.expensetracker.data.model.Transaction
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransaction()

    @Query("SELECT * FROM 'transactions' WHERE userId = :userId")
    fun getAllTransaction(userId: String) : Flow<List<Transaction>>

    @Query("SELECT * FROM 'transactions' WHERE isSynced = 0 AND userId = :userId")
    fun getUnsyncedTransaction(userId: String) : Flow<List<Transaction>>

}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: com.aditya.expensetracker.data.model.LocalNotification)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY date DESC")
    fun getAllNotifications(userId: String): Flow<List<com.aditya.expensetracker.data.model.LocalNotification>>

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Int)

    @Query("SELECT * FROM notifications WHERE type = :type AND userId = :userId LIMIT 1")
    suspend fun getNotificationByType(type: String, userId: String): com.aditya.expensetracker.data.model.LocalNotification?

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()


}

