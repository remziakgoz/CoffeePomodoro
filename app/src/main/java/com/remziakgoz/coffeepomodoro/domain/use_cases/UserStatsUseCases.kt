package com.remziakgoz.coffeepomodoro.domain.use_cases

data class UserStatsUseCases (
    val getUserStats: GetUserStatsUseCase,
    val updateUserStats: UpdateUserStatsUseCase
)