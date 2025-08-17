package com.remziakgoz.coffeepomodoro.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserStats(
    // User info
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val name: String = "Guest",
    val email: String? = null,
    val firebaseId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),

    // Daily / Weekly / Monthly data
    @ColumnInfo(name = "todayCups")
    val todayCups: Int = 0,
    @ColumnInfo(name = "weeklyCups")
    val weeklyCups: Int = 0,
    @ColumnInfo(name = "monthlyCups")
    val monthlyCups: Int = 0,

    // Daily / Weekly / Monthly date
    @ColumnInfo(name = "todayDate")
    val todayDate: String = "",
    @ColumnInfo(name = "currentWeekStart")
    val currentWeekStart: String = "",
    @ColumnInfo(name = "currentMonth")
    val currentMonth: String = "",

    // Goals & progress
    @ColumnInfo(name = "weeklyGoal")
    val weeklyGoal: Int = 35,
    @ColumnInfo(name = "bestStreak")
    val bestStreak: Int = 0,
    @ColumnInfo(name = "currentStreak")
    val currentStreak: Int = 0,

    // Daily statistics (7 days)
    @ColumnInfo(name = "dailyData")
    val dailyData: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),

    // Achievements
    @ColumnInfo(name = "morningStar")
    val morningStar: Boolean = false,

    // Other
    @ColumnInfo(name = "dailyAverage")
    val dailyAverage: Float = 0f,
    @ColumnInfo(name = "totalDays")
    val totalDays: Int = 0,
    @ColumnInfo(name = "totalCups")
    val totalCups: Int = 0,
    @ColumnInfo(name = "lastUpdated")
    val lastUpdated: Long = System.currentTimeMillis()


)