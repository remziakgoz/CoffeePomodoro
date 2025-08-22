package com.remziakgoz.coffeepomodoro.presentation.init

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.remziakgoz.coffeepomodoro.data.sync.DataSyncManager
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppInitViewModel @Inject constructor(
    private val dataSyncManager: DataSyncManager,
    private val userStatsUseCases: UserStatsUseCases,
    private val auth: FirebaseAuth
) : ViewModel() {

    companion object {
        private const val TAG = "AppInitViewModel"
    }

    fun syncEverything() {
        viewModelScope.launch {
            Log.d(TAG, "🔧 App initialization sync triggered")
            
            if (auth.currentUser != null) {
                Log.d(TAG, "👤 User already logged in - skipping duplicate sync (will be handled by AuthViewModel)")
            } else {
                Log.d(TAG, "👻 No user logged in - performing offline initialization")
                userStatsUseCases.initializeLocalUserUseCase()
            }
            
            userStatsUseCases.ensureDateWindowsUseCase()
            Log.d(TAG, "✅ App initialization completed")
        }
    }
}