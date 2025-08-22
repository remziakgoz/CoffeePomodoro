package com.remziakgoz.coffeepomodoro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.remziakgoz.coffeepomodoro.data.local.roomdb.UserStatsDao
import com.remziakgoz.coffeepomodoro.data.remote.FirebaseUserStatsService
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserStatsRepositoryImpl @Inject constructor(
    private val userStatsDao: UserStatsDao,
    private val firebaseService: FirebaseUserStatsService,
    private val auth: FirebaseAuth
) : UserStatsRepository {

    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    private fun isOnline(): Boolean = auth.currentUser != null

    override fun getUserStatsFlow(): Flow<UserStats?> {
        return userStatsDao.getUserStatsFLow()
    }

    override suspend fun updateUserStats(userStats: UserStats) {
        android.util.Log.d("UserStatsRepository", "⚠️ updateUserStats called - This will trigger Firebase backup!")
        userStatsDao.updateUserStats(userStats)

        if (isOnline()) {
            getCurrentUserId()?.let { userId ->
                runCatching {
                    firebaseService.backupUserStats(userStats.copy(firebaseId = userId))
                }
            }
        }
    }

    override suspend fun updateUserStatsWithoutBackup(userStats: UserStats) {
        android.util.Log.d("UserStatsRepository", "🔄 updateUserStatsWithoutBackup - No Firebase backup")
        userStatsDao.updateUserStats(userStats)
    }

    override suspend fun insertUserStats(userStats: UserStats): Long {
        val localId = userStatsDao.insertOrUpdate(userStats)

        if (isOnline()) {
            getCurrentUserId()?.let { userId ->
                runCatching {
                    firebaseService.backupUserStats(userStats.copy(firebaseId = userId))
                }
            }
        }
        return localId
    }

    override suspend fun insertUserStatsWithoutBackup(userStats: UserStats): Long {
        android.util.Log.d("UserStatsRepository", "🔄 Inserting WITHOUT backup - Total: ${userStats.totalCups}, Weekly: ${userStats.weeklyCups}")
        return userStatsDao.insertOrUpdate(userStats)
    }

    override suspend fun getCurrentUserStats(): UserStats? {
        return if (isOnline()) {
            getCurrentUserId()?.let { userId ->
                runCatching {
                    firebaseService.fetchUserStats(userId)
                }.getOrNull()
            }
        } else {
            userStatsDao.getCurrentUserStats()
        }
    }

    override suspend fun clearAllUserData() {
        android.util.Log.d("UserStatsRepository", "🧹 CLEARING all user data from local database")
        userStatsDao.clearAllUserData()
    }
}