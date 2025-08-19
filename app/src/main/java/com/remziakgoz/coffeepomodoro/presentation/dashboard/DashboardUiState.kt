package com.remziakgoz.coffeepomodoro.presentation.dashboard

import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.domain.model.AchievementsUi
import com.remziakgoz.coffeepomodoro.domain.model.UserStats

data class DashboardUiState(
    val stats: UserStats = UserStats(),
    val isLoading: Boolean = false,
    val isErrorMessage: String? = null,
    val achievements: AchievementsUi = AchievementsUi(),
    val quickDailyAvg: Float = 0f,
    val level: Int = 1,
    val levelTitle: String = "Babyccino",
    val levelIconRes: Int = R.drawable.cuplevel0,
    val nextTargetTotal: Int = 50,
    val remainingToNext: Int = 50,
    val justLeveledUp: Boolean = false,
    val remainingToNextDays: Int = 0

)