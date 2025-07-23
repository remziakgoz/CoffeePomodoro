package com.remziakgoz.coffeepomodoro.data.remote.dto

import com.google.firebase.firestore.PropertyName
import com.remziakgoz.coffeepomodoro.data.local.entities.PomodoroSession
import com.remziakgoz.coffeepomodoro.data.local.entities.SessionType

// Firebase Firestore DTO
data class PomodoroSessionDto(
    @PropertyName("id")
    val id: String = "",
    
    @PropertyName("user_id")
    val userId: String = "",
    
    @PropertyName("session_duration")
    val sessionDuration: Long = 25 * 60 * 1000L,
    
    @PropertyName("break_duration") 
    val breakDuration: Long = 5 * 60 * 1000L,
    
    @PropertyName("is_completed")
    val isCompleted: Boolean = false,
    
    @PropertyName("completed_at")
    val completedAt: Long = System.currentTimeMillis(),
    
    @PropertyName("session_type")
    val sessionType: String = SessionType.WORK.name,
    
    @PropertyName("cups_consumed")
    val cupsConsumed: Int = 1,
    
    @PropertyName("focus_score")
    val focusScore: Int = 5,
    
    @PropertyName("notes")
    val notes: String? = null,
    
    @PropertyName("day")
    val day: String = "",
    
    @PropertyName("week") 
    val week: String = "",
    
    @PropertyName("month")
    val month: String = "",
    
    @PropertyName("created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @PropertyName("updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

// Extension functions for conversion
fun PomodoroSessionDto.toEntity(): PomodoroSession {
    return PomodoroSession(
        id = this.id,
        userId = this.userId,
        sessionDuration = this.sessionDuration,
        breakDuration = this.breakDuration,
        isCompleted = this.isCompleted,
        completedAt = this.completedAt,
        sessionType = SessionType.valueOf(this.sessionType),
        cupsConsumed = this.cupsConsumed,
        focusScore = this.focusScore,
        notes = this.notes,
        day = this.day,
        week = this.week,
        month = this.month
    )
}

fun PomodoroSession.toDto(): PomodoroSessionDto {
    return PomodoroSessionDto(
        id = this.id,
        userId = this.userId,
        sessionDuration = this.sessionDuration,
        breakDuration = this.breakDuration,
        isCompleted = this.isCompleted,
        completedAt = this.completedAt,
        sessionType = this.sessionType.name,
        cupsConsumed = this.cupsConsumed,
        focusScore = this.focusScore,
        notes = this.notes,
        day = this.day,
        week = this.week,
        month = this.month,
        createdAt = this.completedAt,
        updatedAt = System.currentTimeMillis()
    )
} 