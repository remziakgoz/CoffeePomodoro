package com.remziakgoz.coffeepomodoro.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.remziakgoz.coffeepomodoro.data.local.dao.PomodoroDao
import com.remziakgoz.coffeepomodoro.data.local.database.AppDatabase
import com.remziakgoz.coffeepomodoro.data.remote.FirebaseDataSource
import com.remziakgoz.coffeepomodoro.data.repository.PomodoroRepository
import com.remziakgoz.coffeepomodoro.domain.usecase.AddTestSessionUseCase
import com.remziakgoz.coffeepomodoro.domain.usecase.GetAchievementsUseCase
import com.remziakgoz.coffeepomodoro.domain.usecase.GetDashboardDataUseCase
import com.remziakgoz.coffeepomodoro.domain.usecase.SavePomodoroSessionUseCase
import com.remziakgoz.coffeepomodoro.domain.usecase.SyncUserDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    // ========== Firebase ==========
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    // ========== Room Database ==========
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "coffee_pomodoro_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun providePomodoroDao(database: AppDatabase): PomodoroDao {
        return database.pomodoroDao()
    }

    // ========== Data Sources ==========
    
    @Provides
    @Singleton
    fun provideFirebaseDataSource(firestore: FirebaseFirestore): FirebaseDataSource {
        return FirebaseDataSource(firestore)
    }

    // ========== Repository ==========
    
    @Provides
    @Singleton
    fun providePomodoroRepository(
        pomodoroDao: PomodoroDao,
        firebaseDataSource: FirebaseDataSource
    ): PomodoroRepository {
        return PomodoroRepository(pomodoroDao, firebaseDataSource)
    }

    // ========== Use Cases ==========
    
    @Provides
    fun provideGetDashboardDataUseCase(
        repository: PomodoroRepository
    ): GetDashboardDataUseCase {
        return GetDashboardDataUseCase(repository)
    }

    @Provides
    fun provideSavePomodoroSessionUseCase(
        repository: PomodoroRepository
    ): SavePomodoroSessionUseCase {
        return SavePomodoroSessionUseCase(repository)
    }

    @Provides
    fun provideGetAchievementsUseCase(
        repository: PomodoroRepository
    ): GetAchievementsUseCase {
        return GetAchievementsUseCase(repository)
    }

    @Provides
    fun provideSyncUserDataUseCase(
        repository: PomodoroRepository
    ): SyncUserDataUseCase {
        return SyncUserDataUseCase(repository)
    }

    @Provides
    fun provideAddTestSessionUseCase(
        savePomodoroSessionUseCase: SavePomodoroSessionUseCase
    ): AddTestSessionUseCase {
        return AddTestSessionUseCase(savePomodoroSessionUseCase)
    }
}