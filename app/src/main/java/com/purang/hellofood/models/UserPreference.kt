package com.purang.hellofood.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
data class UserPreference(
    val preferenceId: Int,
    val userId: String,
    val userEmail : String,
    val preferredFood: String?,
    val dislikedFood: String?,
    val allergies: String?,
    val createdAt: String = LocalDateTime.now().toString()
)

/**
 * preference_id (INT, PK) – 선호 ID
 *
 * user_id (INT, FK) – 사용자 ID
 *
 * preferred_food (TEXT) – 선호하는 음식
 *
 * disliked_food (TEXT) – 기피하는 음식
 *
 * allergies (TEXT) – 알러지 정보
 *
 * created_at (TIMESTAMP) – 설정 날짜
 */