package com.remziakgoz.coffeepomodoro.presentation.pomodoro

import com.remziakgoz.coffeepomodoro.domain.model.UserStats

data class PomodoroUiState(
    val stats: UserStats = UserStats(),
    val currentState: PomodoroState = PomodoroState.Ready,
    val remainingTime: Long = 12 * 1000L, //
    val isRunning: Boolean = false,
    // Animation state
    val animationProgress: Float = 1f,
    val pausedTime: Long = 0L,
    val pausedTimeReverse: Long = 0L,
    // Cycle tracking for infinite pomodoro cycles
    val cycleCount: Int = 0 // Counts completed pomodoros, long break every 4 cycles (4, 8, 12...)
)



enum class PomodoroState {
    Work,
    ShortBreak,
    LongBreak,
    Paused,
    Ready
}
