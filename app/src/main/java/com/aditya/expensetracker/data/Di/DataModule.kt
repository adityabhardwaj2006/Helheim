package com.aditya.expensetracker.data.Di

import android.content.Context
import androidx.room.Room
import com.aditya.expensetracker.data.Api.AuthApi
import com.aditya.expensetracker.data.Api.ChatApi
import com.aditya.expensetracker.data.Api.NotificationApi
import com.aditya.expensetracker.data.Api.TransactionApi
import com.aditya.expensetracker.data.DAO.TransactionDao
import com.aditya.expensetracker.data.DAO.UserDao
import com.aditya.expensetracker.data.Database.RoomsDatabase
import com.aditya.expensetracker.data.repoImpl.AuthRepoImpl
import com.aditya.expensetracker.data.repoImpl.ChatRepoImpl
import com.aditya.expensetracker.data.repoImpl.NotificationRepoImpl
import com.aditya.expensetracker.data.repoImpl.RatingRepoImpl
import com.aditya.expensetracker.data.repoImpl.RepoImpl
import com.aditya.expensetracker.data.repoImpl.TransactionRepoImpl
import com.aditya.expensetracker.domain.repo.AuthRepo
import com.aditya.expensetracker.domain.repo.ChatRepo
import com.aditya.expensetracker.domain.repo.NotificationRepo
import com.aditya.expensetracker.domain.repo.RatingRepo
import com.aditya.expensetracker.domain.repo.Repo
import com.aditya.expensetracker.domain.repo.TransactionRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(
         @ApplicationContext context : Context
    ): RoomsDatabase{

        return Room.databaseBuilder(
            context,
            RoomsDatabase::class.java,
            "transactionDao"
        ).fallbackToDestructiveMigration().build()

    }

    @Provides
    @Singleton
    fun provideDao(
        roomsDatabase : RoomsDatabase
    ) : TransactionDao{
        return roomsDatabase.transactionDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(
        roomsDatabase: RoomsDatabase
    ) : UserDao{
        return roomsDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://expensetrackerbackend-2-rxtw.onrender.com")
            .client(okHttpClient)
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

    @Provides
    @Singleton
    fun provideTransactionApi(
        retrofit: Retrofit
    ) : TransactionApi {
        return retrofit.create(TransactionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApi(
        retrofit: Retrofit
    ): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }


    @Provides
    @Singleton
    fun provideChatApi(
        retrofit: Retrofit
    ) : ChatApi {
        return retrofit.create(
            ChatApi::class.java
        )
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule{

    @Binds
    @Singleton
    abstract fun bindChatRepo(
        chatRepoImpl : ChatRepoImpl
    ) : ChatRepo

    @Binds
    @Singleton
    abstract fun bindAuthRepo(
        authRepoImpl: AuthRepoImpl
    ): AuthRepo

    @Binds
    @Singleton
    abstract fun bindRepo(
        repoImpl: RepoImpl
    ): Repo

    @Binds
    @Singleton
    abstract fun bindsTransactionRepo(
        transactionRepoImpl : TransactionRepoImpl
    ) : TransactionRepo

    @Binds
    @Singleton
    abstract fun bindRatingRepo(
        ratingRepoImpl: RatingRepoImpl
    ) : RatingRepo

    @Binds
    @Singleton
    abstract fun bindNotificationRepo(
        notificationRepoImpl: NotificationRepoImpl
    ): NotificationRepo


}