package com.remziakgoz.coffeepomodoro.presentation.dashboard

import com.remziakgoz.coffeepomodoro.domain.model.UserStats

data class DashboardUiState(
    val stats: UserStats = UserStats(),
    val isLoading: Boolean = false,
    val isErrorMessage: String? = null,
    val unlockedAchievements: Set<String> = emptySet()
)