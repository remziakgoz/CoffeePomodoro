package com.remziakgoz.coffeepomodoro.presentation.pomodoro

data class PomodoroUiState(
    val currentState: PomodoroState = PomodoroState.Work,
    val remainingTime: Long = 25 * 60 * 1000L,
    val isRunning: Boolean = false,
    val cupProgress : Float = 1f
)



enum class PomodoroState {
    Work,
    ShortBreak,
    LongBreak,
    Paused
}
