package com.purang.hellofood.models

import android.os.Build
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Keep
@Parcelize
@RequiresApi(Build.VERSION_CODES.O)
data class FoodLog(
    var foodId: String = "0",
    val userId: String = "",
    val photoUrl: String? = null,
    val foodName: String? = null,
    val foodDescription: String? = null,
    val calories: Float? = null,
    val carbohydrates: Float? = null,
    val proteins: Float? = null,
    val fats: Float? = null,
    val vitamin: Float? = null,
    val water: Int? = null,
    val createdAt: String = LocalDateTime.now().toString()
) : Parcelable
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

