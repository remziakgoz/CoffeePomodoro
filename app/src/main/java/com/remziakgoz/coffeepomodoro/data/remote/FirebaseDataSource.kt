package com.remziakgoz.coffeepomodoro.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.remziakgoz.coffeepomodoro.data.remote.dto.PomodoroSessionDto
import com.remziakgoz.coffeepomodoro.data.remote.dto.toDto
import com.remziakgoz.coffeepomodoro.data.remote.dto.toEntity
import com.remziakgoz.coffeepomodoro.data.local.entities.PomodoroSession
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    companion object {
        private const val COLLECTION_SESSIONS = "pomodoro_sessions"
    }
    
    // ========== Create & Update ==========
    
    suspend fun saveSession(session: PomodoroSession): Result<String> {
        return try {
            val sessionDto = session.toDto()
            firestore.collection(COLLECTION_SESSIONS)
                .document(session.id)
                .set(sessionDto)
                .await()
            Result.success(session.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveSessions(sessions: List<PomodoroSession>): Result<Int> {
        return try {
            val batch = firestore.batch()
            sessions.forEach { session ->
                val sessionDto = session.toDto()
                val docRef = firestore.collection(COLLECTION_SESSIONS).document(session.id)
                batch.set(docRef, sessionDto)
            }
            batch.commit().await()
            Result.success(sessions.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== Read ==========
    
    suspend fun getUserSessions(userId: String): Result<List<PomodoroSession>> {
        return try {
            val querySnapshot = firestore.collection(COLLECTION_SESSIONS)
                .whereEqualTo("user_id", userId)
                .orderBy("completed_at", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val sessions = querySnapshot.documents.mapNotNull { document ->
                document.toObject(PomodoroSessionDto::class.java)?.toEntity()
            }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserSessionsAfter(userId: String, timestamp: Long): Result<List<PomodoroSession>> {
        return try {
            val querySnapshot = firestore.collection(COLLECTION_SESSIONS)
                .whereEqualTo("user_id", userId)
                .whereGreaterThan("updated_at", timestamp)
                .orderBy("updated_at", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val sessions = querySnapshot.documents.mapNotNull { document ->
                document.toObject(PomodoroSessionDto::class.java)?.toEntity()
            }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getUserSessionsRealtime(userId: String): Flow<List<PomodoroSession>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_SESSIONS)
            .whereEqualTo("user_id", userId)
            .orderBy("completed_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }
                
                val sessions = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(PomodoroSessionDto::class.java)?.toEntity()
                } ?: emptyList()
                
                trySend(sessions)
            }
        
        awaitClose { listener.remove() }
    }
    
    // ========== Delete ==========
    
    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_SESSIONS)
                .document(sessionId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAllUserSessions(userId: String): Result<Unit> {
        return try {
            val querySnapshot = firestore.collection(COLLECTION_SESSIONS)
                .whereEqualTo("user_id", userId)
                .get()
                .await()
            
            val batch = firestore.batch()
            querySnapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== Sync Helpers ==========
    
    suspend fun getLastSyncTimestamp(userId: String): Result<Long> {
        return try {
            val querySnapshot = firestore.collection(COLLECTION_SESSIONS)
                .whereEqualTo("user_id", userId)
                .orderBy("updated_at", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
            
            val lastTimestamp = querySnapshot.documents.firstOrNull()
                ?.toObject(PomodoroSessionDto::class.java)
                ?.updatedAt ?: 0L
            
            Result.success(lastTimestamp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun syncUserData(userId: String, localSessions: List<PomodoroSession>): Result<List<PomodoroSession>> {
        return try {
            val remoteResult = getUserSessions(userId)
            if (remoteResult.isFailure) {
                return Result.failure(remoteResult.exceptionOrNull()!!)
            }
            
            val remoteSessions = remoteResult.getOrNull()!!
            val remoteSessionIds = remoteSessions.map { it.id }.toSet()
            
            val sessionsToUpload = localSessions.filter { it.id !in remoteSessionIds }
            if (sessionsToUpload.isNotEmpty()) {
                saveSessions(sessionsToUpload)
            }
            
            val localSessionIds = localSessions.map { it.id }.toSet()
            val sessionsToDownload = remoteSessions.filter { it.id !in localSessionIds }
            
            Result.success(sessionsToDownload)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 