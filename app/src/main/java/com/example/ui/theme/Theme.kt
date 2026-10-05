package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = YouTubeRed,
    secondary = GoldAccent,
    tertiary = SuccessGreen,
    background = StudioDarkBg,
    surface = StudioCardBg,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onSurface = TextPrimary,
    onBackground = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = YouTubeRed,
    secondary = GoldAccent,
    tertiary = SuccessGreen,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onSurface = Color(0xFF1A1A1A),
    onBackground = Color(0xFF1A1A1A)
)

@Composable
fun YSeoStudioTheme(
    darkTheme: Boolean = true, // Default to sleek dark studio theme favored by creators
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
