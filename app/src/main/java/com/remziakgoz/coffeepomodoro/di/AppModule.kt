package com.remziakgoz.coffeepomodoro.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.remziakgoz.coffeepomodoro.data.roomdb.UserDatabase
import com.remziakgoz.coffeepomodoro.data.roomdb.UserStatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "userDatabase"
        ).build()
    }

    @Provides
    fun provideUserStatsDao(userDatabase: UserDatabase): UserStatsDao {
        return userDatabase.userStatsDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

}