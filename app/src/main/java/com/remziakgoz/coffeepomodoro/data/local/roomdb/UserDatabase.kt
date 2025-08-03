package com.remziakgoz.coffeepomodoro.data.local.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.remziakgoz.coffeepomodoro.domain.model.UserStats

@Database(entities = [UserStats::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userStatsDao() : UserStatsDao
}