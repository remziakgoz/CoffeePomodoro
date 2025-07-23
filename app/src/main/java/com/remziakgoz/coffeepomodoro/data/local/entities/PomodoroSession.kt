package com.remziakgoz.coffeepomodoro.data.local.entities

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSession(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "", // Firebase UID
    val sessionDuration: Long = 25 * 60 * 1000L, // 25 dakika milliseconds
    val breakDuration: Long = 5 * 60 * 1000L, // 5 dakika break
    val isCompleted: Boolean = false,
    val completedAt: Long = System.currentTimeMillis(),
    val sessionType: SessionType = SessionType.WORK,
    val cupsConsumed: Int = 1, // Bu session'da kaç bardak kahve içildi
    val focusScore: Int = 5, // 1-5 arası focus puanı
    val notes: String? = null,
    val day: String = getCurrentDay(), // "2024-01-15" format
    val week: String = getCurrentWeek(), // "2024-W03" format
    val month: String = getCurrentMonth() // "2024-01" format
)

enum class SessionType {
    WORK,           // Çalışma seansı
    SHORT_BREAK,    // Kısa mola
    LONG_BREAK      // Uzun mola
}

// Helper functions
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