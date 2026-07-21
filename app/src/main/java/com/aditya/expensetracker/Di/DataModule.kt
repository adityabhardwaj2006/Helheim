package com.aditya.expensetracker.Di

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.Database.TransactionDatabase
import com.aditya.expensetracker.data.RepoImpl
import com.aditya.expensetracker.domain.Repo.Repo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(
         @ApplicationContext context : Context
    ): TransactionDatabase{

        return Room.databaseBuilder(
            context,
            TransactionDatabase::class.java,
            "transactionDao"
        ).build()

    }

    @Provides
    @Singleton
    fun provideDao(
        transactionDatabase : TransactionDatabase
    ) : TransactionDao{
        return transactionDatabase.transactionDao()
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    @Singleton
    abstract fun bindRepo(repoImpl: RepoImpl): Repo
}



