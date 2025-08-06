package com.remziakgoz.coffeepomodoro.data.sync

import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataSyncManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val preferencesManager: PreferenceManager,
    private val userStatsUseCases: UserStatsUseCases
) {

    suspend fun performInitialSync() {
        // Step 1: Local user initialize
        userStatsUseCases.initializeLocalUserUseCase()

        // Step 2: Firebase ID sync
        userStatsUseCases.syncFirebaseUser()

        // Step 3: if Firebase login control cloud data
        val firebaseUid = auth.currentUser?.uid ?: return
        val currentLocalId = preferencesManager.getCurrentUserLocalId()

        val currentUser = userStatsUseCases.getUserStats(currentLocalId).first()

        if (currentUser.totalCups == 0 && currentUser.weeklyCups == 0) {
            userStatsUseCases.restoreUserStats()
        }

        userStatsUseCases.backupUserStats()
    }

    suspend fun performFinalBackup() {
        // if User not login don't backup
        val firebaseUid = auth.currentUser?.uid ?: return

        // localId control
        val localId = preferencesManager.getCurrentUserLocalId()
        if (localId == -1L) return

        // backup Cloud
        userStatsUseCases.backupUserStats()
    }

}