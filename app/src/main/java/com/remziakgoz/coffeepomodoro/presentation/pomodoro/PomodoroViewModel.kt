package com.remziakgoz.coffeepomodoro.presentation.pomodoro

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PomodoroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()


    fun startPomodoro() {
        val remainingTime = when (_uiState.value.currentState) {
            PomodoroState.Work -> 1 * 60 * 1000L
            PomodoroState.ShortBreak -> 1 * 60 * 1000L
            PomodoroState.LongBreak -> 1 * 60 * 1000L
            else -> 1 * 60 * 1000L
        }
        _uiState.value = _uiState.value.copy(isRunning = true, currentState = PomodoroState.Work, remainingTime = remainingTime, cupProgress = 0f)
    }

    fun pausePomodoro() {
        _uiState.value = _uiState.value.copy(isRunning = false, currentState = PomodoroState.Paused)
    }

    fun resumePomodoro() {
        _uiState.value = _uiState.value.copy(isRunning = true, currentState = PomodoroState.Work)
    }

    fun resetPomodoro() {
        _uiState.value = PomodoroUiState()
    }

}