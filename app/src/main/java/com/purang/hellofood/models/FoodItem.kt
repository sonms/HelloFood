package com.purang.hellofood.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FoodItem(
    val foodId: Int? = 0,
    val userId: String? = "",
    val photoUrl: String? = "",
    val foodName: String? = "",
    val foodDescription : String? = ""
) : Parcelable
