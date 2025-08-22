package com.remziakgoz.coffeepomodoro.presentation.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.model.ResetResult
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.use_cases.ResetCycleCountIfNeededUseCase
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import com.remziakgoz.coffeepomodoro.utils.getCurrentDateString
import com.remziakgoz.coffeepomodoro.utils.getCurrentDayIndex
import com.remziakgoz.coffeepomodoro.utils.getCurrentMonthString
import com.remziakgoz.coffeepomodoro.utils.getMondayOfCurrentWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val resetCycleCountIfNeededUseCase: ResetCycleCountIfNeededUseCase,
    private val userStatsUseCases: UserStatsUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState(
        cycleCount = preferenceManager.getCycleCount()
    ))
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var animationJob: Job? = null
    private val pomodoroTime = 12 * 1000L // 25 minutes
    private val breakTime = 4 * 1000L // 5 minutes
    private val longBreakTime = 8 * 60 * 1000L // 15 minutes

    // Call this when screen is opened to initialize proper state
    fun initializeScreenState() {
        when (resetCycleCountIfNeededUseCase()) {
            is ResetResult.ResetDone -> {
               _uiState.value = _uiState.value.copy(cycleCount = 0)
            }
            is ResetResult.NoResetNeeded -> {

            }
        }

        when (_uiState.value.currentState) {
            PomodoroState.Ready -> {
                // Reset to initial state
                _uiState.value = PomodoroUiState(
                    cycleCount = preferenceManager.getCycleCount()
                )
            }
            PomodoroState.Work -> {
                // If we're in work state, set appropriate animation progress
                val elapsed = pomodoroTime - _uiState.value.remainingTime
                val progress = 1f - (elapsed.toFloat() / pomodoroTime)
                _uiState.value = _uiState.value.copy(
                    animationProgress = progress.coerceIn(0f, 1f)
                )
            }
            PomodoroState.Paused -> {
                // Keep current state but make sure animation is paused
                val elapsed = pomodoroTime - _uiState.value.remainingTime
                val progress = 1f - (elapsed.toFloat() / pomodoroTime)
                _uiState.value = _uiState.value.copy(
                    animationProgress = progress.coerceIn(0f, 1f),
                    pausedTime = elapsed
                )
            }
            PomodoroState.ShortBreak -> {
                // Set animation progress for break (reverse animation)
                val elapsed = breakTime - _uiState.value.remainingTime
                val progress = elapsed.toFloat() / breakTime
                _uiState.value = _uiState.value.copy(
                    animationProgress = progress.coerceIn(0f, 1f),
                    pausedTimeReverse = if (!_uiState.value.isRunning) elapsed else 0L
                )
            }
            PomodoroState.LongBreak -> {
                // Set animation progress for long break (reverse animation)
                val elapsed = longBreakTime - _uiState.value.remainingTime
                val progress = elapsed.toFloat() / longBreakTime
                _uiState.value = _uiState.value.copy(
                    animationProgress = progress.coerceIn(0f, 1f),
                    pausedTimeReverse = if (!_uiState.value.isRunning) elapsed else 0L
                )
            }
            else -> {}
        }
    }

    fun toggleTimer() {
        when (_uiState.value.currentState) {
            PomodoroState.Ready -> startPomodoro()
            PomodoroState.Work -> {
                if (_uiState.value.isRunning) {
                    pausePomodoro()
                } else {
                    resumePomodoro()
                }
            }
            PomodoroState.Paused -> resumePomodoro()
            PomodoroState.ShortBreak -> {
                if (_uiState.value.isRunning) {
                    pauseBreak()
                } else {
                    // Check if break was already started before
                    if (_uiState.value.remainingTime < breakTime) {
                        // Break was paused, resume it
                        resumeBreak()
                    } else {
                        // First time starting break
                        startBreak()
                    }
                }
            }
            PomodoroState.LongBreak -> {
                if (_uiState.value.isRunning) {
                    pauseLongBreak()
                } else {
                    // Check if long break was already started before
                    if (_uiState.value.remainingTime < longBreakTime) {
                        // Long break was paused, resume it
                        resumeLongBreak()
                    } else {
                        // First time starting long break
                        startLongBreak()
                    }
                }
            }
            else -> {}
        }
    }

    private fun startPomodoro() {
        timerJob?.cancel()
        animationJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isRunning = true,
            currentState = PomodoroState.Work,
            remainingTime = pomodoroTime,
            animationProgress = 1f,
            pausedTime = 0L,
            pausedTimeReverse = 0L
        )
        startTimer()
        startForwardAnimation()
    }

    private fun pausePomodoro() {
        timerJob?.cancel()
        animationJob?.cancel()
        // Calculate elapsed time from current animation progress for better precision
        val elapsed = ((1f - _uiState.value.animationProgress) * pomodoroTime).toLong()
        _uiState.value = _uiState.value.copy(
            isRunning = false,
            currentState = PomodoroState.Paused,
            pausedTime = elapsed
        )
    }

    private fun resumePomodoro() {
        _uiState.value = _uiState.value.copy(
            isRunning = true,
            currentState = PomodoroState.Work
        )
        startTimer()
        startForwardAnimation()
    }

    private fun startBreak() {
        timerJob?.cancel()
        animationJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isRunning = true,
            currentState = PomodoroState.ShortBreak,
            remainingTime = breakTime,
            animationProgress = 0f,
            pausedTime = 0L,
            pausedTimeReverse = 0L
        )
        startTimer()
        startReverseAnimation(breakTime)
    }

    private fun pauseBreak() {
        timerJob?.cancel()
        animationJob?.cancel()
        // Calculate elapsed time from current animation progress for better precision
        val elapsed = (_uiState.value.animationProgress * breakTime).toLong()
        _uiState.value = _uiState.value.copy(
            isRunning = false,
            pausedTimeReverse = elapsed
        )
    }

    private fun resumeBreak() {
        _uiState.value = _uiState.value.copy(
            isRunning = true,
            currentState = PomodoroState.ShortBreak
        )
        startTimer()
        startReverseAnimation(breakTime)
    }

    private fun startLongBreak() {
        timerJob?.cancel()
        animationJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isRunning = true,
            currentState = PomodoroState.LongBreak,
            remainingTime = longBreakTime,
            animationProgress = 0f,
            pausedTime = 0L,
            pausedTimeReverse = 0L
        )
        startTimer()
        startReverseAnimation(longBreakTime)
    }

    private fun pauseLongBreak() {
        timerJob?.cancel()
        animationJob?.cancel()
        // Calculate elapsed time from current animation progress for better precision
        val elapsed = (_uiState.value.animationProgress * longBreakTime).toLong()
        _uiState.value = _uiState.value.copy(
            isRunning = false,
            pausedTimeReverse = elapsed
        )
    }

    private fun resumeLongBreak() {
        _uiState.value = _uiState.value.copy(
            isRunning = true,
            currentState = PomodoroState.LongBreak
        )
        startTimer()
        startReverseAnimation(longBreakTime)
    }

    private fun startForwardAnimation() {
        animationJob = viewModelScope.launch {
            val startTime = System.nanoTime() - (_uiState.value.pausedTime * 1_000_000L)

            while (_uiState.value.isRunning && _uiState.value.animationProgress > 0f) {
                val currentTime = System.nanoTime()
                val elapsed = (currentTime - startTime) / 1_000_000L // Convert to milliseconds
                val progress = 1f - (elapsed.toFloat() / pomodoroTime)

                _uiState.value = _uiState.value.copy(
                    animationProgress = progress.coerceIn(0f, 1f)
                )

                if (progress <= 0f) {
                    break
                }
                delay(16)
            }
        }
    }

    private fun startReverseAnimation(duration: Long) {
        animationJob = viewModelScope.launch {
            val startTime = System.nanoTime() - (_uiState.value.pausedTimeReverse * 1_000_000L)

            while (_uiState.value.isRunning && _uiState.value.animationProgress < 1f) {
                val currentTime = System.nanoTime()
                val elapsed = (currentTime - startTime) / 1_000_000L // Convert to milliseconds
                val progress = elapsed.toFloat() / duration

                _uiState.value = _uiState.value.copy(
                    animationProgress = progress.coerceIn(0f, 1f)
                )

                if (progress >= 1f) {
                    break
                }
                delay(16)
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingTime > 0 && _uiState.value.isRunning) {
                delay(1000)
                val newTime = _uiState.value.remainingTime - 1000

                _uiState.value = _uiState.value.copy(
                    remainingTime = newTime
                )

                if (newTime <= 0) {
                    completeCurrentSession()
                    break
                }
            }
        }
    }

    private fun completeCurrentSession() {
        animationJob?.cancel()
        when (_uiState.value.currentState) {
            PomodoroState.Work -> {
                // After work, determine break type based on CURRENT cycle count
                val currentCycleCount = _uiState.value.cycleCount

                if ((currentCycleCount + 1) % 4 == 0) {
                    // Every 4 cycles, long break time! (4, 8, 12, 16...)
                    _uiState.value = _uiState.value.copy(
                        isRunning = false,
                        currentState = PomodoroState.LongBreak,
                        remainingTime = longBreakTime,
                        animationProgress = 0f,
                        pausedTime = 0L,
                        pausedTimeReverse = 0L
                        // Keep cycleCount unchanged - will increment after break
                    )
                } else {
                    // Regular short break
                    _uiState.value = _uiState.value.copy(
                        isRunning = false,
                        currentState = PomodoroState.ShortBreak,
                        remainingTime = breakTime,
                        animationProgress = 0f,
                        pausedTime = 0L,
                        pausedTimeReverse = 0L
                        // Keep cycleCount unchanged - will increment after break
                    )
                }
            }
            PomodoroState.ShortBreak, PomodoroState.LongBreak -> {
                // Increment cycle count AFTER completing break
                val newCount = _uiState.value.cycleCount + 1
                _uiState.value = _uiState.value.copy(
                    isRunning = false,
                    currentState = PomodoroState.Ready,
                    remainingTime = pomodoroTime,
                    animationProgress = 1f,
                    pausedTime = 0L,
                    pausedTimeReverse = 0L,
                    cycleCount = newCount
                )
                preferenceManager.saveCycleCount(newCount)

                viewModelScope.launch {
                    userStatsUseCases.getUserStats().first()?.let { stats ->
                        updateDrinkStatsOnCycleComplete(stats)
                    }
                }
            }
            else -> {}
        }
    }

    fun nextStep() {
        // Cancel any running timers and animations
        timerJob?.cancel()
        animationJob?.cancel()

        // Move to next step regardless of timer state
        when (_uiState.value.currentState) {
            PomodoroState.Ready -> {
                // From Ready, determine break type based on current cycle count
                val currentCycleCount = _uiState.value.cycleCount

                if ((currentCycleCount + 1) % 4 == 0) {
                    // Go to long break
                    _uiState.value = _uiState.value.copy(
                        isRunning = false,
                        currentState = PomodoroState.LongBreak,
                        remainingTime = longBreakTime,
                        animationProgress = 0f,
                        pausedTime = 0L,
                        pausedTimeReverse = 0L
                        // Don't increment cycle count yet
                    )
                } else {
                    // Go to short break
                    _uiState.value = _uiState.value.copy(
                        isRunning = false,
                        currentState = PomodoroState.ShortBreak,
                        remainingTime = breakTime,
                        animationProgress = 0f,
                        pausedTime = 0L,
                        pausedTimeReverse = 0L
                        // Don't increment cycle count yet
                    )
                }
            }
            PomodoroState.Work, PomodoroState.Paused -> {
                // From Work/Paused, go to appropriate break based on current cycle count
                val currentCycleCount = _uiState.value.cycleCount

                if ((currentCycleCount + 1) % 4 == 0) {
                    // Go to long break
                    _uiState.value = _uiState.value.copy(
                        isRunning = false,
                        currentState = PomodoroState.LongBreak,
                        remainingTime = longBreakTime,
                        animationProgress = 0f,
                        pausedTime = 0L,
                        pausedTimeReverse = 0L
                        // Don't increment cycle count yet
                    )
                } else {
                    // Go to short break
                    _uiState.value = _uiState.value.copy(
                        isRunning = false,
                        currentState = PomodoroState.ShortBreak,
                        remainingTime = breakTime,
                        animationProgress = 0f,
                        pausedTime = 0L,
                        pausedTimeReverse = 0L
                        // Don't increment cycle count yet
                    )
                }
            }
            PomodoroState.ShortBreak -> {
                // From ShortBreak, go to next pomodoro and increment cycle count
                val newCount = _uiState.value.cycleCount + 1
                _uiState.value = _uiState.value.copy(
                    isRunning = false,
                    currentState = PomodoroState.Ready,
                    remainingTime = pomodoroTime,
                    animationProgress = 1f,
                    pausedTime = 0L,
                    pausedTimeReverse = 0L,
                    cycleCount = newCount
                )
                preferenceManager.saveCycleCount(newCount)

                viewModelScope.launch {
                    userStatsUseCases.getUserStats().firstOrNull()?.let { stats ->
                        updateDrinkStatsOnCycleComplete(stats)
                    }
                }
            }
            PomodoroState.LongBreak -> {
                // From LongBreak, go to next pomodoro and increment cycle count
                val newCount = _uiState.value.cycleCount + 1
                _uiState.value = _uiState.value.copy(
                    isRunning = false,
                    currentState = PomodoroState.Ready,
                    remainingTime = pomodoroTime,
                    animationProgress = 1f,
                    pausedTime = 0L,
                    pausedTimeReverse = 0L,
                    cycleCount = newCount
                )
                preferenceManager.saveCycleCount(newCount)

                viewModelScope.launch {
                    userStatsUseCases.getUserStats().firstOrNull()?.let { stats ->
                        updateDrinkStatsOnCycleComplete(stats)
                    }
                }
            }
        }
    }

    fun restart() {
        // Cancel any running timers and animations
        timerJob?.cancel()
        animationJob?.cancel()

        // Reset current session based on state, but keep cycle count
        val current = _uiState.value.currentState

        val (targetState, targetTime, targetAnim) = when (current) {
            PomodoroState.Work, PomodoroState.Paused, PomodoroState.Ready -> {
                Triple(PomodoroState.Ready, pomodoroTime, 1f)
            }
            PomodoroState.ShortBreak -> Triple(PomodoroState.ShortBreak, breakTime, 0f)
            PomodoroState.LongBreak -> Triple(PomodoroState.LongBreak, longBreakTime, 0f)
        }

        _uiState.value = _uiState.value.copy(
            currentState = targetState,
            remainingTime = targetTime,
            isRunning = false,
            animationProgress = targetAnim,
            pausedTime = 0L,
            pausedTimeReverse = 0L
        )
    }

    private fun updateDrinkStatsOnCycleComplete(currentStats: UserStats) {
        val now = System.currentTimeMillis()
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val today = getCurrentDateString()
        val monday = getMondayOfCurrentWeek()
        val month = getCurrentMonthString()

        val sameDay   = (today == currentStats.todayDate)
        val sameWeek  = (monday == currentStats.currentWeekStart)
        val sameMonth = (month == currentStats.currentMonth)

        val idx = getCurrentDayIndex()

        val baseDaily = if (currentStats.dailyData.size == 7) currentStats.dailyData else List(7) { 0 }
        val newDaily = when {
            !sameWeek -> MutableList(7) { 0 }.apply { this[idx] = 1 }
            !sameDay  -> baseDaily.toMutableList().apply { this[idx] = 1 }
            else      -> baseDaily.toMutableList().apply { this[idx] = this[idx] + 1}
        }

        val newToday = if (sameDay) currentStats.todayCups + 1 else 1
        val newCurrentStreak = if(!sameDay) {
            if (currentStats.todayCups > 0) currentStats.currentStreak + 1 else 0
        } else {
            currentStats.currentStreak
        }
        val newBestStreak = maxOf(currentStats.bestStreak, newCurrentStreak)

        val morningStar = when {
            !sameDay -> false
            newToday == 1 && hour < 10 -> true
            else -> currentStats.morningStar
        }


        val updated = currentStats.copy(
            todayCups = newToday,
            todayDate = today,

            weeklyCups = if (sameWeek) currentStats.weeklyCups + 1 else 1,
            currentWeekStart = monday,

            monthlyCups = if (sameMonth) currentStats.monthlyCups + 1 else 1,
            currentMonth = month,

            dailyData = newDaily,

            currentStreak = newCurrentStreak,
            bestStreak = newBestStreak,

            morningStar = morningStar,

            totalCups = currentStats.totalCups + 1,
            lastUpdated = now
        )

        viewModelScope.launch { userStatsUseCases.updateUserStats(updated) }

    }

    fun resetEverything() {
        // Cancel any running timers and animations
        timerJob?.cancel()
        animationJob?.cancel()

        // Reset everything to initial state
        preferenceManager.saveCycleCount(0)
        _uiState.value = PomodoroUiState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        animationJob?.cancel()
    }
}