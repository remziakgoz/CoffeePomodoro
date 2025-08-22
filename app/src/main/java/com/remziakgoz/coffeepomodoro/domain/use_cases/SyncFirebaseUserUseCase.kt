package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncFirebaseUserUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: UserStatsRepository
    ) {
    suspend operator fun invoke() {
        val firebaseUid = auth.currentUser?.uid ?: return

        val user = repository.getUserStatsFlow().first() ?: return

        if (user.firebaseId == null) {
            val updatedUser = user.copy(firebaseId = firebaseUid)
            // Use updateUserStatsWithoutBackup to prevent Firebase override during sync
            repository.updateUserStatsWithoutBackup(updatedUser)
        }
    }
}