package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.ObsidianDark
import kotlin.math.sin
import kotlin.random.Random

private data class ChakraParticle(
    val initialX: Float,
    val speedY: Float,
    val radius: Float,
    val phase: Float,
    val color: Color
)

@Composable
fun ChakraBackgroundLayer(
    animeEffectsEnabled: Boolean = true,
    animationLevel: String = "FULL", // FULL, LOW, OFF
    modifier: Modifier = Modifier
) {
    if (!animeEffectsEnabled) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianDark)
        )
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "chakra_ambient_loop")

    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (animationLevel == "LOW") 7000 else 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_anim"
    )

    val chakraGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val particleCount = when (animationLevel) {
        "FULL" -> 36
        "LOW" -> 14
        else -> 0
    }

    val particles = remember(particleCount) {
        val colors = listOf(
            Color(0xFFFF5722), // Blazing Chakra Orange
            Color(0xFFFF9800), // Fiery Amber
            Color(0xFFFF3D00), // Crimson Kurama Flame
            Color(0xFFFFC107), // Golden Lightning Spark
            Color(0xFFFF6D00)  // Deep Chakra Fire
        )
        List(particleCount) {
            ChakraParticle(
                initialX = Random.nextFloat(),
                speedY = Random.nextFloat() * 0.6f + 0.4f,
                radius = Random.nextFloat() * 3.5f + 1.5f,
                phase = Random.nextFloat() * 6.28f,
                color = colors.random()
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // High-Quality Anime Ninja & Nine-Tails Chakra Background
        Image(
            painter = painterResource(id = R.drawable.img_anime_ninja_bg),
            contentDescription = "Anime Ninja Chakra Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Balanced Scrim & Glass Contrast Gradient
        // Darkens the left side for UI elements while letting character & fox spirit pop on right
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFA090D14), // Left: Deep obsidian glass for navigation & cards
                            Color(0xEA090D14), // Center-Left: High contrast
                            Color(0xAA0D111A), // Center: Translucent
                            Color(0x55111827), // Right: Subtle overlay over character & glowing chakra fox
                            Color(0x44080B10)  // Far Right: Crisply visible anime art
                        )
                    )
                )
        )

        // Top and bottom edge vignettes
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC06080D),
                            Color.Transparent,
                            Color(0xDD06080D)
                        )
                    )
                )
        )

        // Ambient Chakra Energy Glow overlay on the right half
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF5722).copy(alpha = chakraGlowAlpha),
                            Color(0xFFFF3D00).copy(alpha = chakraGlowAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                        radius = 1200f
                    )
                )
        )

        // Swirling Floating Chakra Flame Embers & Sparks
        if (particleCount > 0) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                particles.forEach { p ->
                    val yProgress = (pulseProgress * p.speedY + p.phase) % 1f
                    val y = canvasHeight * (1f - yProgress)
                    val sway = sin((pulseProgress * 6.28f + p.phase).toDouble()).toFloat() * 24f
                    val x = (p.initialX * canvasWidth + sway).coerceIn(0f, canvasWidth)

                    // Embers glow brighter near the bottom and middle
                    val alpha = sin(yProgress * 3.14159f).coerceIn(0.1f, 0.85f)

                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.radius,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}
