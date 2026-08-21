package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalTurboAccent

@Composable
fun AudioVisualizerBar(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 14,
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
            val factor = when (i % 4) {
                0 -> h1
                1 -> h2
                2 -> h3
                else -> h4
            }
            val heightFraction = if (isActive) factor else 0.15f

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
