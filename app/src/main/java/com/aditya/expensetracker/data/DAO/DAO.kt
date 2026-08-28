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

}

