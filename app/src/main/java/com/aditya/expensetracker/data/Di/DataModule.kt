package com.aditya.expensetracker.data.Di

import android.content.Context
import androidx.room.Room
import com.aditya.expensetracker.data.Api.AuthApi
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.Database.TransactionDatabase
import com.aditya.expensetracker.data.repoImpl.RepoImpl
import com.aditya.expensetracker.domain.repo.Repo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit : Retrofit
    ): AuthApi{
        return retrofit.create(AuthApi::class.java)
    }

    @Binds
    @Singleton
    fun bindRepo(
        repoImpl: RepoImpl
    ): Repo {
        return repoImpl
    }


}



