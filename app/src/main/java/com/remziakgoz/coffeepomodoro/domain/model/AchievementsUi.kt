package com.remziakgoz.coffeepomodoro.domain.model

data class AchievementsUi(
    val goalReached: Boolean = false,
    val threeDayStreak: Boolean = false,
    val morningStar: Boolean = false,
    val consistency: Boolean = false
)