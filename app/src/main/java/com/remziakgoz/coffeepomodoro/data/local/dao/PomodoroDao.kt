package com.remziakgoz.coffeepomodoro.data.local.dao

import android.annotation.SuppressLint
import androidx.room.*
import com.remziakgoz.coffeepomodoro.data.local.entities.PomodoroSession
import com.remziakgoz.coffeepomodoro.data.local.entities.SessionType
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {

    // ========== Basic CRUD Operations ==========
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSession)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<PomodoroSession>)
    
    @Update
    suspend fun updateSession(session: PomodoroSession)
    
    @Delete
    suspend fun deleteSession(session: PomodoroSession)
    
    @Query("DELETE FROM pomodoro_sessions WHERE userId = :userId")
    suspend fun deleteAllUserSessions(userId: String)

    // ========== Dashboard Data Queries ==========
    
    @Query("""
        SELECT SUM(cupsConsumed) 
        FROM pomodoro_sessions 
        WHERE userId = :userId 
        AND day = :day 
        AND isCompleted = 1
        AND sessionType = :sessionType
    """)
    suspend fun getTodayCups(
        userId: String, 
        day: String = getCurrentDay(),
        sessionType: SessionType = SessionType.WORK
    ): Int?
    
    @Query("""
        SELECT SUM(cupsConsumed)
        FROM pomodoro_sessions
        WHERE userId = :userId 
        AND week = :week
        AND isCompleted = 1  
        AND sessionType = :sessionType
    """)
    suspend fun getWeeklyCups(
        userId: String,
        week: String = getCurrentWeek(),
        sessionType: SessionType = SessionType.WORK
    ): Int?
    
    @Query("""
        SELECT SUM(cupsConsumed)
        FROM pomodoro_sessions  
        WHERE userId = :userId
        AND month = :month
        AND isCompleted = 1
        AND sessionType = :sessionType
    """)
    suspend fun getMonthlyCups(
        userId: String,
        month: String = getCurrentMonth(), 
        sessionType: SessionType = SessionType.WORK
    ): Int?
    
    @Query("""
        SELECT day, SUM(cupsConsumed) as totalCups
        FROM pomodoro_sessions
        WHERE userId = :userId
        AND isCompleted = 1
        AND sessionType = :sessionType
        AND completedAt >= :sevenDaysAgo
        GROUP BY day
        ORDER BY day DESC
        LIMIT 7
    """)
    suspend fun getLast7DaysCups(
        userId: String,
        sevenDaysAgo: Long = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L),
        sessionType: SessionType = SessionType.WORK
    ): List<DailyCupData>
    
    @Query("""
        SELECT AVG(dailyTotal) 
        FROM (
            SELECT SUM(cupsConsumed) as dailyTotal
            FROM pomodoro_sessions
            WHERE userId = :userId 
            AND isCompleted = 1
            AND sessionType = :sessionType
            AND completedAt >= :thirtyDaysAgo
            GROUP BY day
        )
    """)
    suspend fun getDailyAverage(
        userId: String,
        thirtyDaysAgo: Long = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L),
        sessionType: SessionType = SessionType.WORK
    ): Double?

    @Query("""
        SELECT MAX(streak_length) as maxStreak
        FROM (
            SELECT COUNT(*) as streak_length
            FROM (
                SELECT day,
                       ROW_NUMBER() OVER (ORDER BY day) - 
                       ROW_NUMBER() OVER (PARTITION BY has_session ORDER BY day) as streak_group
                FROM (
                    SELECT DISTINCT day,
                           CASE WHEN SUM(cupsConsumed) > 0 THEN 1 ELSE 0 END as has_session
                    FROM pomodoro_sessions
                    WHERE userId = :userId 
                    AND isCompleted = 1
                    AND sessionType = :sessionType
                    GROUP BY day
                ) daily_sessions
                WHERE has_session = 1
            ) grouped_days
            GROUP BY streak_group
        )
    """)
    suspend fun getLongestStreak(
        userId: String,
        sessionType: SessionType = SessionType.WORK
    ): Int?

    @Query("""
        SELECT COUNT(*) as current_streak
        FROM (
            SELECT day
            FROM pomodoro_sessions
            WHERE userId = :userId
            AND isCompleted = 1
            AND sessionType = :sessionType
            GROUP BY day
            HAVING SUM(cupsConsumed) > 0
            ORDER BY day DESC
        ) consecutive_days
        WHERE julianday('now') - julianday(day) < (
            SELECT COUNT(*)
            FROM (
                SELECT day, ROW_NUMBER() OVER (ORDER BY day DESC) as rn
                FROM pomodoro_sessions
                WHERE userId = :userId
                AND isCompleted = 1
                AND sessionType = :sessionType
                GROUP BY day
                HAVING SUM(cupsConsumed) > 0
                ORDER BY day DESC
            ) ranked
            WHERE julianday('now') - julianday(day) = rn - 1
        )
    """)
    suspend fun getCurrentStreak(
        userId: String,
        sessionType: SessionType = SessionType.WORK
    ): Int?

    @Query("""
        SELECT COUNT(*)
        FROM pomodoro_sessions
        WHERE userId = :userId
        AND isCompleted = 1
        AND sessionType = :sessionType
    """)
    suspend fun getTotalCompletedSessions(
        userId: String,
        sessionType: SessionType = SessionType.WORK
    ): Int

    @Query("""
        SELECT * FROM pomodoro_sessions 
        WHERE userId = :userId 
        ORDER BY completedAt DESC
    """)
    fun getAllUserSessions(userId: String): Flow<List<PomodoroSession>>
    
    @Query("""
        SELECT * FROM pomodoro_sessions
        WHERE userId = :userId
        AND day = :day
        ORDER BY completedAt DESC
    """)
    fun getTodaySessions(
        userId: String,
        day: String = getCurrentDay()
    ): Flow<List<PomodoroSession>>
}

// Data class for daily cup results
data class DailyCupData(
    val day: String,
    val totalCups: Int
)

// Helper functions (same as entity)
@SuppressLint("DefaultLocale")
private fun getCurrentDay(): String {
    val calendar = java.util.Calendar.getInstance()
    return "${calendar.get(java.util.Calendar.YEAR)}-${
        String.format("%02d", calendar.get(java.util.Calendar.MONTH) + 1)
    }-${String.format("%02d", calendar.get(java.util.Calendar.DAY_OF_MONTH))}"
}

@SuppressLint("DefaultLocale")
private fun getCurrentWeek(): String {
    val calendar = java.util.Calendar.getInstance()
    return "${calendar.get(java.util.Calendar.YEAR)}-W${
        String.format("%02d", calendar.get(java.util.Calendar.WEEK_OF_YEAR))
    }"
}

@SuppressLint("DefaultLocale")
private fun getCurrentMonth(): String {
    val calendar = java.util.Calendar.getInstance()
    return "${calendar.get(java.util.Calendar.YEAR)}-${
        String.format("%02d", calendar.get(java.util.Calendar.MONTH) + 1)
    }"
} 