package com.purang.hellofood.models

data class User(
    val userId: Int,
    val name: String,
    val age: Int,
    val gender: String, // "남", "여", "기타"
    val height: Float, // cm 단위
    val weight: Float, // kg 단위
    val bmi: Float,
    val activityLevel: String, // 활동 단계 - "낮음", "보통", "높음"
    val healthGoal: String, // 건강목표 - 예: "체중 감량", "근육 증가"
    val allergies: String?, // 알러지 정보 (Nullable)
    val dietPreference: String?, // 식단 선호-예: "비건", "저탄고지"
)