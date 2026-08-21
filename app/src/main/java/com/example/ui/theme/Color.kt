package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core HyperOS Dark Canvas
val ObsidianDark = Color(0xFF080B10)
val DarkSurfaceGlass = Color(0xDD111622)
val DarkSurfaceGlassLight = Color(0xBB182030)
val DarkSurfaceCard = Color(0xFF131A26)
val DarkSurfaceElevated = Color(0xFF1B2436)

// Text Colors
val TextPrimary = Color(0xFFF0F4F8)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Accent Palette
val CyberCyan = Color(0xFF00E5FF)
val CyberCyanGlow = Color(0x3300E5FF)

val ElectricBlue = Color(0xFF2979FF)
val ElectricBlueGlow = Color(0x332979FF)

val HyperPurple = Color(0xFFA855F7)
val HyperPurpleGlow = Color(0x33A855F7)

val TurboCrimson = Color(0xFFFF3366)
val TurboCrimsonGlow = Color(0x33FF3366)

val NeonEmerald = Color(0xFF00E676)
val NeonEmeraldGlow = Color(0x3300E676)

// Performance State Indicators
val StatusOptimal = Color(0xFF00E676)
val StatusWarning = Color(0xFFFFB300)
val StatusExtreme = Color(0xFFFF3366)

enum class TurboAccentColor(val displayName: String, val primary: Color, val glow: Color) {
    CYAN("Cyber Cyan", CyberCyan, CyberCyanGlow),
    BLUE("Electric Blue", ElectricBlue, ElectricBlueGlow),
    PURPLE("Hyper Purple", HyperPurple, HyperPurpleGlow),
    CRIMSON("Turbo Crimson", TurboCrimson, TurboCrimsonGlow),
    EMERALD("Neon Emerald", NeonEmerald, NeonEmeraldGlow);

    companion object {
        fun fromKey(key: String): TurboAccentColor {
            return entries.firstOrNull { it.name.equals(key, ignoreCase = true) } ?: CYAN
        }
    }
}

// Glass Gradients
val GlassCardBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xEE161F2E),
        Color(0xDD0F1522)
    )
)

val GlassCardBorderBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x664A628A),
        Color(0x22202E44)
    )
)

val HeroOverlayGradient = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color(0xDD080B10),
        Color(0xFF080B10)
    )
)
