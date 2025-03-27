package com.purang.hellofood.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class FoodLog (
    val foodId: Int,
    val userId: Int,
    val photoUrl: String?,
    val foodName: String,
    val calories: Float,
    val carbohydrates: Float,
    val proteins: Float,
    val fats: Float,
    val mealTime: String, // "아침", "점심", "저녁", "간식"
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/** food_id (INT, PK) – 음식 기록 ID

user_id (INT, FK) – 사용자 ID

photo_url (VARCHAR) – 음식 사진 URL

food_name (VARCHAR) – 음식 이름

calories (FLOAT) – 칼로리(kcal)

carbohydrates (FLOAT) – 탄수화물 (g)

proteins (FLOAT) – 단백질 (g)

fats (FLOAT) – 지방 (g)

meal_time (ENUM) – 식사 시간 (아침, 점심, 저녁, 간식)

created_at (TIMESTAMP) – 기록 날짜
**/

