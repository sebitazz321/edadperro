package com.optativa.dogage.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFE8743B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDCC2),
    onPrimaryContainer = Color(0xFF4A1F00),
    secondary = Color(0xFF2A9D8F),
    onSecondary = Color.White,
    tertiary = Color(0xFF264653),
    background = Color(0xFFFFF8F1),
    surface = Color.White,
    surfaceVariant = Color(0xFFFFE9D6)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB68A),
    onPrimary = Color(0xFF4A1F00),
    primaryContainer = Color(0xFF6B3410),
    onPrimaryContainer = Color(0xFFFFDCC2),
    secondary = Color(0xFF7FD6CB),
    onSecondary = Color(0xFF00201C),
    tertiary = Color(0xFFA8D5E2),
    background = Color(0xFF1B1411),
    surface = Color(0xFF261C17),
    surfaceVariant = Color(0xFF3A2A21)
)

@Composable
fun DogAgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
