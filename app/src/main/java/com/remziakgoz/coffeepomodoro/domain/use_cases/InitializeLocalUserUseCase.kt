package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import javax.inject.Inject

class InitializeLocalUserUseCase @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val userStatsRepository: UserStatsRepository
) {
    suspend operator fun invoke() {
        val localId = preferenceManager.getCurrentUserLocalId()
        if (localId == -1L) {
            val guestUser = UserStats(name = "Guest")
            val generatedId = userStatsRepository.insertUserStats(guestUser)
            preferenceManager.saveCurrentUserLocalId(generatedId)
        }
    }
}