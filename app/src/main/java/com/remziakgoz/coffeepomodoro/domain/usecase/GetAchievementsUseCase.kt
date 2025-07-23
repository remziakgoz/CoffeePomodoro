package com.remziakgoz.coffeepomodoro.domain.usecase

import com.remziakgoz.coffeepomodoro.data.repository.Achievement
import com.remziakgoz.coffeepomodoro.data.repository.PomodoroRepository
import javax.inject.Inject

class GetAchievementsUseCase @Inject constructor(
    private val repository: PomodoroRepository
) {
    suspend operator fun invoke(userId: String): List<Achievement> {
        return repository.getAchievements(userId)
    }
} 