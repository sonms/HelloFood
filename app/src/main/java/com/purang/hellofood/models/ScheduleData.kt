package com.purang.hellofood.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class ScheduleData(
    val scheduleId: String = "",
    val userId: String = "",
    val date: String = "", // YYYY-MM-DD
    val time: String = "", // HH:mm
    val year: Int = 0,  // 연도
    val month: Int = 0, // 월
    val eventType: String = "", // "운동", "식사", "건강검진", "복약"
    val description: String? = null,
    val status: Boolean = false, // 일정 완료 여부
    val createdAt: LocalDateTime = LocalDateTime.now()
)