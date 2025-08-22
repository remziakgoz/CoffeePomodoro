package com.remziakgoz.coffeepomodoro.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.remziakgoz.coffeepomodoro.data.sync.DataSyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataSyncManager: DataSyncManager
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private var hasSyncedAfterLogin = false

    private val listener = FirebaseAuth.AuthStateListener { fb ->
        val wasLoggedIn = _isLoggedIn.value
        val isNowLoggedIn = fb.currentUser != null
        _isLoggedIn.value = isNowLoggedIn
        
        if (!wasLoggedIn && isNowLoggedIn && !hasSyncedAfterLogin) {
            Log.d(TAG, "🔄 Auth state changed - triggering initial sync")
            viewModelScope.launch {
                dataSyncManager.performInitialSync()
                hasSyncedAfterLogin = true
            }
        } else if (!isNowLoggedIn) {
            hasSyncedAfterLogin = false
        }
    }

    init { auth.addAuthStateListener(listener) }
    override fun onCleared() { auth.removeAuthStateListener(listener); super.onCleared() }
    
    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        Log.d(TAG, "📧 Signing in with email...")
        auth.signInWithEmailAndPassword(email, password).await()
        Log.d(TAG, "✅ Email sign-in successful")
    }

    suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        Log.d(TAG, "📝 Creating new account...")
        auth.createUserWithEmailAndPassword(email, password).await()
        Log.d(TAG, "✅ Account creation successful")
    }

    suspend fun signInWithGoogle(idToken: String): Result<Unit> = runCatching {
        Log.d(TAG, "🔍 Signing in with Google...")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
        Log.d(TAG, "✅ Google sign-in successful")
    }

    fun logout() {
        viewModelScope.launch {
            // before backup
            runCatching { dataSyncManager.performFinalBackup() }
            // Firebase logout
            auth.signOut()

        }
    }

}