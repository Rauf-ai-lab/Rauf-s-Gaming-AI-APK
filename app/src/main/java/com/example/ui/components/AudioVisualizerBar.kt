package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalTurboAccent

@Composable
fun AudioVisualizerBar(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 14,
    amplitude: Float = 0f,
    color: Color = LocalTurboAccent.current
) {
    val transition = rememberInfiniteTransition(label = "audioWave")
    
    val h1 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(380, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 0.4f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(260, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h3"
    )
    val h4 by transition.animateFloat(
        initialValue = 0.15f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(310, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h4"
    )

    Row(
        modifier = modifier.height(28.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val baseFactor = when (i % 4) {
                0 -> h1
                1 -> h2
                2 -> h3
                else -> h4
            }
            val heightFraction = if (isActive) {
                if (amplitude > 0.05f) {
                    (baseFactor * 0.4f + amplitude * 0.6f).coerceIn(0.2f, 1.0f)
                } else {
                    baseFactor
                }
            } else {
                0.15f
            }

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .fillMaxHeight(heightFraction)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color.copy(alpha = if (isActive) 0.95f else 0.3f))
            )
        }
    }
}

@Composable
fun VoiceChakraOrb(
    isActive: Boolean,
    isThinking: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val transition = rememberInfiniteTransition(label = "chakra_orb")
    val pulseScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = if (isActive || isThinking) 1.22f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isThinking) 500 else if (isActive) 750 else 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val innerRotation by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "inner_rot"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing halo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = if (isActive || isThinking) 0.45f else 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Middle ring
        Box(
            modifier = Modifier
                .size(size * 0.75f)
                .scale(if (isActive) innerRotation else 1.0f)
                .clip(CircleShape)
                .border(1.5.dp, color.copy(alpha = if (isActive || isThinking) 0.85f else 0.35f), CircleShape)
                .background(color.copy(alpha = 0.15f))
        )
    }
}

