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

    //스케줄 아이디로 가져오기
    suspend fun getUserScheduleById(userId: String, scheduleId: String): ScheduleData? {
        val result = scheduleCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("scheduleId", scheduleId)
            .get()
            .await()

        return result.documents.firstOrNull()?.toObject(ScheduleData::class.java)
    }

    //users 컬렉션: 각 유저의 데이터를 userId로 구분하여 저장합니다.
    //
    //schedules 서브컬렉션: 각 유저의 스케줄 데이터를 userId에 대응하는 서브컬렉션 안에 저장합니다.
    //
    //userId → schedules 서브컬렉션 → scheduleId (문서)
    suspend fun addSchedule(schedule: ScheduleData): Boolean {
        return try {
            // Firestore에서 userId에 해당하는 유저 문서의 schedules 서브컬렉션에 스케줄 추가
            val userScheduleCollection = firestore.collection("users").document(schedule.userId).collection("schedules")

            // Firestore에 문서를 추가하고, 그 문서의 자동 생성된 ID를 scheduleId로 설정
            val documentReference = userScheduleCollection.add(schedule).await()

            // 문서가 추가된 후, Firestore 문서 ID를 scheduleId로 설정
            val updatedSchedule = schedule.copy(scheduleId = documentReference.id)

            // Firestore에 자동 생성된 scheduleId를 반영하여 다시 업데이트
            userScheduleCollection.document(documentReference.id).set(updatedSchedule).await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteSchedule(userId: String, scheduleId: String): Boolean {
        return try {
            // Firestore에서 해당 유저의 schedules 컬렉션에 있는 특정 scheduleId 문서를 삭제
            firestore.collection("users")
                .document(userId)
                .collection("schedules")
                .document(scheduleId)
                .delete()
                .await()

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


