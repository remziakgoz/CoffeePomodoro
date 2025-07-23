package com.remziakgoz.coffeepomodoro.domain.usecase

import com.remziakgoz.coffeepomodoro.data.repository.PomodoroRepository
import javax.inject.Inject

class SyncUserDataUseCase @Inject constructor(
    private val repository: PomodoroRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return repository.syncData(userId)
    }
    
    suspend fun performInitialSync(userId: String) {
        repository.performInitialSync(userId)
    }
} 