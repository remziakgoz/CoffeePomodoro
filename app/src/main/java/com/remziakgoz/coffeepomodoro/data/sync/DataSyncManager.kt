package com.remziakgoz.coffeepomodoro.data.sync

import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.data.remote.FirebaseUserStatsService
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataSyncManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val userStatsUseCases: UserStatsUseCases,
    private val firebaseService: FirebaseUserStatsService
) {
    companion object {
        private const val TAG = "DataSyncManager"
    }

    suspend fun performInitialSync() {
        Log.d(TAG, "🚀 STARTING Initial Sync Process")
        
        // Step 1: Check Firebase user first
        val firebaseUid = auth.currentUser?.uid ?: run {
            Log.d(TAG, "❌ No Firebase user found, performing offline initialization")
            userStatsUseCases.initializeLocalUserUseCase()
            return
        }

        Log.d(TAG, "1️⃣ Firebase user detected: $firebaseUid")
        
        // Step 2: Fetch Firebase data FIRST (before any local operations)
        Log.d(TAG, "2️⃣ PRIORITY: Fetching Firebase data first...")
        val firebaseUserStats = firebaseService.fetchUserStats(firebaseUid)
        
        if (firebaseUserStats != null) {
            // Firebase has data - Firebase wins, restore immediately
            Log.d(TAG, "☁️ Firebase data found - IMMEDIATE RESTORE (Firebase priority)")
            Log.d(TAG, "📊 Firebase Data - Total: ${firebaseUserStats.totalCups}, Weekly: ${firebaseUserStats.weeklyCups}")
            userStatsUseCases.restoreUserStats.restoreFromData(firebaseUserStats)
        } else {
            Log.d(TAG, "📭 No Firebase data found")
            
            // Step 3: Initialize local user (only if no Firebase data)
            Log.d(TAG, "3️⃣ Initializing local user...")
            userStatsUseCases.initializeLocalUserUseCase()
            
            // Step 4: Check if local has any meaningful data to backup
            val currentLocalUser = userStatsUseCases.getUserStats().first()
            Log.d(TAG, "📊 Local Data - Total: ${currentLocalUser?.totalCups ?: 0}, Weekly: ${currentLocalUser?.weeklyCups ?: 0}")
            
            if (currentLocalUser != null && (currentLocalUser.totalCups > 0 || currentLocalUser.weeklyCups > 0)) {
                Log.d(TAG, "📱 Local has meaningful data - BACKING UP to cloud")
                userStatsUseCases.backupUserStats()
            }
        }
        
        // Step 5: Sync Firebase ID (after data operations)
        Log.d(TAG, "4️⃣ Syncing Firebase user ID...")
        userStatsUseCases.syncFirebaseUser()
        
        Log.d(TAG, "✅ Initial Sync Process COMPLETED")
    }

    suspend fun performFinalBackup() {
        // if User not login don't backup
        val firebaseUid = auth.currentUser?.uid ?: return

        // localId control
        val currentUser = userStatsUseCases.getUserStats().first()
        if (currentUser ==  null) return

        // backup Cloud
        userStatsUseCases.backupUserStats()
    }

}