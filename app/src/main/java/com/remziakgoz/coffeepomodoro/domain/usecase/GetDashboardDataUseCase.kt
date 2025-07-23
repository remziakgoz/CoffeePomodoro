package com.remziakgoz.coffeepomodoro.domain.usecase

import com.remziakgoz.coffeepomodoro.data.repository.DashboardData
import com.remziakgoz.coffeepomodoro.data.repository.PomodoroRepository
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val repository: PomodoroRepository
) {
    suspend operator fun invoke(userId: String): DashboardData {
        return repository.getTodayProgress(userId)
    }
} 