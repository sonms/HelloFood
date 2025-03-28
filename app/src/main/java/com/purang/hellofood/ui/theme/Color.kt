package com.purang.hellofood.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val blueColor1 = Color(0xFFF2F7FF)
val blueColor2 = Color(0xFFE4F0FF)
val blueColor3 = Color(0xFFD7E8FE)
val blueColor4 = Color(0xFFC9E0FE)
val blueColor5 = Color(0xFFBCD9FE)
val blueColor6 = Color(0xFF96ADCB)
val blueColor7 = Color(0xFF718298)
val blueColor8 = Color(0xFF262733)

// Blue Colors
val blueExDark = Color(0xFF1976D2)  // 기존 대비 향상
val blueExLight = Color(0xFF1565C0) // 더 높은 대비를 위해 조정

// Red Colors
val redInDark = Color(0xFFD81B60)   // 어두운 대비 (기존 대비 향상)
val redInLight = Color(0xFFFFA4B4)  // 밝은 대비 조정 (기존보다 대비 개선)

val mintColor1 = Color(0xFFE6FAF5) // 가장 연한 민트
val mintColor2 = Color(0xFFCFF6EC) // 아주 밝은 민트
val mintColor3 = Color(0xFF9EF0DA) // 밝은 민트
val mintColor4 = Color(0xFF70EAC8) // 기본 밝은 민트 (#00E381과 유사)
val mintColor5 = Color(0xFF16AAAA) // 기본 민트 (#16AAAA)
val mintColor6 = Color(0xFF128F8F) // 진한 민트
val mintColor7 = Color(0xFF0C6F6F) // 더 어두운 민트
val mintColor8 = Color(0xFF084F4F) // 가장 어두운 민트

val orange = validateColor(Color(0xFFFF9800))
val yellow = validateColor(Color(0xFFFFEB3B))
val green = validateColor(Color(0xFF4CAF50))

fun validateColor(color: Color): Color {
    return color.copy(
        alpha = color.alpha.coerceIn(0f, 1f),
        red = color.red.coerceIn(0f, 1f),
        green = color.green.coerceIn(0f, 1f),
        blue = color.blue.coerceIn(0f, 1f)
    )
}


