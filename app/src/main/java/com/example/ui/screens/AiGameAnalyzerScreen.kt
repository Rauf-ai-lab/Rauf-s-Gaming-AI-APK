package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel

@Composable
fun AiGameAnalyzerScreen(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val input by viewModel.analyzerInput.collectAsState()
    val result by viewModel.analyzerResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingGame.collectAsState()
    val accent = LocalTurboAccent.current

    val leftScrollState = rememberScrollState()
    val rightScrollState = rememberScrollState()

    val gamesList = listOf("Free Fire MAX", "BGMI / PUBG", "COD Mobile", "Genshin Impact", "Apex Mobile")
    val ramOptions = listOf(4, 6, 8, 12, 16)
    val fpsOptions = listOf(60, 90, 120)
    val playstyleOptions = listOf("Rush / Aggressive", "Sniper / Support", "Balanced Flanker")
    val aimOptions = listOf("Drag Headshot", "Tap Headshot", "Claw Tracking")

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Interactive Input Form
        Column(
            modifier = Modifier
                .weight(1.05f)
                .fillMaxHeight()
                .verticalScroll(leftScrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "GAMEPLAY PROFILING CONFIGURATOR",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // Game Selector
            Text("Select Target Game:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(gamesList) { game ->
                    val isSelected = input.game == game
                    ChipOption(text = game, isSelected = isSelected, accentColor = accent) {
                        viewModel.updateAnalyzerInput { it.copy(game = game) }
                    }
                }
            }

            // RAM Selector
            Text("Device RAM Capacity:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ramOptions.forEach { ram ->
                    ChipOption(text = "${ram}GB", isSelected = input.ramGb == ram, accentColor = accent) {
                        viewModel.updateAnalyzerInput { it.copy(ramGb = ram) }
                    }
                }
            }

            // Target FPS Selector
            Text("Target Frame Rate:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                fpsOptions.forEach { fps ->
                    ChipOption(text = "${fps} FPS", isSelected = input.targetFps == fps, accentColor = accent) {
                        viewModel.updateAnalyzerInput { it.copy(targetFps = fps) }
                    }
                }
            }

            // Playstyle & Aim
            Text("Tactical Playstyle:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(playstyleOptions) { style ->
                    ChipOption(text = style, isSelected = input.playstyle == style, accentColor = accent) {
                        viewModel.updateAnalyzerInput { it.copy(playstyle = style) }
                    }
                }
            }

            Text("Aim Technique:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(aimOptions) { aim ->
                    ChipOption(text = aim, isSelected = input.aimStyle == aim, accentColor = accent) {
                        viewModel.updateAnalyzerInput { it.copy(aimStyle = aim) }
                    }
                }
            }

            // Gyroscope Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceElevated)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gyroscope Sensor Active", style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp))
                Switch(
                    checked = input.isGyroOn,
                    onCheckedChange = { checked ->
                        viewModel.updateAnalyzerInput { it.copy(isGyroOn = checked) }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = accent
                    ),
                    modifier = Modifier.height(28.dp)
                )
            }

            // Generate Button
            Button(
                onClick = { viewModel.runAiGameAnalysis() },
                enabled = !isAnalyzing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("generate_analysis_button")
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("COMPUTING CALIBRATION...", style = MaterialTheme.typography.labelMedium)
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Generate", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("GENERATE AI RECOMMENDATION", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Right Column: AI Calibrated Output Cards
        Column(
            modifier = Modifier
                .weight(1.15f)
                .fillMaxHeight()
                .verticalScroll(rightScrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "CALIBRATED SENSITIVITY MATRIX",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            if (result != null) {
                val r = result!!
                GlassCard(modifier = Modifier.fillMaxWidth(), glowAccent = true) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CALIBRATED FOR: ${input.game}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "DPI: ${r.recommendedDpi}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = StatusOptimal,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Sensitivity Table Grid (2x3)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            SensiValueBadge("General Look", r.generalSensi, accent, Modifier.weight(1f))
                            SensiValueBadge("Red Dot Sight", r.redDotSensi, accent, Modifier.weight(1f))
                            SensiValueBadge("2X Scope", r.scope2xSensi, accent, Modifier.weight(1f))
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            SensiValueBadge("4X Scope", r.scope4xSensi, accent, Modifier.weight(1f))
                            SensiValueBadge("Sniper Scope", r.sniperSensi, accent, Modifier.weight(1f))
                            SensiValueBadge("Free Look", r.freeLookSensi, accent, Modifier.weight(1f))
                        }

                        // HUD and Graphics Suggestions
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text("🎯 HUD & Button Layout:", style = MaterialTheme.typography.labelSmall.copy(color = accent, fontSize = 9.sp))
                                Text(r.hudSuggestion, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 9.5.sp, color = TextPrimary))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("⚡ Graphics Optimization:", style = MaterialTheme.typography.labelSmall.copy(color = accent, fontSize = 9.sp))
                                Text(r.graphicsSetting, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 9.5.sp, color = TextSecondary))
                            }
                        }

                        // Estimation disclaimer badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Disclaimer", tint = TextMuted, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Recommendations are AI calibrated estimates. Fine-tune in practice range.",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 8.5.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChipOption(
    text: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.25f) else DarkSurfaceElevated)
            .border(1.dp, if (isSelected) accentColor else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) accentColor else TextPrimary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun SensiValueBadge(
    label: String,
    value: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, color = TextMuted),
                maxLines = 1
            )
            Text(
                text = "$value",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            )
        }
    }
}
