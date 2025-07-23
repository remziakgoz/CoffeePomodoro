package com.remziakgoz.coffeepomodoro.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import android.content.Context
import com.remziakgoz.coffeepomodoro.data.local.dao.PomodoroDao
import com.remziakgoz.coffeepomodoro.data.local.entities.PomodoroSession
import com.remziakgoz.coffeepomodoro.data.local.entities.SessionType

@Database(
    entities = [PomodoroSession::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun pomodoroDao(): PomodoroDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coffee_pomodoro_database"
                )
                .fallbackToDestructiveMigration() // Dev sırasında kullan
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Type Converters for Room
class Converters {
    
    @TypeConverter
    fun fromSessionType(sessionType: SessionType): String {
        return sessionType.name
    }
    
    @TypeConverter
    fun toSessionType(sessionType: String): SessionType {
        return SessionType.valueOf(sessionType)
    }
} 