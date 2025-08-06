package com.remziakgoz.coffeepomodoro.data.repository

import com.remziakgoz.coffeepomodoro.data.local.roomdb.UserStatsDao
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserStatsRepositoryImpl @Inject constructor(
    private val userStatsDao: UserStatsDao,
) : UserStatsRepository {

    override fun getUserStatsFlow(localId: Long): Flow<UserStats> {
        return userStatsDao.getUserStats(localId)
    }

    override suspend fun updateUserStats(userStats: UserStats) {
        return userStatsDao.updateUserStats(userStats)
    }

    override suspend fun insertUserStats(userStats: UserStats): Long {
        return userStatsDao.insertOrUpdate(userStats)
    }
}