package com.remziakgoz.coffeepomodoro.data.repository

import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.data.local.roomdb.UserStatsDao
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserStatsRepositoryImpl @Inject constructor(
    private val userStatsDao: UserStatsDao,
    private val sharedPreferences: PreferenceManager
) : UserStatsRepository {
    override fun getUserStatsFlow(): Flow<UserStats> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserStats(stats: UserStats) {
        TODO("Not yet implemented")
    }
}