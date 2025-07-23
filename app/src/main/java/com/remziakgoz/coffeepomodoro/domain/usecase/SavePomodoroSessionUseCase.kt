package com.remziakgoz.coffeepomodoro.domain.usecase

import com.remziakgoz.coffeepomodoro.data.local.entities.PomodoroSession
import com.remziakgoz.coffeepomodoro.data.local.entities.SessionType
import com.remziakgoz.coffeepomodoro.data.repository.PomodoroRepository
import javax.inject.Inject

class SavePomodoroSessionUseCase @Inject constructor(
    private val repository: PomodoroRepository
) {
    suspend operator fun invoke(
        userId: String,
        sessionDuration: Long,
        breakDuration: Long,
        sessionType: SessionType,
        cupsConsumed: Int = 1,
        focusScore: Int = 5,
        notes: String? = null
    ): Result<String> {
        val session = PomodoroSession(
            userId = userId,
            sessionDuration = sessionDuration,
            breakDuration = breakDuration,
            isCompleted = true,
            sessionType = sessionType,
            cupsConsumed = cupsConsumed,
            focusScore = focusScore,
            notes = notes
        )
        
        return repository.saveSession(session)
    }
} 