package com.remziakgoz.coffeepomodoro.data.local.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    // Cycle Count
    fun saveCycleCount(count: Int) {
        sharedPreferences.edit { putInt("cycle_count", count) }
    }

    fun getCycleCount(): Int {
        return sharedPreferences.getInt("cycle_count",0)
    }

    fun getLastResetDate(): String? {
        return sharedPreferences.getString("last_reset_date", null)
    }

    fun saveLastResetDate(date: String) {
        sharedPreferences.edit { putString("last_reset_date", date) }
    }

    fun clearDailyValue() {
        sharedPreferences.edit{ putInt("cycle_count",0) }
    }

    //User Settings
    fun saveCurrentUserLocalId(id: Long) {
        sharedPreferences.edit{ putLong("current_user_local_id", id)}
    }

    fun getCurrentUserLocalId(): Long {
        return sharedPreferences.getLong("current_user_local_id", -1L)
    }
}