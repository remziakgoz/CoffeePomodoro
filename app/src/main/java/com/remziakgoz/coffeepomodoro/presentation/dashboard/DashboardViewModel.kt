package com.remziakgoz.coffeepomodoro.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.model.AchievementsUi
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import com.remziakgoz.coffeepomodoro.utils.getCurrentDayIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userStatsUseCases: UserStatsUseCases,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState : StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeUserStats()
    }

    private fun observeUserStats() {
        val localId = preferenceManager.getCurrentUserLocalId()
        if (localId == -1L) {
            _uiState.value = DashboardUiState(isErrorMessage = "Local ID not found")
            return
        }
        viewModelScope.launch {
           try {
               userStatsUseCases.getUserStats(localId).collect { stats ->
                   _uiState.value = _uiState.value.copy(
                       stats = stats,
                       achievements = mapAchievements(stats),
                       quickDailyAvg = calcDailyAvg(stats),
                       isLoading = false,
                       isErrorMessage = null
                   )
               }
           } catch (e: Exception) {
               _uiState.value = _uiState.value.copy(
                   isErrorMessage = e.message ?: "An unexpected error occurred",
                   isLoading = false
               )
           }
        }
    }

    private fun mapAchievements(stats: UserStats): AchievementsUi {
        val goalReached   = stats.weeklyCups >= stats.weeklyGoal
        val threeStreak   = stats.currentStreak >= 3
        val consistency   = stats.dailyData.count { it > 0 } >= 5
        val morningStar   = stats.morningStar
        return AchievementsUi(
            goalReached = goalReached,
            threeDayStreak = threeStreak,
            morningStar = morningStar,
            consistency = consistency
        )
    }

    private fun calcDailyAvg(stats: UserStats): Float {
        val activeDaysSoFar = stats.totalDays + if (stats.todayCups > 0) 1 else 0
        if (activeDaysSoFar > 0) {
            return (stats.totalCups.toFloat() / activeDaysSoFar).coerceAtLeast(0f)
        }
        val daysElapsedThisWeek = (getCurrentDayIndex() + 1).coerceIn(1, 7)
        return (stats.weeklyCups.toFloat() / daysElapsedThisWeek).coerceAtLeast(0f)
    }
    
}