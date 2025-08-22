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
        
        // Step 1: Local user initialize
        Log.d(TAG, "1️⃣ Initializing local user...")
        userStatsUseCases.initializeLocalUserUseCase()

        // Step 2: Firebase ID sync
        Log.d(TAG, "2️⃣ Syncing Firebase user...")
        userStatsUseCases.syncFirebaseUser()

        // Step 3: Smart sync - Firebase data has priority
        val firebaseUid = auth.currentUser?.uid ?: run {
            Log.d(TAG, "❌ No Firebase user found, skipping cloud sync")
            return
        }

        Log.d(TAG, "3️⃣ Checking Firebase data for user: $firebaseUid")
        
        // First, fetch Firebase data
        val firebaseUserStats = firebaseService.fetchUserStats(firebaseUid)
        val currentLocalUser = userStatsUseCases.getUserStats().first()
        
        Log.d(TAG, "📊 Local Data - Total: ${currentLocalUser?.totalCups ?: 0}, Weekly: ${currentLocalUser?.weeklyCups ?: 0}")

        when {
            firebaseUserStats != null -> {
                // Firebase has data - Firebase wins
                Log.d(TAG, "☁️ Firebase data found - RESTORING from cloud (Firebase priority)")
                Log.d(TAG, "📊 Firebase Data - Total: ${firebaseUserStats.totalCups}, Weekly: ${firebaseUserStats.weeklyCups}")
                userStatsUseCases.restoreUserStats.restoreFromData(firebaseUserStats)
            }
            currentLocalUser != null && (currentLocalUser.totalCups > 0 || currentLocalUser.weeklyCups > 0) -> {
                // No Firebase data but local has data - backup to Firebase
                Log.d(TAG, "📱 No Firebase data, local has data - BACKING UP to cloud")
                userStatsUseCases.backupUserStats()
            }
            else -> {
                Log.d(TAG, "📭 No data found in Firebase or locally - fresh start")
            }
        }
        
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