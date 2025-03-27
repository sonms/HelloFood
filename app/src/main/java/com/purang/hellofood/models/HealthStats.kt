package com.purang.hellofood.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class HealthStats (
    val statsId: Int,
    val userId: Int,
    val date: String, // YYYY-MM-DD
    val steps: Int?,
    val caloriesBurned: Float?,
    val exerciseMinutes: Int?,
    val sleepHours: Float?,
    val weight: Float?,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
/**
 * stats_id (INT, PK) – 통계 ID
 *
 * user_id (INT, FK) – 사용자 ID
 *
 * date (DATE) – 기록 날짜
 *
 * steps (INT) – 걸음 수
 *
 * calories_burned (FLOAT) – 소모 칼로리
 *
 * exercise_minutes (INT) – 운동 시간(분)
 *
 *
 * sleep_hours (FLOAT) – 수면 시간
 *
 * weight (FLOAT) – 몸무게 변화 기록
 *
 * created_at (TIMESTAMP) – 기록 시간
 *
 *
 */
