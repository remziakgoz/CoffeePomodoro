package com.remziakgoz.coffeepomodoro.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.remziakgoz.coffeepomodoro.domain.model.UserStats
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserStatsService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "FirebaseUserStatsService"
    }

    suspend fun backupUserStats(userStats: UserStats) {
        val firebaseId = userStats.firebaseId ?: return
        
        Log.d(TAG, "🔄 BACKING UP to Firebase - ID: $firebaseId")
        Log.d(TAG, "📊 Backup Data - Total: ${userStats.totalCups}, Weekly: ${userStats.weeklyCups}")
        
        try {
            firestore.collection("users")
                .document(firebaseId)
                .set(userStats)
                .await()
            Log.d(TAG, "✅ BACKUP SUCCESS - Data uploaded to Firebase")
        } catch (e: Exception) {
            Log.e(TAG, "❌ BACKUP FAILED", e)
        }
    }

    suspend fun fetchUserStats(firebaseId: String): UserStats? {
        Log.d(TAG, "🔍 FETCHING from Firebase - ID: $firebaseId")
        
        return try {
            val result = firestore.collection("users")
                .document(firebaseId)
                .get()
                .await()
                .toObject(UserStats::class.java)
            
            if (result != null) {
                Log.d(TAG, "✅ FETCH SUCCESS - Total: ${result.totalCups}, Weekly: ${result.weeklyCups}")
            } else {
                Log.d(TAG, "📭 FETCH RESULT: No data found in Firebase")
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "❌ FETCH FAILED", e)
            null
        }
    }

}