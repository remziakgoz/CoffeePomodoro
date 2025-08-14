package com.remziakgoz.coffeepomodoro.presentation.dashboard

import com.remziakgoz.coffeepomodoro.domain.model.AchievementsUi
import com.remziakgoz.coffeepomodoro.domain.model.UserStats

data class DashboardUiState(
    val stats: UserStats = UserStats(),
    val isLoading: Boolean = false,
    val isErrorMessage: String? = null,
    val achievements: AchievementsUi = AchievementsUi()
)