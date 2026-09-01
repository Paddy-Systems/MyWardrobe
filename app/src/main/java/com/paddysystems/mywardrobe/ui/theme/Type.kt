package com.paddysystems.mywardrobe.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

private val EditorialSerif = FontFamily.Serif
private val UtilitySans = FontFamily.SansSerif

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = EditorialSerif, fontWeight = FontWeight.Normal, fontSize = 48.sp, lineHeight = 52.sp, letterSpacing = (-1).sp),
    displayMedium = TextStyle(fontFamily = EditorialSerif, fontWeight = FontWeight.Normal, fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp),
    headlineLarge = TextStyle(fontFamily = EditorialSerif, fontWeight = FontWeight.Normal, fontSize = 34.sp, lineHeight = 39.sp),
    headlineMedium = TextStyle(fontFamily = EditorialSerif, fontWeight = FontWeight.Normal, fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontFamily = EditorialSerif, fontWeight = FontWeight.Medium, fontSize = 23.sp, lineHeight = 29.sp),
    titleMedium = TextStyle(fontFamily = UtilitySans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = UtilitySans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = UtilitySans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontFamily = UtilitySans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp),
    labelMedium = TextStyle(fontFamily = UtilitySans, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.8.sp),
    labelSmall = TextStyle(fontFamily = UtilitySans, fontWeight = FontWeight.Bold, fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 1.4.sp)
)

val WardrobeShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
