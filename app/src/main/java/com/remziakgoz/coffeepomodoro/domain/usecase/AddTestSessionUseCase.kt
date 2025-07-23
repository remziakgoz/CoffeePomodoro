package com.remziakgoz.coffeepomodoro.domain.usecase

import com.remziakgoz.coffeepomodoro.data.local.entities.SessionType
import javax.inject.Inject

class AddTestSessionUseCase @Inject constructor(
    private val savePomodoroSessionUseCase: SavePomodoroSessionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        cupsCount: Int = 1
    ): Result<String> {
        return savePomodoroSessionUseCase(
            userId = userId,
            sessionDuration = 25 * 60 * 1000L, // 25 minutes
            breakDuration = 5 * 60 * 1000L,   // 5 minutes
            sessionType = SessionType.WORK,
            cupsConsumed = cupsCount,
            focusScore = 5,
            notes = "Test session"
        )
    }
} 