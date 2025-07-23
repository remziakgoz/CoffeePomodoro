package com.remziakgoz.coffeepomodoro.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.data.repository.Achievement
import com.remziakgoz.coffeepomodoro.data.repository.DashboardData
import com.remziakgoz.coffeepomodoro.data.repository.PomodoroRepository
import com.remziakgoz.coffeepomodoro.domain.usecase.AddTestSessionUseCase
import com.remziakgoz.coffeepomodoro.domain.usecase.GetAchievementsUseCase
import com.remziakgoz.coffeepomodoro.domain.usecase.GetDashboardDataUseCase
import com.remziakgoz.coffeepomodoro.domain.usecase.SyncUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val getAchievementsUseCase: GetAchievementsUseCase,
    private val syncUserDataUseCase: SyncUserDataUseCase,
    private val addTestSessionUseCase: AddTestSessionUseCase,
    private val repository: PomodoroRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    init {
        loadDashboardData()
        performInitialSync()
        startRealtimeDataObserver()
    }
    
    private fun startRealtimeDataObserver() {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            // Real-time data flow - her değişiklikte otomatik güncelle
            repository.getAllUserSessions(userId).collect { sessions ->
                // Session değişikliği olduğunda dashboard'ı güncelle
                println("🔄 Dashboard: Session data changed, total sessions: ${sessions.size}")
                loadDashboardData()
            }
        }
    }

    fun loadDashboardData() {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val dashboardData = getDashboardDataUseCase(userId)
                val achievements = getAchievementsUseCase(userId)
                
                println("📊 Dashboard loaded: Today=${dashboardData.todayCups}, Week=${dashboardData.weeklyCups}, Month=${dashboardData.monthlyCups}")
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    dashboardData = dashboardData,
                    achievements = achievements,
                    error = null
                )
                
            } catch (e: Exception) {
                println("❌ Dashboard load error: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun refreshData() {
        loadDashboardData()
    }

    private fun performInitialSync() {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            try {
                syncUserDataUseCase.performInitialSync(userId)
                loadDashboardData()
            } catch (e: Exception) {
            }
        }
    }

    fun syncData() {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            
            try {
                val result = syncUserDataUseCase(userId)
                if (result.isSuccess) {
                    loadDashboardData()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Synchronization failed: ${e.message}"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isSyncing = false)
            }
        }
    }

    fun addTestSession(cupsCount: Int = 1) {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            try {
                addTestSessionUseCase(userId, cupsCount)
                loadDashboardData()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Session could not be added: ${e.message}"
                )
            }
        }
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val dashboardData: DashboardData = DashboardData(),
    val achievements: List<Achievement> = emptyList(),
    val error: String? = null
) {
    val weeklyProgressPercentage: Int
        get() = (dashboardData.weeklyProgress * 100).toInt()
    
    val dailyProgressPercentage: Int
        get() = (dashboardData.dailyProgress * 100).toInt()
    
    val isGoalReached: Boolean
        get() = achievements.contains(Achievement.GOAL_REACHED)
    
    val hasThreeDayStreak: Boolean
        get() = achievements.contains(Achievement.THREE_DAY_STREAK)
    
    val isMorningStar: Boolean
        get() = achievements.contains(Achievement.MORNING_STAR)
    
    val isConsistent: Boolean
        get() = achievements.contains(Achievement.CONSISTENCY)
    
    val dailyProgressData: List<Int>
        get() = dashboardData.getLast7DaysAsIntList()
} 