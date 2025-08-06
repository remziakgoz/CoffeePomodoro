package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.data.remote.FirebaseUserStatsService
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import javax.inject.Inject

class RestoreUserStatsUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: UserStatsRepository,
    private val preferenceManager: PreferenceManager,
    private val firebaseService: FirebaseUserStatsService
) {
    suspend operator fun invoke() {
        val firebaseUid = auth.currentUser?.uid ?: return

        val remoteUserStats = firebaseService.fetchUserStats(firebaseUid) ?: return

        val localId = repository.insertUserStats(remoteUserStats.copy(localId = 0L))

        preferenceManager.saveCurrentUserLocalId(localId)

    }

}