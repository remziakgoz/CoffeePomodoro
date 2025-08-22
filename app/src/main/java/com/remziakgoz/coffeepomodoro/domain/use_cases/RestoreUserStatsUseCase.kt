package com.remziakgoz.coffeepomodoro.domain.use_cases

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.data.remote.FirebaseUserStatsService
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import javax.inject.Inject

class RestoreUserStatsUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: UserStatsRepository,
    private val firebaseService: FirebaseUserStatsService
) {
    companion object {
        private const val TAG = "RestoreUserStatsUseCase"
    }

    // Original method - fetch from Firebase directly
    suspend operator fun invoke() {
        val firebaseUid = auth.currentUser?.uid ?: return

        val remoteUserStats = firebaseService.fetchUserStats(firebaseUid) ?: return

        restoreFromData(remoteUserStats)
    }

    // New method - restore from already fetched data (preferred)
    suspend fun restoreFromData(firebaseUserStats: UserStats) {
        Log.d(TAG, "🔄 RESTORING from Firebase data - Total: ${firebaseUserStats.totalCups}, Weekly: ${firebaseUserStats.weeklyCups}")
        
        // Use a special method that doesn't trigger auto-backup
        repository.insertUserStatsWithoutBackup(firebaseUserStats)
        
        Log.d(TAG, "✅ RESTORE completed successfully")
    }

}