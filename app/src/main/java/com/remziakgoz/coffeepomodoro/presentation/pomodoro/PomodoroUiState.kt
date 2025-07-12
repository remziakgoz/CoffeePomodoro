package com.remziakgoz.coffeepomodoro.presentation.pomodoro

data class PomodoroUiState(
    val currentState: PomodoroState = PomodoroState.Ready,
    val remainingTime: Long = 25 * 60 * 1000L, //
    val isRunning: Boolean = false,
    // Animation state
    val animationProgress: Float = 1f,
    val pausedTime: Long = 0L,
    val pausedTimeReverse: Long = 0L,
    // Cycle tracking for long break
    val cycleCount: Int = 0 // 0-3, after 4th cycle comes long break
)



enum class PomodoroState {
    Work,
    ShortBreak,
    LongBreak,
    Paused,
    Ready
}
