package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel

@Composable
fun GamingToolsScreen(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val isDnd by viewModel.isDndActive.collectAsState()
    val isTouchBoost by viewModel.isTouchBoostActive.collectAsState()
    val brightness by viewModel.brightnessLevel.collectAsState()
    val volume by viewModel.volumeLevel.collectAsState()
    val activeVoiceFx by viewModel.voiceEffect.collectAsState()
    val accent = LocalTurboAccent.current
    val context = LocalContext.current

    val scrollLeft = rememberScrollState()
    val scrollRight = rememberScrollState()

    val voiceEffects = listOf("Studio Clear", "Deep Cyber", "Tactical Radio", "Mech Robot")

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Core Gaming Toggles & Capture Shortcuts
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(scrollLeft),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "GAMING FOCUS & CAPTURE TOOLS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // DND Gaming Mode
            ToolToggleCard(
                title = "DO NOT DISTURB MODE",
                subtitle = "Suppress popups & block non-urgent call banners",
                icon = Icons.Default.DoNotDisturbOn,
                isActive = isDnd,
                accentColor = accent,
                onToggle = { viewModel.toggleDnd() },
                testTag = "dnd_switch"
            )

            // Touch Sampling Rate Booster
            ToolToggleCard(
                title = "TOUCH RESPONSE BOOST",
                subtitle = "Accelerates touch polling & gesture response curves",
                icon = Icons.Default.TouchApp,
                isActive = isTouchBoost,
                accentColor = accent,
                onToggle = { viewModel.toggleTouchBoost() },
                testTag = "touch_boost_switch"
            )

            // Capture Shortcuts Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp),
                    onClick = {
                        Toast.makeText(context, "📸 Game Screenshot Captured to Gallery", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, "Screenshot", tint = accent, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("SCREENSHOT", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
                            Text("Instant capture", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
                        }
                    }
                }

                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp),
                    onClick = {
                        Toast.makeText(context, "🎥 1080p 60FPS Game Recording Started", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF3366).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Videocam, "Record", tint = Color(0xFFFF3366), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("SCREEN RECORD", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
                            Text("Internal Audio", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
                        }
                    }
                }
            }
        }

        // Right Column: Sliders (Brightness, Volume) & Voice FX Modulator
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(scrollRight),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "DISPLAY, AUDIO & VOICE FX",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // Brightness & Volume Sliders Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Brightness6, "Brightness", tint = accent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BRIGHTNESS LOCK", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Text("${(brightness * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(color = accent, fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = brightness,
                        onValueChange = { viewModel.setBrightness(it) },
                        colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent, inactiveTrackColor = DarkSurfaceElevated),
                        modifier = Modifier.height(20.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, "Volume", tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GAME AUDIO VOLUME", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Text("${(volume * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = volume,
                        onValueChange = { viewModel.setVolume(it) },
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF38BDF8), inactiveTrackColor = DarkSurfaceElevated),
                        modifier = Modifier.height(20.dp)
                    )
                }
            }

            // Voice Changer Modulator
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RecordVoiceOver, "Voice FX", tint = accent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VOICE CHANGER FX", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Text(activeVoiceFx, style = MaterialTheme.typography.labelSmall.copy(color = accent, fontWeight = FontWeight.Bold))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        voiceEffects.forEach { effect ->
                            val isSelected = activeVoiceFx == effect
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) accent.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                    .border(1.dp, if (isSelected) accent else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setVoiceEffect(effect) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = effect,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) accent else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 9.5.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Text(
                        text = "Real-time acoustic equalizer filters (Synthetic voice effects for squad privacy).",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 8.5.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun ToolToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    accentColor: Color,
    onToggle: () -> Unit,
    testTag: String
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isActive) accentColor.copy(alpha = 0.5f) else null,
        glowAccent = isActive
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (isActive) accentColor.copy(alpha = 0.2f) else DarkSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isActive) accentColor else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) accentColor else TextPrimary,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Switch(
                checked = isActive,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = accentColor
                ),
                modifier = Modifier
                    .height(26.dp)
                    .testTag(testTag)
            )
        }
    }
}
