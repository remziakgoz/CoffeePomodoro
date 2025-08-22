package com.remziakgoz.coffeepomodoro.domain.repository

import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserStatsRepository {
    fun getUserStatsFlow(): Flow<UserStats?>
    suspend fun updateUserStats(userStats: UserStats)
    suspend fun updateUserStatsWithoutBackup(userStats: UserStats) 
    suspend fun insertUserStats(userStats: UserStats): Long
    suspend fun insertUserStatsWithoutBackup(userStats: UserStats): Long
    suspend fun getCurrentUserStats(): UserStats?
}