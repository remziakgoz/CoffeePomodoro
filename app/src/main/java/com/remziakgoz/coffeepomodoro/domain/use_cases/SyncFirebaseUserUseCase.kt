package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncFirebaseUserUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val preferenceManager: PreferenceManager,
    private val repository: UserStatsRepository
    ) {
    suspend operator fun invoke() {
        val firebaseUid = auth.currentUser?.uid ?: return
        val localId = preferenceManager.getCurrentUserLocalId()
        if (localId == -1L) return

        val user = repository.getUserStatsFlow(localId).first()

        if (user.firebaseId == null) {
            val updatedUser = user.copy(firebaseId = firebaseUid)
            repository.updateUserStats(updatedUser)
        }
    }
}