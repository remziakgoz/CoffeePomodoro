package com.remziakgoz.coffeepomodoro.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val userStatsUseCases: UserStatsUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState : StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {

    }

    private fun observeUserStats() {
        viewModelScope.launch {

        }
    }





}