package com.remziakgoz.coffeepomodoro.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.domain.model.AchievementsUi
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import com.remziakgoz.coffeepomodoro.utils.getCurrentDayIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userStatsUseCases: UserStatsUseCases,
    private val preferenceManager: PreferenceManager,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState : StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (firebaseAuth.currentUser == null) {
            // User logged out - let the flow handle the state naturally
            android.util.Log.d("DashboardViewModel", "🚪 Auth state: User logged out - flow will handle state")
            // Don't manually reset UI state here to preserve reactive binding
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
        observeUserStats()
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authStateListener)
        super.onCleared()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeUserStats() {
        viewModelScope.launch {
            userStatsUseCases.getUserStats()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isErrorMessage = e.message ?: "An unexpected error occurred"
                    )
                }
                .collect { stats ->
                    if (stats == null) {
                        // Handle null state - could be initial load or data cleared after logout
                        android.util.Log.d("DashboardViewModel", "📭 UserStats is null - resetting dashboard")
                        _uiState.value = DashboardUiState() // Reset to initial state
                        return@collect
                    }
                    val calc = computeLevel(stats)

                    val lastSeen = preferenceManager.getLastSeenLevel()
                    val displayLevel = max(lastSeen, calc.meta.level)
                    val justLeveled = displayLevel > lastSeen
                    val hasPendingLevelUp = preferenceManager.getPendingLevelUp()
                    
                    // Check if there's a level up (either new or pending from previous session)
                    val shouldShowLevelUp = justLeveled || hasPendingLevelUp
                    
                    if (justLeveled) {
                        preferenceManager.setLastSeenLevel(displayLevel)
                        // Set pending level up flag - will be cleared when animation is consumed
                        preferenceManager.setPendingLevelUp(true)
                    }

                    val displayMeta = levelMeta[displayLevel - 1]
                    val (nextTotal, remainCups, remainDays) =
                        nextRequirementsForDisplayLevel(displayLevel, stats)

                    _uiState.value = _uiState.value.copy(
                        stats = stats,
                        achievements = mapAchievements(stats),
                        quickDailyAvg = calcDailyAvg(stats),
                        isLoading = false,
                        isErrorMessage = null,
                        level = displayLevel,
                        levelTitle = displayMeta.title,
                        levelIconRes = displayMeta.iconRes,
                        nextTargetTotal = nextTotal,
                        remainingToNext = remainCups,
                        remainingToNextDays = remainDays,
                        justLeveledUp = shouldShowLevelUp,
                        shouldShowLevelUpAnimation = false
                    )
                }
        }
    }

    private fun mapAchievements(stats: UserStats): AchievementsUi {
        val goalReached   = stats.weeklyCups >= stats.weeklyGoal
        val threeStreak   = stats.currentStreak >= 3
        val consistency   = stats.dailyData.count { it > 0 } >= 5
        val morningStar   = stats.morningStar
        return AchievementsUi(
            goalReached = goalReached,
            threeDayStreak = threeStreak,
            morningStar = morningStar,
            consistency = consistency
        )
    }

    private fun calcDailyAvg(stats: UserStats): Float {
        val activeDaysSoFar = stats.totalDays + if (stats.todayCups > 0) 1 else 0
        if (activeDaysSoFar > 0) {
            return (stats.totalCups.toFloat() / activeDaysSoFar).coerceAtLeast(0f)
        }
        val daysElapsedThisWeek = (getCurrentDayIndex() + 1).coerceIn(1, 7)
        return (stats.weeklyCups.toFloat() / daysElapsedThisWeek).coerceAtLeast(0f)
    }

    private data class LevelMeta(
        val level: Int, val title: String, val iconRes: Int
    )

    private val levelMeta = listOf(
        LevelMeta(1, "Babyccino", R.drawable.cuplevel0),
        LevelMeta(2, "Espresso Master", R.drawable.cuplevel1),
        LevelMeta(3, "Cappuccino Champion", R.drawable.cuplevel2),
        LevelMeta(4, "Latte Legend", R.drawable.cuplevel3),
        LevelMeta(5, "Mocha Monarch", R.drawable.cuplevel4),
        LevelMeta(6, "Coffee Conqueror", R.drawable.cuplevel5)
    )

    private data class LevelCalc(
        val meta: LevelMeta,
        val nextTargetTotal: Int,
        val remainingCups: Int,
        val remainingDays: Int
    )

    private fun computeLevel(stats: UserStats): LevelCalc {
        val total = stats.totalCups
        val streak = stats.currentStreak
        val metWeekly = stats.weeklyCups >= stats.weeklyGoal

        return when {
            total >= 1000 -> {
                val meta = levelMeta[5] // L5
                LevelCalc(meta, nextTargetTotal = 1000, remainingCups = 0, remainingDays = 0)
            }

            total >= 500 && streak >= 30 -> {
                val meta = levelMeta[4] // L4
                LevelCalc(
                    meta = meta,
                    nextTargetTotal = 1000,
                    remainingCups = (1000 - total).coerceAtLeast(0),
                    remainingDays = 0
                )
            }

            total >= 250 && streak >= 21 -> {
                val meta = levelMeta[3] // L3
                LevelCalc(
                    meta = meta,
                    nextTargetTotal = 500,
                    remainingCups = (500 - total).coerceAtLeast(0),
                    remainingDays = (30 - streak).coerceAtLeast(0)
                )
            }

            total >= 50 && metWeekly -> {
                val meta = levelMeta[2] // L2
                LevelCalc(
                    meta = meta,
                    nextTargetTotal = 250,
                    remainingCups = (250 - total).coerceAtLeast(0),
                    remainingDays = (21 - streak).coerceAtLeast(0)
                )
            }

            total >= 10 -> {
                val meta = levelMeta[1]
                LevelCalc(
                    meta = meta,
                    nextTargetTotal = 50,
                    remainingCups = (50 - total).coerceAtLeast(0),
                    remainingDays = 0
                )
            }

            else -> {
                val meta = levelMeta[0] // L1
                LevelCalc(
                    meta = meta,
                    nextTargetTotal = 10,
                    remainingCups = (10 - total).coerceAtLeast(0),
                    remainingDays = 0
                )
            }
        }
    }

    private fun nextRequirementsForDisplayLevel(displayLevel: Int, stats: UserStats): Triple<Int, Int, Int> {
        val total = stats.totalCups
        val streakDays = stats.currentStreak
        return when (displayLevel) {
            1 -> Triple(10, (10 - total).coerceAtLeast(0), 0)
            2 -> Triple(50, (50 - total).coerceAtLeast(0), 0)
            3 -> Triple (250, (250 - total).coerceAtLeast(0), (21 - streakDays).coerceAtLeast(0))
            4 -> Triple (500, (500 - total).coerceAtLeast(0), (30 - streakDays).coerceAtLeast(0))
            5 -> Triple(1000, (1000 - total).coerceAtLeast(0), 0)
            else -> Triple (1000, 0, 0)
        }
    }

    fun consumeLevelUpAnimation() {
        val cur = _uiState.value
        if (cur.shouldShowLevelUpAnimation) {
            // Clear the pending level up flag from preferences
            preferenceManager.setPendingLevelUp(false)
            _uiState.value = cur.copy(
                justLeveledUp = false,
                shouldShowLevelUpAnimation = false
            )
        }
    }

    fun onDashboardBecameVisible() {
        val cur = _uiState.value
        if (cur.justLeveledUp && !cur.shouldShowLevelUpAnimation) {
            _uiState.value = cur.copy(shouldShowLevelUpAnimation = true)
        }
    }

    fun refreshDashboard() {
        android.util.Log.d("DashboardViewModel", "🔄 Manual dashboard refresh triggered")
        _uiState.value = DashboardUiState()
        // Re-trigger data observation (flow will emit immediately if data exists)
    }

} 