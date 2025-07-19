package com.remziakgoz.coffeepomodoro.presentation.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
): ViewModel() {
    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError(it.localizedMessage ?: "Unknown error occurred")
        }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError(it.localizedMessage ?: "Unknown error occurred")
        }
    }

}