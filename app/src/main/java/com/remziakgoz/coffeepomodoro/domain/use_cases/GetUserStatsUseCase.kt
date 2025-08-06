package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserStatsUseCase @Inject constructor(
    private val repository: UserStatsRepository
) {
    operator fun invoke(localId: Long): Flow<UserStats> {
        return repository.getUserStatsFlow(localId)
    }
}