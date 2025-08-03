package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import javax.inject.Inject

class UpdateUserStatsUseCase @Inject constructor(
    private val userStatsRepository: UserStatsRepository
) {
    suspend operator fun invoke(stats: UserStats) {
        userStatsRepository.updateUserStats(stats)
    }
}