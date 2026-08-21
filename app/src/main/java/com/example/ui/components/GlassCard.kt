package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.GlassCardBorderBrush
import com.example.ui.theme.LocalTurboAccent

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = DarkSurfaceGlass,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    glowAccent: Boolean = false,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val accent = LocalTurboAccent.current
    val effectiveBorder = when {
        glowAccent -> BorderStroke(borderWidth, accent.copy(alpha = 0.6f))
        borderColor != null -> BorderStroke(borderWidth, borderColor)
        else -> BorderStroke(borderWidth, GlassCardBorderBrush)
    }

    val baseModifier = modifier
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    backgroundColor,
                    backgroundColor.copy(alpha = 0.85f)
                )
            ),
            shape = shape
        )
        .border(effectiveBorder, shape)
        .then(
            if (testTag != null) Modifier.testTag(testTag) else Modifier
        )

    val finalModifier = if (onClick != null) {
        baseModifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = accent),
            onClick = onClick
        )
    } else {
        baseModifier
    }

    Box(modifier = finalModifier, content = content)
}
