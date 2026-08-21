package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalTurboAccent = compositionLocalOf { CyberCyan }
val LocalTurboGlow = compositionLocalOf { CyberCyanGlow }

@Composable
fun GameTurboTheme(
    accentColor: TurboAccentColor = TurboAccentColor.CYAN,
    content: @Composable () -> Unit
) {
    val darkColorScheme = darkColorScheme(
        primary = accentColor.primary,
        onPrimary = Color.Black,
        primaryContainer = accentColor.glow,
        onPrimaryContainer = accentColor.primary,
        secondary = Color(0xFF38BDF8),
        onSecondary = Color.Black,
        tertiary = Color(0xFFA78BFA),
        background = ObsidianDark,
        onBackground = TextPrimary,
        surface = DarkSurfaceCard,
        onSurface = TextPrimary,
        surfaceVariant = DarkSurfaceElevated,
        onSurfaceVariant = TextSecondary
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ObsidianDark.toArgb()
                window.navigationBarColor = ObsidianDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(
        LocalTurboAccent provides accentColor.primary,
        LocalTurboGlow provides accentColor.glow
    ) {
        MaterialTheme(
            colorScheme = darkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
