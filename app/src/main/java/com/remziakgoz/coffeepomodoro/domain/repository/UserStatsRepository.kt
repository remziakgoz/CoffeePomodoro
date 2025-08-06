package com.remziakgoz.coffeepomodoro.domain.repository

import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserStatsRepository {
    fun getUserStatsFlow(localId: Long): Flow<UserStats>
    suspend fun updateUserStats(userStats: UserStats)
    suspend fun insertUserStats(userStats: UserStats): Long
}