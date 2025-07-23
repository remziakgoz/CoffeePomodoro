package com.remziakgoz.coffeepomodoro.data.repository

import com.remziakgoz.coffeepomodoro.data.local.dao.DailyCupData
import com.remziakgoz.coffeepomodoro.data.local.dao.PomodoroDao
import com.remziakgoz.coffeepomodoro.data.local.entities.PomodoroSession
import com.remziakgoz.coffeepomodoro.data.local.entities.SessionType
import com.remziakgoz.coffeepomodoro.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroRepository @Inject constructor(
    private val localDao: PomodoroDao,
    private val remoteDataSource: FirebaseDataSource
) {
    
    // ========== Session Management ==========
    
    suspend fun saveSession(session: PomodoroSession): Result<String> {
        return try {
            // Önce local'e kaydet (offline support için)
            localDao.insertSession(session)
            
            // Sonra remote'a kaydet (network varsa)
            val remoteResult = remoteDataSource.saveSession(session)
            if (remoteResult.isFailure) {
                // Remote başarısız olsa bile local başarılı, kullanıcıya hata verme
                // Sync later ile halledilir
            }
            
            Result.success(session.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateSession(session: PomodoroSession): Result<String> {
        return try {
            localDao.updateSession(session)
            remoteDataSource.saveSession(session) // Firebase'de update = set
            Result.success(session.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteSession(session: PomodoroSession): Result<Unit> {
        return try {
            localDao.deleteSession(session)
            remoteDataSource.deleteSession(session.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== Dashboard Data ==========
    
    suspend fun getTodayProgress(userId: String): DashboardData {
        return try {
            val today = localDao.getTodayCups(userId) ?: 0
            val week = localDao.getWeeklyCups(userId) ?: 0
            val month = localDao.getMonthlyCups(userId) ?: 0
            val dailyAvg = localDao.getDailyAverage(userId) ?: 0.0
            val bestStreak = localDao.getLongestStreak(userId) ?: 0
            val currentStreak = localDao.getCurrentStreak(userId) ?: 0
            val totalSessions = localDao.getTotalCompletedSessions(userId)
            val last7Days = localDao.getLast7DaysCups(userId)
            
            DashboardData(
                todayCups = today,
                weeklyCups = week,
                monthlyCups = month,
                dailyAverage = dailyAvg,
                bestStreak = bestStreak,
                currentStreak = currentStreak,
                totalSessions = totalSessions,
                last7DaysData = last7Days
            )
        } catch (e: Exception) {
            // Error durumunda default değerler döndür
            DashboardData()
        }
    }
    
    fun getTodayProgressFlow(userId: String): Flow<DashboardData> = flow {
        while (true) {
            emit(getTodayProgress(userId))
            kotlinx.coroutines.delay(30000) // 30 saniye refresh
        }
    }
    
    // ========== Sync Operations ==========
    
    suspend fun syncData(userId: String): Result<Unit> {
        return try {
            // Local tüm sessionları al
            val localSessions = localDao.getAllUserSessions(userId).first()
            
            // Firebase ile sync et
            val syncResult = remoteDataSource.syncUserData(userId, localSessions)
            if (syncResult.isSuccess) {
                // Remote'dan gelen yeni sessionları local'e kaydet
                val newSessions = syncResult.getOrNull()
                if (!newSessions.isNullOrEmpty()) {
                    localDao.insertSessions(newSessions)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Otomatik sync (app başlangıcında çağır)
    suspend fun performInitialSync(userId: String) {
        try {
            syncData(userId)
        } catch (e: Exception) {
            // Silent fail - offline mode için
        }
    }
    
    // ========== Stream Data (Reactive) ==========
    
    fun getAllUserSessions(userId: String): Flow<List<PomodoroSession>> {
        return localDao.getAllUserSessions(userId)
    }
    
    fun getTodaySessions(userId: String): Flow<List<PomodoroSession>> {
        return localDao.getTodaySessions(userId)
    }
    
    // ========== Achievement Helpers ==========
    
    suspend fun checkTodayGoalAchieved(userId: String, goalCups: Int = 5): Boolean {
        val todayCups = localDao.getTodayCups(userId) ?: 0
        return todayCups >= goalCups
    }
    
    suspend fun checkWeeklyGoalAchieved(userId: String, goalCups: Int = 35): Boolean {
        val weeklyCups = localDao.getWeeklyCups(userId) ?: 0
        return weeklyCups >= goalCups
    }
    
    suspend fun getAchievements(userId: String): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        
        val todayGoal = checkTodayGoalAchieved(userId)
        if (todayGoal) {
            achievements.add(Achievement.GOAL_REACHED)
        }
        
        val currentStreak = localDao.getCurrentStreak(userId) ?: 0
        if (currentStreak >= 3) {
            achievements.add(Achievement.THREE_DAY_STREAK)
        }
        
        val todayCups = localDao.getTodayCups(userId) ?: 0
        // Sabah 6-10 arası session var mı kontrol et (Morning Star)
        // Bu SQL query'si eklenebilir
        
        return achievements
    }
}

// Data Classes
data class DashboardData(
    val todayCups: Int = 0,
    val weeklyCups: Int = 0, 
    val monthlyCups: Int = 0,
    val dailyAverage: Double = 0.0,
    val bestStreak: Int = 0,
    val currentStreak: Int = 0,
    val totalSessions: Int = 0,
    val last7DaysData: List<DailyCupData> = emptyList(),
    val weeklyGoal: Int = 35,
    val dailyGoal: Int = 5
) {
    val weeklyProgress: Float = if (weeklyGoal > 0) weeklyCups.toFloat() / weeklyGoal else 0f
    val dailyProgress: Float = if (dailyGoal > 0) todayCups.toFloat() / dailyGoal else 0f
    
    // Dashboard için son 7 günü List<Int> formatında döndür
    fun getLast7DaysAsIntList(): List<Int> {
        val result = mutableListOf<Int>()
        val today = java.util.Calendar.getInstance()
        
        // Son 7 gün için tarih listesi oluştur
        for (i in 6 downTo 0) {
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -i)
            val dayString = "${calendar.get(java.util.Calendar.YEAR)}-${
                String.format("%02d", calendar.get(java.util.Calendar.MONTH) + 1)
            }-${String.format("%02d", calendar.get(java.util.Calendar.DAY_OF_MONTH))}"
            
            val cupsForDay = last7DaysData.find { it.day == dayString }?.totalCups ?: 0
            result.add(cupsForDay)
        }
        
        return result
    }
}

enum class Achievement {
    GOAL_REACHED,      // Günlük hedef 
    THREE_DAY_STREAK,  // 3 günlük seri
    MORNING_STAR,      // Sabah kahvesi 
    CONSISTENCY        // Haftalık tutarlılık
} 