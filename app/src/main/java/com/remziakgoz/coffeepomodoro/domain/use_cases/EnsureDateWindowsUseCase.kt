package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import com.remziakgoz.coffeepomodoro.utils.getCurrentDateString
import com.remziakgoz.coffeepomodoro.utils.getCurrentMonthString
import com.remziakgoz.coffeepomodoro.utils.getMondayOfCurrentWeek
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class EnsureDateWindowsUseCase @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val userStatsRepository: UserStatsRepository
) {
    suspend operator fun invoke() {
        val localId = preferenceManager.getCurrentUserLocalId()
        val stats = userStatsRepository.getUserStatsFlow(localId).firstOrNull() ?: return

        val today = getCurrentDateString()
        val monday = getMondayOfCurrentWeek()
        val month = getCurrentMonthString()

        val updatedStats = stats.copy(
            todayCups = if (today != stats.todayDate) 0 else stats.todayCups,
            todayDate = today,
            weeklyCups = if (monday != stats.currentWeekStart) 0 else stats.weeklyCups,
            currentWeekStart = monday,
            monthlyCups = if (month != stats.currentMonth) 0 else stats.monthlyCups,
            currentMonth = month
        )

        if (updatedStats != stats) {
            userStatsRepository.updateUserStats(updatedStats)
        }
    }

}