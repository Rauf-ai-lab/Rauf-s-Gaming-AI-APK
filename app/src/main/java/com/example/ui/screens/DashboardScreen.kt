package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.engine.VoiceState
import com.example.ui.components.AudioVisualizerBar
import com.example.ui.components.GlassCard
import com.example.ui.components.VoiceChakraOrb
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.DarkSurfaceGlassLight
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
    val voiceState by viewModel.voiceEngine.voiceState.collectAsState()
    val voiceAmplitude by viewModel.voiceEngine.speechAmplitude.collectAsState()
    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
    val isListening by viewModel.voiceEngine.isListening.collectAsState()
    val streamingText by viewModel.streamingAiResponse.collectAsState()
    val isAiGenerating by viewModel.isAiGenerating.collectAsState()
    val lastRecognizedText by viewModel.lastRecognizedSpeech.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val handsFreeEnabled by viewModel.handsFreeModeEnabled.collectAsState()
    val pttEnabled by viewModel.pushToTalkEnabled.collectAsState()
    val voiceLang by viewModel.voiceLanguage.collectAsState()

    val accent = LocalTurboAccent.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Permission launcher for microphone
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        if (granted) {
            viewModel.startVoiceInput()
        } else {
            Toast.makeText(context, "Microphone permission is required for voice commands", Toast.LENGTH_LONG).show()
        }
    }

    fun requestVoiceInput(isPtt: Boolean = false) {
        if (hasAudioPermission) {
            viewModel.startVoiceInput(isPtt)
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val latestAiMessage = chatMessages.lastOrNull { it.sender == "ai" }?.message ?: ""

    // 3-Column Landscape Dashboard Layout: Left (Library), Center (Voice AI), Right (Telemetry & Tools)
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ==========================================
        // LEFT COLUMN: Game Library & Active Profile
        // ==========================================
        Column(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Active Game Hero Glass Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.25f),
                glowAccent = true
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.bg_game_turbo_hero),
                        contentDescription = "Game Hero",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x77080B10),
                                        Color(0xCC080B10),
                                        Color(0xFF080B10)
                                    )
                                )
                            )
                    )

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
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(accent.copy(alpha = 0.2f))
                                        .border(1.dp, accent, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = "Game",
                                        tint = accent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "ACTIVE PROFILE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = accent,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = activeGame,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(accent.copy(alpha = 0.25f))
                                    .border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = stats.performanceMode.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = accent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }

                        // Live Metrics Mini Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MiniMetricTile("TARGET FPS", "${stats.targetFps} Hz", Icons.Default.Speed, accent, Modifier.weight(1f))
                            MiniMetricTile("RAM LOAD", "${stats.ramUsagePercent}%", Icons.Default.Bolt, Color(0xFF38BDF8), Modifier.weight(1f))
                            MiniMetricTile("NET PING", "${stats.pingMs} ms", Icons.Default.NetworkCheck, StatusOptimal, Modifier.weight(1f))
                        }

                        // Boost & Launch Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.triggerQuickBoost() },
                                enabled = !isBoosting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accent,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("boost_button")
                            ) {
                                if (isBoosting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = Color.Black,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("BOOSTING...", style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp))
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = "Boost",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("TURBO BOOST", style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                                }
                            }

                            // Launch / In-Game HUD Button
                            val activeProfile = profiles.firstOrNull { it.name == activeGame }
                            Button(
                                onClick = {
                                    if (activeProfile != null) {
                                        viewModel.launchGameOrApp(activeProfile)
                                    } else {
                                        viewModel.toggleFloatingOverlay()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkSurfaceElevated,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .border(1.dp, Color(0x334A628A), RoundedCornerShape(10.dp))
                                    .testTag("launch_game_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Launch",
                                    tint = accent,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("LAUNCH", style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp))
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(accent.copy(alpha = 0.18f))
                        .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = boostMsg ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = accent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Game Library Quick Switcher
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GAME LIBRARY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${profiles.size} Installed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accent,
                                fontSize = 8.5.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        contentPadding = PaddingValues(horizontal = 1.dp)
                    ) {
                        items(profiles) { profile ->
                            val isSelected = profile.name == activeGame
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected) accent.copy(alpha = 0.25f)
                                        else DarkSurfaceElevated
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) accent else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.setActiveGame(profile.name) }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) accent else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Quick "+ ADD APP" chip
                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(accent.copy(alpha = 0.15f))
                                    .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        viewModel.selectTab(NavigationTab.LIBRARY)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                    .testTag("dashboard_add_app_chip")
                            ) {
                                Text(
                                    text = "+ ADD APP",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = accent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // ========================================================
        // CENTER COLUMN: Gemini Real-Time Voice Gaming Assistant
        // ========================================================
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main Voice Assistant Control Glass Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                glowAccent = isListening || isSpeaking || isAiGenerating
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header with Avatar & Voice Mode Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.5.dp,
                                        if (isListening) StatusExtreme else if (isSpeaking) accent else Color(0x664A628A),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_anime_ai_avatar),
                                    contentDescription = "AI Ninja Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "GEMINI VOICE COPILOT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = accent,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    // Live Mic Status Indicator Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (isListening) StatusExtreme.copy(alpha = 0.25f)
                                                else if (isSpeaking) accent.copy(alpha = 0.25f)
                                                else DarkSurfaceElevated
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                isListening -> "● REC"
                                                isAiGenerating -> "THINKING"
                                                isSpeaking -> "SPEAKING"
                                                else -> "IDLE"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isListening) StatusExtreme else if (isSpeaking) accent else TextMuted,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 7.5.sp
                                            )
                                        )
                                    }
                                }
                                Text(
                                    text = "Language: $voiceLang • Flash Model Ready",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 8.5.sp
                                    )
                                )
                            }
                        }

                        // Top right quick actions: Interrupt / Stop or Full Screen
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSpeaking) {
                                IconButton(
                                    onClick = { viewModel.stopSpeaking() },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Stop Speech",
                                        tint = StatusExtreme,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { viewModel.selectTab(NavigationTab.AI_ASSISTANT) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "Open Chat",
                                    tint = accent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Voice Waveform & Dynamic Transcription Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceGlassLight)
                            .border(1.dp, if (isListening || isSpeaking) accent.copy(alpha = 0.6f) else Color(0x224A628A), RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            when {
                                isListening -> {
                                    Text(
                                        text = if (lastRecognizedText.isNotEmpty()) "\"$lastRecognizedText\"" else "Listening to your gaming command...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.5.sp
                                        ),
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    AudioVisualizerBar(
                                        isActive = true,
                                        amplitude = voiceAmplitude,
                                        barCount = 18,
                                        color = StatusExtreme
                                    )
                                }
                                isAiGenerating -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = accent,
                                            strokeWidth = 2.dp
                                        )
                                        Text(
                                            text = if (streamingText.isNotEmpty()) streamingText.takeLast(60) else "AI thinking & calculating tactics...",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextPrimary,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 2
                                        )
                                    }
                                }
                                isSpeaking || latestAiMessage.isNotEmpty() -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = latestAiMessage.take(130) + if (latestAiMessage.length > 130) "..." else "",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextPrimary,
                                                fontSize = 10.5.sp,
                                                lineHeight = 14.sp
                                            ),
                                            maxLines = 3
                                        )
                                        if (isSpeaking) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            AudioVisualizerBar(
                                                isActive = true,
                                                barCount = 16,
                                                color = accent
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    Text(
                                        text = "Tap 🎙️ ASK AI or Hold to Talk to ask for sensitivity, FPS optimization, or esports strategy.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextSecondary,
                                            fontSize = 10.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Main Voice Interactive Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Big 🎙️ ASK AI Button
                        Button(
                            onClick = {
                                if (isListening) {
                                    viewModel.stopVoiceInput()
                                } else {
                                    requestVoiceInput(isPtt = false)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListening) StatusExtreme else accent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(40.dp)
                                .testTag("ask_ai_voice_button")
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Voice",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isListening) "LISTENING..." else "🎙️ ASK AI",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // HOLD TO TALK Push-To-Talk Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isListening) accent.copy(alpha = 0.3f) else DarkSurfaceElevated)
                                .border(1.dp, if (isListening) accent else Color(0x444A628A), RoundedCornerShape(10.dp))
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            requestVoiceInput(isPtt = true)
                                            tryAwaitRelease()
                                            viewModel.stopVoiceInput()
                                        }
                                    )
                                }
                                .testTag("hold_to_talk_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "PTT",
                                    tint = if (isListening) accent else TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "HOLD TO TALK",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isListening) accent else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Quick Voice Commands Suggestions Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                contentPadding = PaddingValues(horizontal = 1.dp)
            ) {
                val voiceSuggestions = listOf(
                    "🎯 Best Sensi for $activeGame" to "Give me the best drag headshot sensitivity and DPI for $activeGame",
                    "🚀 Boost My Game" to "Optimize my device and boost game performance",
                    "⚡ Fix FPS Drops" to "Why am I getting frame drops and how to fix them?",
                    "📶 Reduce High Ping" to "My ping is very high, help me reduce network latency",
                    "⚔️ Rush or Hold?" to "Should I rush or hold position in safe zone for $activeGame?"
                )

                items(voiceSuggestions) { (chipLabel, fullPrompt) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.sendChatMessage(fullPrompt, "QuickVoice", isVoiceTriggered = true)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = chipLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }

        // ========================================================
        // RIGHT COLUMN: Performance, Network & Turbo Quick Tools
        // ========================================================
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live Hardware Telemetry Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TELEMETRY & HARDWARE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "${stats.fps} FPS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accent,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MiniMetricTile("CPU LOAD", "${stats.cpuUsagePercent}%", Icons.Default.Speed, Color(0xFF38BDF8), Modifier.weight(1f))
                        MiniMetricTile("TEMP", "${stats.batteryTempCelsius}°C", Icons.Default.Bolt, if (stats.batteryTempCelsius > 42) StatusExtreme else StatusOptimal, Modifier.weight(1f))
                    }
                }
            }

            // Quick AI Action Cards Grid
            Text(
                text = "TURBO AI TOOLS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 2.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickActionCard(
                    title = "BEST SENSI",
                    subtitle = "Drag Sensi",
                    icon = Icons.Default.Tune,
                    color = accent,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectTab(NavigationTab.AI_ANALYZER)
                    }
                )
                QuickActionCard(
                    title = "FIX LAG",
                    subtitle = "Thermal Graphics",
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
                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                    title = "STRATEGY",
                    subtitle = "Claw Rotation",
                    icon = Icons.Default.Psychology,
                    color = Color(0xFFA855F7),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectTab(NavigationTab.AI_ASSISTANT)
                        viewModel.sendChatMessage("Give me pro esports rush strategy and 4-finger claw rotation tips for $activeGame", "Strategy")
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
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated.copy(alpha = 0.85f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 7.5.sp,
                        color = TextMuted
                    )
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 11.sp,
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
        modifier = modifier.height(54.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.18f))
                    .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
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
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.sp,
                        color = TextSecondary
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
