package com.purang.hellofood.utils

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object FontUtils {
    fun getTextStyle(fontSize: Float): TextStyle {
        return TextStyle(
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Normal
        )
    }
}