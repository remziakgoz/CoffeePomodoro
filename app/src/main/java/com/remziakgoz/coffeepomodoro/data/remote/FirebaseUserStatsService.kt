package com.remziakgoz.coffeepomodoro.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserStatsService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun backupUserStats(userStats: UserStats) {
        val firebaseId = userStats.firebaseId ?: return
        firestore.collection("users")
            .document(firebaseId)
            .set(userStats)
            .await()
    }

    suspend fun fetchUserStats(firebaseId: String): UserStats? {
        val snapshot = firestore.collection("users")
            .document(firebaseId)
            .get()
            .await()

        return snapshot.toObject(UserStats::class.java)
    }

}