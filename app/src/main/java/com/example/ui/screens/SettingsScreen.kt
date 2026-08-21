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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AiConnectionStatus
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboAccentColor
import com.example.ui.viewmodel.GameTurboViewModel

@Composable
fun SettingsScreen(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val currentAccent by viewModel.currentAccent.collectAsState()
    val aiStatus by viewModel.aiStatus.collectAsState()
    val aiStatusDetails by viewModel.aiStatusDetails.collectAsState()
    val accent = LocalTurboAccent.current
    val context = LocalContext.current

    var customKeyInput by remember { mutableStateOf(viewModel.preferencesManager.customApiKey) }
    var selectedLang by remember { mutableStateOf(viewModel.preferencesManager.voiceLanguage) }
    var speechSpeed by remember { mutableFloatStateOf(viewModel.preferencesManager.speechSpeed) }

    val scrollLeft = rememberScrollState()
    val scrollRight = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Visual Theme & Voice Engine
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(scrollLeft),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "HYPEROS ACCENT THEME",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // Accent Color Chooser
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, "Theme", tint = accent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GLOW PALETTE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Text(currentAccent.displayName, style = MaterialTheme.typography.labelSmall.copy(color = currentAccent.primary, fontWeight = FontWeight.Bold))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TurboAccentColor.entries.forEach { themeAccent ->
                            val isSelected = currentAccent == themeAccent
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(themeAccent.primary.copy(alpha = if (isSelected) 0.9f else 0.25f))
                                    .border(
                                        2.dp,
                                        if (isSelected) Color.White else themeAccent.primary.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setAccentColor(themeAccent) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, "Selected", tint = Color.Black, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = "AI VOICE COPILOT SETTINGS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // Voice Config Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RecordVoiceOver, "Voice", tint = accent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("VOICE LANGUAGE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("English", "Hindi", "Hinglish").forEach { lang ->
                            val isSel = selectedLang.equals(lang, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) accent.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                    .border(1.dp, if (isSel) accent else Color.Transparent, RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedLang = lang
                                        viewModel.setVoiceLanguage(lang)
                                    }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) accent else TextPrimary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SPEECH SPEED RATE", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        Text("%.2fx".format(speechSpeed), style = MaterialTheme.typography.labelSmall.copy(color = accent, fontWeight = FontWeight.Bold))
                    }

                    Slider(
                        value = speechSpeed,
                        onValueChange = {
                            speechSpeed = it
                            viewModel.setSpeechSpeed(it)
                        },
                        valueRange = 0.8f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent, inactiveTrackColor = DarkSurfaceElevated),
                        modifier = Modifier.height(20.dp)
                    )
                }
            }
        }

        // Right Column: Gemini API Configuration & Database Options
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(scrollRight),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "GEMINI API & NEURAL ENGINE CONFIG",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // Gemini API Card
            GlassCard(modifier = Modifier.fillMaxWidth(), glowAccent = true) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, "AI", tint = accent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GEMINI FLASH ENGINE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (aiStatus == AiConnectionStatus.ONLINE) StatusOptimal.copy(alpha = 0.2f) else StatusWarning.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (aiStatus == AiConnectionStatus.ONLINE) "ACTIVE" else "OFFLINE BACKUP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (aiStatus == AiConnectionStatus.ONLINE) StatusOptimal else StatusWarning,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Text(
                        text = aiStatusDetails,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 9.sp, color = TextSecondary)
                    )

                    OutlinedTextField(
                        value = customKeyInput,
                        onValueChange = { customKeyInput = it },
                        placeholder = { Text("Optional: Custom Gemini API Key", fontSize = 10.sp, color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("api_key_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated,
                            focusedBorderColor = accent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.saveCustomApiKey(customKeyInput)
                                Toast.makeText(context, "API Key Updated & Connection Tested", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        ) {
                            Text("SAVE & TEST", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp))
                        }

                        Button(
                            onClick = { viewModel.checkAiStatus() },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated, contentColor = TextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        ) {
                            Icon(Icons.Default.Refresh, "Check", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RE-CHECK", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp))
                        }
                    }
                }
            }

            // Privacy & Architecture Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, "Privacy", tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PRIVACY & DATA STORAGE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp))
                    }
                    Text(
                        text = "• All telemetry, game sensitivities, and chat history are saved 100% locally on-device using Room Database.\n• API Keys are stored encrypted in Android local secure preferences and never exposed.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 9.sp, color = TextSecondary, lineHeight = 13.sp)
                    )
                }
            }
        }
    }
}
