package com.remziakgoz.coffeepomodoro.domain.repository

import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserStatsRepository {
    fun getUserStatsFlow(): Flow<UserStats>
    suspend fun updateUserStats(stats: UserStats)
}