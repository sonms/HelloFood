package com.purang.hellofood.models

data class FoodItem(
    val foodId: Int,
    val userId: String,
    val photoUrl: String?,
    val foodName: String?,
    val foodDescription : String?
)
