package com.paddysystems.mywardrobe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Lilac,
    onPrimary = Charcoal,
    secondary = Tomato,
    background = Charcoal,
    onBackground = WarmWhite,
    surface = DarkSurface,
    onSurface = WarmWhite,
    surfaceVariant = Aubergine,
    outline = WarmGrey
)

private val LightColorScheme = lightColorScheme(
    primary = Aubergine,
    onPrimary = WarmWhite,
    primaryContainer = PaleLilac,
    onPrimaryContainer = Charcoal,
    secondary = Tomato,
    onSecondary = WarmWhite,
    secondaryContainer = Lilac,
    onSecondaryContainer = Charcoal,
    tertiary = Sage,
    background = Oat,
    onBackground = Charcoal,
    surface = WarmWhite,
    onSurface = Charcoal,
    surfaceVariant = PaleLilac,
    onSurfaceVariant = WarmGrey,
    outline = Hairline,
    outlineVariant = Hairline,
    error = Tomato,
    onError = WarmWhite
)

@Composable
fun MyWardrobeTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = WardrobeShapes,
        content = content
    )
}
