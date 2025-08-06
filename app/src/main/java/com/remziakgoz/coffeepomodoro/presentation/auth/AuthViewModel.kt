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
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val preferenceManager: PreferenceManager,
    private val userStatsUseCases: UserStatsUseCases,
    private val dataSyncManager: DataSyncManager
) : ViewModel() {
    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            CoroutineScope(Dispatchers.IO).launch {
                userStatsUseCases.restoreUserStats()
            }
            onSuccess()
        }.addOnFailureListener {
            onError(it.localizedMessage ?: "Unknown error occurred")
        }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val firebaseUser = auth.currentUser
            val firebaseUid = firebaseUser?.uid

            if (firebaseUid != null) {
                val localId = preferenceManager.getCurrentUserLocalId()
                if (localId != -1L){
                    CoroutineScope(Dispatchers.IO).launch {
                       userStatsUseCases.getUserStats(localId).collect { userStats ->
                           val updatedStats = userStats.copy(firebaseId = firebaseUid)
                           userStatsUseCases.updateUserStats(updatedStats)
                       }
                    }
                }
            }

            onSuccess()
        }.addOnFailureListener {
            onError(it.localizedMessage ?: "Unknown error occurred")
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError(it.localizedMessage ?: "Unknown error occurred")
        }
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