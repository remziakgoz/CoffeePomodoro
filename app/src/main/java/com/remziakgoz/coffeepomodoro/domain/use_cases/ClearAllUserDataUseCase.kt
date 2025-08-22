package com.remziakgoz.coffeepomodoro.domain.use_cases

import android.util.Log
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import javax.inject.Inject

class ClearAllUserDataUseCase @Inject constructor(
    private val repository: UserStatsRepository,
    private val preferenceManager: PreferenceManager
) {
    companion object {
        private const val TAG = "ClearAllUserDataUseCase"
    }

    suspend operator fun invoke() {
        Log.d(TAG, "🧹 STARTING complete user data cleanup")
        
        // Clear database
        repository.clearAllUserData()
        
        // Clear preferences
        preferenceManager.clearAllUserData()
        
        Log.d(TAG, "✅ User data cleanup COMPLETED")
    }
}