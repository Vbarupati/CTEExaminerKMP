package com.cte.examiner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy       = Color(0xFF1B3C8C)
val NavyDark   = Color(0xFF16306B)
val Orange     = Color(0xFFF59E0B)
val GreenChip  = Color(0xFF2E7D32)
val GreenBg    = Color(0xFFE6F4EA)
val RedChip    = Color(0xFFD32F2F)
val RedBg      = Color(0xFFFCE8E8)
val ShieldBg   = Color(0xFFE3ECFB)
val ScreenBg   = Color(0xFFF4F6FA)

private val LightColors = lightColorScheme(
    primary = Navy, onPrimary = Color.White, secondary = Orange, background = ScreenBg
)
private val DarkColors = darkColorScheme(
    primary = Navy, onPrimary = Color.White, secondary = Orange
)

@Composable
fun CteTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}