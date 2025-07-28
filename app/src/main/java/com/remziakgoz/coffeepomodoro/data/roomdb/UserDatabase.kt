package com.remziakgoz.coffeepomodoro.data.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.remziakgoz.coffeepomodoro.domain.model.UserStats

@Database(entities = [UserStats::class], version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userStatsDao() : UserStatsDao
}