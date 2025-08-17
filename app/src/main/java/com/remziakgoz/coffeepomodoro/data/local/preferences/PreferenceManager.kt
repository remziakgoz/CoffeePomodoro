package com.remziakgoz.coffeepomodoro.data.local.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val KEY_LAST_SEEN_LEVEL_GLOBAL = "last_seen_level_global"
        private const val KEY_CURRENT_USER_LOCAL_ID = "current_user_local_id"
        private const val KEY_CYCLE_COUNT = "cycle_count"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
    }

    private fun lastSeenLevelKey(userId: Long) = "last_seen_level_$userId"

    // Cycle Count
    fun saveCycleCount(count: Int) {
        sharedPreferences.edit { putInt(KEY_CYCLE_COUNT, count) }
    }

    fun getCycleCount(): Int {
        return sharedPreferences.getInt(KEY_CYCLE_COUNT,0)
    }

    fun getLastResetDate(): String? {
        return sharedPreferences.getString(KEY_LAST_RESET_DATE, null)
    }

    fun saveLastResetDate(date: String) {
        sharedPreferences.edit { putString(KEY_LAST_RESET_DATE, date) }
    }

    fun clearDailyValue() {
        sharedPreferences.edit{ putInt(KEY_CYCLE_COUNT,0) }
    }

    //User Settings
    fun saveCurrentUserLocalId(id: Long) {
        sharedPreferences.edit{ putLong(KEY_CURRENT_USER_LOCAL_ID, id)}
    }

    fun getCurrentUserLocalId(): Long {
        return sharedPreferences.getLong(KEY_CURRENT_USER_LOCAL_ID, -1L)
    }

    fun getLastSeenLevel(): Int {
        val userId = getCurrentUserLocalId()
        return if (userId == -1L) {
            sharedPreferences.getInt(KEY_LAST_SEEN_LEVEL_GLOBAL, 1)
        } else {
            sharedPreferences.getInt(lastSeenLevelKey(userId), 1)
        }
    }

    fun setLastSeenLevel(level: Int) {
        val userId = getCurrentUserLocalId()
        sharedPreferences.edit {
            if (userId == -1L) {
                putInt(KEY_LAST_SEEN_LEVEL_GLOBAL, level)
            } else {
                putInt(lastSeenLevelKey(userId), level)
            }
        }
    }

    fun clearLevelStateForCurrentUser() {
        val userId = getCurrentUserLocalId()
        if (userId != -1L) {
            sharedPreferences.edit { remove(lastSeenLevelKey(userId)) }
        }
    }

}