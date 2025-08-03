package com.remziakgoz.coffeepomodoro.data.local.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.remziakgoz.coffeepomodoro.domain.model.UserStats

@Dao
interface UserStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(userStats: UserStats)

    @Query("SELECT * FROM UserStats WHERE userId = :userId LIMIT 1")
    suspend fun getUserStats(userId: Int): UserStats?

    @Update
    suspend fun updateUserStats(userStats: UserStats)

    @Delete
    suspend fun deleteUserStats(userStats: UserStats)
}