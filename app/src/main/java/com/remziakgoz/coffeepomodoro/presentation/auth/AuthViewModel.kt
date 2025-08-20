package com.remziakgoz.coffeepomodoro.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.remziakgoz.coffeepomodoro.data.local.preferences.PreferenceManager
import com.remziakgoz.coffeepomodoro.data.sync.DataSyncManager
import com.remziakgoz.coffeepomodoro.domain.use_cases.UserStatsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataSyncManager: DataSyncManager
) : ViewModel() {
    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        dataSyncManager.performInitialSync()
    }

    suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
        dataSyncManager.performInitialSync()
    }

    suspend fun signInWithGoogle(idToken: String): Result<Unit> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
        dataSyncManager.performInitialSync()
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            // before backup
            dataSyncManager.performFinalBackup()

            // Firebase logout
            auth.signOut()

            onComplete()
        }
    }

}