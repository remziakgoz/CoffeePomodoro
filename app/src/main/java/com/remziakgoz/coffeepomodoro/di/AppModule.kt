package com.remziakgoz.coffeepomodoro.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.data.local.roomdb.UserDatabase
import com.remziakgoz.coffeepomodoro.data.local.roomdb.UserStatsDao
import com.remziakgoz.coffeepomodoro.data.remote.FirebaseUserStatsService
import com.remziakgoz.coffeepomodoro.data.repository.UserStatsRepositoryImpl
import com.remziakgoz.coffeepomodoro.data.sync.DataSyncManager
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import com.remziakgoz.coffeepomodoro.domain.use_cases.BackupUserStatsUseCase
import com.remziakgoz.coffeepomodoro.domain.use_cases.EnsureDateWindowsUseCase
import com.remziakgoz.coffeepomodoro.domain.use_cases.GetUserStatsUseCase
import com.remziakgoz.coffeepomodoro.domain.use_cases.InitializeLocalUserUseCase
import com.remziakgoz.coffeepomodoro.domain.use_cases.RestoreUserStatsUseCase
import com.remziakgoz.coffeepomodoro.domain.use_cases.SyncFirebaseUserUseCase
import com.remziakgoz.coffeepomodoro.domain.use_cases.UpdateUserStatsUseCase
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //#Room
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

    //#Firebase
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseUserStatsService(firestore: FirebaseFirestore): FirebaseUserStatsService {
        return FirebaseUserStatsService(firestore)
    }

    //#SharedPreferences
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("coffee_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePreferenceManager(sharedPreferences: SharedPreferences): PreferenceManager {
        return PreferenceManager(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideUserStatsUseCases(
        repository: UserStatsRepository,
        preferenceManager: PreferenceManager,
        firebaseAuth: FirebaseAuth,
        backupUserStatsUseCase: BackupUserStatsUseCase,
        firebaseUserStatsService: FirebaseUserStatsService,
        ensureDateWindowsUseCase: EnsureDateWindowsUseCase
    ): UserStatsUseCases {
        return UserStatsUseCases(
            getUserStats = GetUserStatsUseCase(repository),
            updateUserStats = UpdateUserStatsUseCase(repository),
            initializeLocalUserUseCase = InitializeLocalUserUseCase(preferenceManager, repository),
            syncFirebaseUser = SyncFirebaseUserUseCase(firebaseAuth, preferenceManager, repository),
            backupUserStats = backupUserStatsUseCase,
            restoreUserStats = RestoreUserStatsUseCase(
                firebaseAuth,
                repository,
                preferenceManager,
                firebaseUserStatsService
            ),
            ensureDateWindowsUseCase = ensureDateWindowsUseCase
        )
    }

    @Provides
    @Singleton
    fun provideUserStatsRepository(
        userStatsDao: UserStatsDao,
        preferenceManager: PreferenceManager
    ): UserStatsRepository {
        return UserStatsRepositoryImpl(userStatsDao)
    }

    @Provides
    @Singleton
    fun provideDataSyncManager(
        auth: FirebaseAuth,
        preferenceManager: PreferenceManager,
        userStatsUseCases: UserStatsUseCases,
    ): DataSyncManager {
        return DataSyncManager(auth, preferenceManager, userStatsUseCases)
    }


}