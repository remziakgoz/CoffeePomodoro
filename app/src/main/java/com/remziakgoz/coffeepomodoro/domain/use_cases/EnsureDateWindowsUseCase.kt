package com.remziakgoz.coffeepomodoro.domain.use_cases

import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.repository.UserStatsRepository
import com.remziakgoz.coffeepomodoro.utils.getCurrentDateString
import com.remziakgoz.coffeepomodoro.utils.getCurrentDayIndex
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

        val sameDay = (today == stats.todayDate)
        val sameWeek = (monday == stats.currentWeekStart)
        val sameMonth = (month == stats.currentMonth)

        val todayIdx = getCurrentDayIndex()

        val baseDaily = if (stats.dailyData.size == 7) stats.dailyData else List(7) { 0 }
        val newDailyData: List<Int> = when {
            !sameWeek -> List(7) { 0 }
            !sameDay  -> baseDaily.toMutableList().apply { this[todayIdx] = 0 }
            else      -> baseDaily
        }

        val newCurrentStreak = if (!sameDay) {
            if (stats.todayCups > 0) stats.currentStreak + 1 else 0
        } else {
            stats.currentStreak
        }
        val newBestStreak = maxOf(stats.bestStreak, newCurrentStreak)

        val newMorningStar = if (sameDay) stats.morningStar else false

        val incTotalDays = if (!sameDay && stats.todayCups > 0) stats.totalDays + 1 else stats.totalDays
        val newDailyAverage = if (incTotalDays > 0) stats.totalCups.toFloat() / incTotalDays else 0f

        val updatedStats = stats.copy(
            todayCups = if (sameDay) stats.todayCups else 0,
            todayDate = today,

            weeklyCups = if (sameWeek) stats.weeklyCups else 0,
            currentWeekStart = monday,

            monthlyCups = if (sameMonth) stats.monthlyCups else 0,
            currentMonth = month,

            dailyData = newDailyData,

            currentStreak = newCurrentStreak,
            bestStreak = newBestStreak,
            morningStar = newMorningStar,

            totalDays = incTotalDays,
            dailyAverage = newDailyAverage
        )

        if (updatedStats != stats) {
            userStatsRepository.updateUserStats(updatedStats)
        }
    }
}