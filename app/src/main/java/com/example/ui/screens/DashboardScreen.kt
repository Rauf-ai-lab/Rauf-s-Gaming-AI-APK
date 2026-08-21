package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.LocalTurboGlow
import com.example.ui.theme.StatusExtreme
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel
import com.example.ui.viewmodel.NavigationTab

@Composable
fun DashboardScreen(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.hardwareStats.collectAsState()
    val activeGame by viewModel.activeGame.collectAsState()
    val profiles by viewModel.gameProfiles.collectAsState()
    val boostMsg by viewModel.boostMessage.collectAsState()
    val isBoosting by viewModel.isBoosting.collectAsState()
    val accent = LocalTurboAccent.current
    val glow = LocalTurboGlow.current

    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Active Game Card & Quick Booster Engine
        Column(
            modifier = Modifier
                .weight(1.05f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Active Game Hero Glass Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.3f),
                glowAccent = true
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background graphic
                    Image(
                        painter = painterResource(id = R.drawable.bg_game_turbo_hero),
                        contentDescription = "Game Hero",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark gradient scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x77080B10),
                                        Color(0xDD080B10),
                                        Color(0xFF080B10)
                                    )
                                )
                            )
                    )

                    // Content overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
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
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(accent.copy(alpha = 0.2f))
                                        .border(1.dp, accent, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = "Game",
                                        tint = accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "ACTIVE ESPORTS PROFILE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = accent,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = activeGame,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 17.sp
                                        )
                                    )
                                }
                            }

                            // Mode Tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(accent.copy(alpha = 0.25f))
                                    .border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = stats.performanceMode.uppercase(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = accent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Live Metrics Mini Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MiniMetricTile("TARGET FPS", "${stats.targetFps} Hz", Icons.Default.Speed, accent, Modifier.weight(1f))
                            MiniMetricTile("RAM LOAD", "${stats.ramUsagePercent}%", Icons.Default.Bolt, Color(0xFF38BDF8), Modifier.weight(1f))
                            MiniMetricTile("NET PING", "${stats.pingMs} ms", Icons.Default.NetworkCheck, StatusOptimal, Modifier.weight(1f))
                        }

                        // Boost & Launch Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.triggerQuickBoost() },
                                enabled = !isBoosting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accent,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("boost_button")
                            ) {
                                if (isBoosting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.Black,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("OPTIMIZING...", style = MaterialTheme.typography.labelLarge)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = "Boost",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("TURBO BOOST", style = MaterialTheme.typography.labelLarge)
                                }
                            }

                            // Floating HUD toggle
                            Button(
                                onClick = { viewModel.toggleFloatingOverlay() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkSurfaceElevated,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .border(1.dp, GlassCardBorderBrush(), RoundedCornerShape(12.dp))
                                    .testTag("floating_overlay_toggle")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Widgets,
                                    contentDescription = "Overlay",
                                    tint = accent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("GAME HUD", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            // Quick Boost notification banner if active
            AnimatedVisibility(
                visible = boostMsg != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.18f))
                        .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = boostMsg ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = accent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Game Library Quick Switcher
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "QUICK SWITCH GAME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(profiles) { profile ->
                            val isSelected = profile.name == activeGame
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) accent.copy(alpha = 0.25f)
                                        else DarkSurfaceElevated
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) accent else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setActiveGame(profile.name) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) accent else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right Column: AI Co-Pilot & Quick Action Command Grid
        Column(
            modifier = Modifier
                .weight(1.15f)
                .fillMaxHeight()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // AI Gaming Co-Pilot Center Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowAccent = true,
                onClick = { viewModel.selectTab(NavigationTab.AI_ASSISTANT) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.2f))
                                .border(1.dp, accent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI",
                                tint = accent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GEMINI GAMING CO-PILOT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = accent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "How can I improve your gameplay?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "Instant sensitivity, FPS boost & strategy analysis",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Voice trigger shortcut
                    IconButton(
                        onClick = {
                            viewModel.selectTab(NavigationTab.AI_ASSISTANT)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Copilot",
                            tint = accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quick AI Action Cards Grid
            Text(
                text = "TURBO AI QUICK ACTIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 2.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionCard(
                    title = "BEST SENSI",
                    subtitle = "Drag Headshot / Gyro",
                    icon = Icons.Default.Tune,
                    color = accent,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectTab(NavigationTab.AI_ANALYZER)
                    }
                )
                QuickActionCard(
                    title = "FIX FPS & LAG",
                    subtitle = "Thermal Graphics Tweak",
                    icon = Icons.Default.Speed,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectTab(NavigationTab.PERFORMANCE)
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionCard(
                    title = "ANALYZE PING",
                    subtitle = "5GHz DNS Acceleration",
                    icon = Icons.Default.NetworkCheck,
                    color = StatusOptimal,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectTab(NavigationTab.NETWORK)
                    }
                )
                QuickActionCard(
                    title = "GAME STRATEGY",
                    subtitle = "Esports Claw & Rotation",
                    icon = Icons.Default.Psychology,
                    color = Color(0xFFA855F7),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectTab(NavigationTab.AI_ASSISTANT)
                        viewModel.sendChatMessage("Give me pro esports rush strategy and 4-finger claw rotation tips for ${activeGame}", "Strategy")
                    }
                )
            }
        }
    }
}

@Composable
fun MiniMetricTile(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceElevated.copy(alpha = 0.85f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        color = TextMuted
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(64.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.18f))
                    .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = TextSecondary
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun GlassCardBorderBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color(0x554A628A),
            Color(0x22202E44)
        )
    )
}
