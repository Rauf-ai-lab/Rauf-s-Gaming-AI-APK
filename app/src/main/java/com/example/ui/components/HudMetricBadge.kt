package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurfaceGlassLight
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusExtreme
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HudMetricBadge(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color = LocalTurboAccent.current,
    showLivePulse: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceGlassLight)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showLivePulse) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
        }

        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = TextSecondary
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 11.sp,
                color = TextPrimary
            )
        )
    }
}

@Composable
fun HudFpsBadge(fps: Int, targetFps: Int = 60) {
    val color = when {
        fps >= 58 -> StatusOptimal
        fps >= 45 -> StatusWarning
        else -> StatusExtreme
    }
    HudMetricBadge(
        label = "FPS",
        value = "$fps",
        icon = Icons.Default.Speed,
        accentColor = color,
        showLivePulse = true
    )
}

@Composable
fun HudPingBadge(pingMs: Int) {
    val color = when {
        pingMs <= 40 -> StatusOptimal
        pingMs <= 90 -> StatusWarning
        else -> StatusExtreme
    }
    HudMetricBadge(
        label = "PING",
        value = "${pingMs}ms",
        icon = Icons.Default.Wifi,
        accentColor = color
    )
}

@Composable
fun HudTempBadge(tempCelsius: Float) {
    val color = when {
        tempCelsius <= 37.0f -> StatusOptimal
        tempCelsius <= 41.0f -> StatusWarning
        else -> StatusExtreme
    }
    HudMetricBadge(
        label = "TEMP",
        value = "%.1f°C".format(tempCelsius),
        icon = Icons.Default.Thermostat,
        accentColor = color
    )
}

@Composable
fun HudBatteryBadge(batteryPercent: Int, isCharging: Boolean) {
    val color = when {
        batteryPercent >= 40 -> StatusOptimal
        batteryPercent >= 20 -> StatusWarning
        else -> StatusExtreme
    }
    HudMetricBadge(
        label = if (isCharging) "CHG" else "BAT",
        value = "$batteryPercent%",
        icon = if (isCharging) Icons.Default.ElectricBolt else Icons.Default.Bolt,
        accentColor = color
    )
}
