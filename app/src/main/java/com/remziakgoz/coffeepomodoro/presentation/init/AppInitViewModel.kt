package com.remziakgoz.coffeepomodoro.presentation.init

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remziakgoz.coffeepomodoro.data.sync.DataSyncManager
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppInitViewModel @Inject constructor(
    private val dataSyncManager: DataSyncManager,
    private val userStatsUseCases: UserStatsUseCases
) : ViewModel() {


    fun syncEverything() {
        viewModelScope.launch {
            dataSyncManager.performInitialSync()
            userStatsUseCases.ensureDateWindowsUseCase()
        }
    }
}