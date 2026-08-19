package com.demo.todo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val default = Typography()

val AppTypography = default.copy(
    headlineSmall = default.headlineSmall.copy(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = default.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = default.titleMedium.copy(fontWeight = FontWeight.Medium),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
    ),
)
