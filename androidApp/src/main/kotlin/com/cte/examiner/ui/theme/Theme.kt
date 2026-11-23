package com.cte.examiner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF211D70)
val Orange = Color(0xFFF15A22)

private val LightColors = lightColorScheme(
    primary = Navy,
    secondary = Orange,
    onPrimary = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Navy,
    secondary = Orange,
    onPrimary = Color.White
)

@Composable
fun CteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}