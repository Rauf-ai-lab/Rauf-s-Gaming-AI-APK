package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusExtreme
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel

@Composable
fun PerformanceScreen(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.hardwareStats.collectAsState()
    val activeMode by viewModel.activePerfMode.collectAsState()
    val isBoosting by viewModel.isBoosting.collectAsState()
    val boostMsg by viewModel.boostMessage.collectAsState()
    val accent = LocalTurboAccent.current
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Performance Mode Selection Grid
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "TURBO PERFORMANCE MODES",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // Modes Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModeSelectionCard(
                    title = "EXTREME",
                    fpsTarget = "120 FPS",
                    description = "Max GPU & Touch Priority",
                    icon = Icons.Default.RocketLaunch,
                    color = StatusExtreme,
                    isSelected = activeMode == "Extreme",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setPerformanceMode("Extreme") }
                )
                ModeSelectionCard(
                    title = "PERFORMANCE",
                    fpsTarget = "90 FPS",
                    description = "Esports Ranked Balance",
                    icon = Icons.Default.Speed,
                    color = accent,
                    isSelected = activeMode == "Performance",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setPerformanceMode("Performance") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModeSelectionCard(
                    title = "BALANCED",
                    fpsTarget = "60 FPS",
                    description = "Cool Thermals & Stable Frame",
                    icon = Icons.Default.Bolt,
                    color = Color(0xFF38BDF8),
                    isSelected = activeMode == "Balanced",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setPerformanceMode("Balanced") }
                )
                ModeSelectionCard(
                    title = "BATTERY SAVER",
                    fpsTarget = "45 FPS",
                    description = "Extended Tournament Life",
                    icon = Icons.Default.BatterySaver,
                    color = StatusOptimal,
                    isSelected = activeMode == "Eco" || activeMode == "Battery Saver",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setPerformanceMode("Battery Saver") }
                )
            }

            // Disclaimer Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Game Turbo AI manages background resource allocation and Android frame sync parameters safely without overclocking hardware.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMuted,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }
        }

        // Right Column: Live Resource Hardware Monitor & RAM Cleaner
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "REAL-TIME RESOURCE TELEMETRY",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // RAM Usage Monitor Card
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "RAM",
                                tint = accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RAM MEMORY USAGE",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Text(
                            text = "${stats.ramUsedMb} MB / ${stats.ramTotalMb} MB (${stats.ramUsagePercent}%)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { stats.ramUsagePercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (stats.ramUsagePercent > 80) StatusExtreme else accent,
                        trackColor = DarkSurfaceElevated
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.triggerQuickBoost() },
                        enabled = !isBoosting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent.copy(alpha = 0.2f),
                            contentColor = accent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .testTag("clean_ram_button")
                    ) {
                        if (isBoosting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = accent,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("FREEING CACHED MEMORY...", style = MaterialTheme.typography.labelSmall)
                        } else {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = "Clean",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DEEP MEMORY CLEANUP", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // CPU & Thermal Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // CPU Utilization Card
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "CPU",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "CPU LOAD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stats.cpuUsagePercent}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Octa-core Turbo Active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 8.5.sp
                            )
                        )
                    }
                }

                // Thermal Monitoring Card
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = "Temp",
                                tint = if (stats.batteryTempCelsius > 40) StatusExtreme else StatusOptimal,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "THERMAL STATUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%.1f°C".format(stats.batteryTempCelsius),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = if (stats.batteryTempCelsius > 40) StatusExtreme else StatusOptimal
                            )
                        )
                        Text(
                            text = if (stats.batteryTempCelsius > 40) "Warm - Throttling Guard" else "Optimal Gaming Curve",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 8.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeSelectionCard(
    title: String,
    fpsTarget: String,
    description: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(96.dp),
        borderColor = if (isSelected) color else null,
        borderWidth = if (isSelected) 1.5.dp else 1.dp,
        glowAccent = isSelected,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = color,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = if (isSelected) color else TextPrimary
                        )
                    )
                }

                Text(
                    text = fpsTarget,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = color,
                        fontSize = 10.sp
                    )
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 9.5.sp,
                    color = TextSecondary
                )
            )

            // Active indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(if (isSelected) color else Color.Transparent)
            )
        }
    }
}
