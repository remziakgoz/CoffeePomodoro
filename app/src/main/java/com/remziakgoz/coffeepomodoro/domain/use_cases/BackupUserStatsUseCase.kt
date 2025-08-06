package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.data.remote.FirebaseUserStatsService
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BackupUserStatsUseCase @Inject constructor(
    private val repository: UserStatsRepository,
    private val preferenceManager: PreferenceManager,
    private val firebaseService: FirebaseUserStatsService
) {
    suspend operator fun invoke() {
        val localId = preferenceManager.getCurrentUserLocalId()
        if (localId == -1L) return

        val userStats = repository.getUserStatsFlow(localId).first()

        if (userStats.firebaseId != null) {
            firebaseService.backupUserStats(userStats)
        }
    }
}