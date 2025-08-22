package com.remziakgoz.coffeepomodoro.data.local.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(userStats: UserStats): Long

    @Query("SELECT * FROM UserStats LIMIT 1")
    fun getUserStatsFLow(): Flow<UserStats?>

    @Query("SELECT * FROM UserStats LIMIT 1")
    suspend fun getCurrentUserStats(): UserStats?

    @Update
    suspend fun updateUserStats(userStats: UserStats)

    @Delete
    suspend fun deleteUserStats(userStats: UserStats)

    @Query("DELETE FROM UserStats")
    suspend fun clearAllUserData()
}