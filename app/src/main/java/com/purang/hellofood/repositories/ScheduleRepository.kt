package com.purang.hellofood.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.purang.hellofood.models.ScheduleData
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ScheduleRepository @Inject constructor(private val firestore: FirebaseFirestore) {
    private val scheduleCollection = firestore.collection("schedules")

    // 연도와 월을 추가하여 해당하는 데이터만 가져오기
    /*fun getUserSchedules(userId: String, year: Int, month: Int): Flow<List<ScheduleData>> {
        return callbackFlow {
            val listener = scheduleCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("year", year)
                .whereEqualTo("month", month)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val schedules = snapshot.documents.mapNotNull { it.toObject(ScheduleData::class.java) }
                        trySend(schedules).isSuccess
                    }
                }

            awaitClose { listener.remove() }
        }
    }*/
    suspend fun getUserSchedules(userId: String, year: Int, month: Int): List<ScheduleData> {
        val result = scheduleCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("year", year)
            .whereEqualTo("month", month)
            .get()
            .await() // 비동기 작업을 동기적으로 변환

        return result.documents.mapNotNull { it.toObject(ScheduleData::class.java) }
    }

    suspend fun addSchedule(schedule: ScheduleData): Boolean {
        return try {
            scheduleCollection.add(schedule).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteSchedule(scheduleId: String): Boolean {
        return try {
            scheduleCollection.document(scheduleId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}


/*
class ScheduleRepository @Inject constructor(private val firestore: FirebaseFirestore) {
    private val scheduleCollection = firestore.collection("schedules")

    fun getUserSchedules(userId: String, onResult: (List<ScheduleData>) -> Unit) {
        scheduleCollection.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val schedules = result.documents.mapNotNull { it.toObject(ScheduleData::class.java) }
                onResult(schedules)
            }
    }

    fun addSchedule(schedule: ScheduleData, onComplete: (Boolean) -> Unit) {
        scheduleCollection
            .add(schedule)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteSchedule(scheduleId: String, onComplete: (Boolean) -> Unit) {
        scheduleCollection.document(scheduleId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}*/


