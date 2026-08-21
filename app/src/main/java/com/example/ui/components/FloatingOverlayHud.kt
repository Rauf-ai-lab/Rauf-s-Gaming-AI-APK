package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel
import com.example.ui.viewmodel.NavigationTab

@Composable
fun FloatingOverlayHud(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val isOpen by viewModel.isFloatingOverlayOpen.collectAsState()
    val stats by viewModel.hardwareStats.collectAsState()
    val isDnd by viewModel.isDndActive.collectAsState()
    val isTouchBoost by viewModel.isTouchBoostActive.collectAsState()
    val activeGame by viewModel.activeGame.collectAsState()
    val accent = LocalTurboAccent.current

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { -it }),
        exit = slideOutHorizontally(targetOffsetX = { -it }),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(320.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xF00D131D),
                            Color(0xE0111824)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(listOf(accent.copy(alpha = 0.8f), Color(0x334A628A))),
                    shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                )
                .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = "Game Turbo HUD",
                                tint = accent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "GAME TURBO HUD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = activeGame,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.toggleFloatingOverlay() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close HUD",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Live Metrics Pill Matrix
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HudFpsBadge(fps = stats.fps, targetFps = stats.targetFps)
                    HudPingBadge(pingMs = stats.pingMs)
                    HudTempBadge(tempCelsius = stats.batteryTempCelsius)
                }

                // Quick Boost Button
                Button(
                    onClick = { viewModel.triggerQuickBoost() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = "Quick Boost",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TURBO BOOST RAM & CPU",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Quick In-Game Tool Buttons (DND, Touch Boost, Sensi, Voice CoPilot)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OverlayToolButton(
                            title = "DND SHIELD",
                            isActive = isDnd,
                            icon = Icons.Default.DoNotDisturbOn,
                            accentColor = accent,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.toggleDnd() }
                        )
                        OverlayToolButton(
                            title = "TOUCH BOOST",
                            isActive = isTouchBoost,
                            icon = Icons.Default.TouchApp,
                            accentColor = accent,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.toggleTouchBoost() }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OverlayToolButton(
                            title = "AI SENSI",
                            isActive = false,
                            icon = Icons.Default.Tune,
                            accentColor = accent,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.toggleFloatingOverlay()
                                viewModel.selectTab(NavigationTab.AI_ANALYZER)
                            }
                        )
                        OverlayToolButton(
                            title = "GAMES",
                            isActive = false,
                            icon = Icons.Default.SportsEsports,
                            accentColor = accent,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.toggleFloatingOverlay()
                                viewModel.selectTab(NavigationTab.LIBRARY)
                            }
                        )
                    }
                }

                // Disclaimer footer
                Text(
                    text = "HyperOS 3 Inspired Gaming Control Panel",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 8.5.sp
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun OverlayToolButton(
    title: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) accentColor.copy(alpha = 0.25f) else DarkSurfaceElevated)
            .border(1.dp, if (isActive) accentColor else Color(0x334A628A), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) accentColor else TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) accentColor else TextPrimary,
                    fontSize = 9.sp
                )
            )
        }
    }
}
