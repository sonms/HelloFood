package com.purang.hellofood.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class AgricultureRecommendation (
    val recommendationId: Int,
    val userId: String,
    val productName: String,
    val category: String, // "채소", "과일", "곡물"
    val nutritionalValue: String?,
    val origin: String?, // 원산지
    val sustainabilityScore: Float?, // 환경 친화적 등급
    val createdAt: String = LocalDateTime.now().toString()
)

/**
 * recommendation_id (INT, PK) – 추천 ID
 *
 * user_id (INT, FK) – 사용자 ID
 *
 * product_name (VARCHAR) – 농산물 이름
 *
 * category (VARCHAR) – 농산물 카테고리 (채소, 과일, 곡물 등)
 *
 * nutritional_value (TEXT) – 영양 정보
 *
 * sustainability_score (FLOAT) – 지속 가능성 지수 (환경 친화적 등급)
 *
 * created_at (TIMESTAMP) – 추천 생성일
 */