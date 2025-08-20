package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.model.ResetResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ResetCycleCountIfNeededUseCase @Inject constructor(
    private val preferenceManager: PreferenceManager
){
    operator fun invoke(): ResetResult {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = formatter.format(Date())
        val lastDate = preferenceManager.getLastResetDate()

        return if (lastDate == null || lastDate != today) {
            preferenceManager.clearCycleCount()
            preferenceManager.saveLastResetDate(today)
            ResetResult.ResetDone
        } else {
            ResetResult.NoResetNeeded
        }
    }
}