package com.remziakgoz.coffeepomodoro.data.local.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

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

    private val _localIdState =
        MutableStateFlow(sharedPreferences.getLong(KEY_CURRENT_USER_LOCAL_ID, -1L))
    val localIdFlow: StateFlow<Long> = _localIdState.asStateFlow()

    //Listener
    private val spListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == KEY_CURRENT_USER_LOCAL_ID) {
            _localIdState.value = prefs.getLong(KEY_CURRENT_USER_LOCAL_ID, -1L)
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(spListener)
    }

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

    fun clearCycleCount() {
        sharedPreferences.edit{ putInt(KEY_CYCLE_COUNT,0) }
    }

    //User Settings
    fun saveCurrentUserLocalId(id: Long) {
        sharedPreferences.edit{ putLong(KEY_CURRENT_USER_LOCAL_ID, id)}
        _localIdState.value = id
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

    fun clearAllUserData() {
        android.util.Log.d("PreferenceManager", "🧹 CLEARING all user preferences")
        sharedPreferences.edit {
            remove(KEY_CURRENT_USER_LOCAL_ID)
            remove(KEY_CYCLE_COUNT)
            remove(KEY_LAST_RESET_DATE)
            remove(KEY_LAST_SEEN_LEVEL_GLOBAL)
            // Also clear any user-specific level keys
            val userId = getCurrentUserLocalId()
            if (userId != -1L) {
                remove(lastSeenLevelKey(userId))
            }
        }
        _localIdState.value = -1L
    }

}