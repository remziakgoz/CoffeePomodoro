package com.remziakgoz.coffeepomodoro.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
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
               userStatsUseCases.getUserStats(localId).collect { userStats ->

                   val unlockedAchievements = mutableSetOf<String>()
                   if (userStats.goldReached) unlockedAchievements.add("Gold Reached")
                   if (userStats.threeDayStreak) unlockedAchievements.add("3-Day Streak")
                   if (userStats.morningStar) unlockedAchievements.add("Morning Star")
                   if (userStats.consistency) unlockedAchievements.add("Consistency")

                   _uiState.value = DashboardUiState(
                       stats = userStats,
                       unlockedAchievements = unlockedAchievements
                   )
               }

           } catch (e: Exception) {
               _uiState.value = DashboardUiState(
                   isErrorMessage = e.message ?: "An unexpected error occurred"
               )
           }
        }
    }
    
}