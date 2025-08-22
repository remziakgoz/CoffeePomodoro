package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.data.remote.FirebaseUserStatsService
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BackupUserStatsUseCase @Inject constructor(
    private val repository: UserStatsRepository,
    private val firebaseService: FirebaseUserStatsService,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke() {
        val currentUser = auth.currentUser ?: return

        val userStats = repository.getUserStatsFlow().first() ?: return

        val statWithFirebaseId = userStats.copy(firebaseId = currentUser.uid)
        firebaseService.backupUserStats(statWithFirebaseId)
    }
}