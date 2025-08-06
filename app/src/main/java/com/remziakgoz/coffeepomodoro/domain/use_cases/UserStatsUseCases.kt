package com.remziakgoz.coffeepomodoro.domain.use_cases

data class UserStatsUseCases (
    val getUserStats: GetUserStatsUseCase,
    val updateUserStats: UpdateUserStatsUseCase,
    val initializeLocalUserUseCase: InitializeLocalUserUseCase,
    val syncFirebaseUser: SyncFirebaseUserUseCase,
    val backupUserStats: BackupUserStatsUseCase,
    val restoreUserStats: RestoreUserStatsUseCase
)