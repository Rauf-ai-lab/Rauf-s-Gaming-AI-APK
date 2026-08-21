package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import com.example.data.local.ChatMessage
import com.example.data.remote.AiConnectionStatus
import com.example.engine.VoiceState
import com.example.ui.components.AudioVisualizerBar
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.DarkSurfaceGlassLight
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusExtreme
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel

@Composable
fun AiAssistantScreen(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val streamingText by viewModel.streamingAiResponse.collectAsState()
    val isGenerating by viewModel.isAiGenerating.collectAsState()
    val activeGame by viewModel.activeGame.collectAsState()
    val aiStatus by viewModel.aiStatus.collectAsState()
    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
    val isListening by viewModel.voiceEngine.isListening.collectAsState()
    val voiceAmplitude by viewModel.voiceEngine.speechAmplitude.collectAsState()
    val voiceState by viewModel.voiceEngine.voiceState.collectAsState()
    val voiceLang by viewModel.voiceLanguage.collectAsState()
    val speechSpeed by viewModel.speechSpeed.collectAsState()
    val autoSpeak by viewModel.autoSpeakResponse.collectAsState()

    val accent = LocalTurboAccent.current
    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Audio Permission Launcher
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
            Toast.makeText(context, "Microphone permission is required for voice assistant", Toast.LENGTH_LONG).show()
        }
    }

    fun requestVoice(isPtt: Boolean = false) {
        if (hasAudioPermission) {
            viewModel.startVoiceInput(isPtt)
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Chakra aura pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "chakra_avatar_pulse")
    val avatarGlowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isGenerating || isSpeaking || isListening) 600 else 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_glow"
    )

    LaunchedEffect(messages.size, streamingText.length) {
        val targetIndex = (messages.size + (if (streamingText.isNotEmpty()) 1 else 0) - 1).coerceAtLeast(0)
        if (targetIndex > 0) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    val quickPrompts = listOf(
        "🦊 Sage Mode: Calibrate extreme 120 FPS graphics for $activeGame",
        "🎯 Drag Headshot: Optimal sensitivity, DPI & fire button size",
        "⚡ Chidori Speed: Fix frame drops & thermal throttling",
        "📶 Ping Fix: 5GHz DNS packet routing for low latency",
        "⚔️ 4-Finger Claw: HUD button placement & jump-shot timing",
        "🗣️ Give pro esports advice in Hinglish"
    )

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left Column: Chat History & Interactive Voice + Text Bar
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar with Anime Avatar, Live Mic Status & Actions
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .scale(if (isGenerating || isSpeaking || isListening) avatarGlowScale else 1.0f)
                                .clip(CircleShape)
                                .border(1.5.dp, if (isListening) StatusExtreme else if (isGenerating) Color(0xFFFF9800) else accent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_anime_ai_avatar),
                                contentDescription = "Anime AI Ninja Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SHINOBI REAL-TIME VOICE AI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = accent,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isListening) StatusExtreme else if (aiStatus == AiConnectionStatus.ONLINE) StatusOptimal else Color(0xFF38BDF8))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = when {
                                            isListening -> "● LISTENING"
                                            isGenerating -> "THINKING"
                                            isSpeaking -> "SPEAKING"
                                            else -> "ONLINE"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.Black,
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Profile: $activeGame • Voice: $voiceLang",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 8.5.sp
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSpeaking) {
                            AudioVisualizerBar(isActive = true, color = accent, barCount = 8)
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { viewModel.stopSpeaking() },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop Voice",
                                    tint = StatusExtreme,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // New Conversation button
                        IconButton(
                            onClick = {
                                viewModel.clearChatHistory()
                                Toast.makeText(context, "New Conversation Started", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "New Conversation",
                                tint = accent,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.clearChatHistory() },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear Chat",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Chat Messages Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty() && streamingText.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(2.dp, accent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_anime_ai_avatar),
                                contentDescription = "Anime AI Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "GEMINI FLASH GAMING VOICE ASSISTANT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = accent,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = "Active for $activeGame. Tap the 🎙️ microphone to talk or hold to speak. Ask about drag sensitivity, FPS stabilization, or 4-finger claw rotation.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 9.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(messages) { msg ->
                            ChatMessageBubble(
                                message = msg,
                                accentColor = accent,
                                onCopy = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("GameTurboAI", msg.message))
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                onSpeak = { viewModel.speakAiMessage(msg.message) }
                            )
                        }

                        if (streamingText.isNotEmpty()) {
                            item {
                                ChatMessageBubble(
                                    message = ChatMessage(
                                        sender = "ai",
                                        message = streamingText,
                                        isStreaming = true
                                    ),
                                    accentColor = accent,
                                    onCopy = {},
                                    onSpeak = {}
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Quick Prompt Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(quickPrompts) { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .clickable { viewModel.sendChatMessage(prompt, "Quick") }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Interactive Voice & Text Input Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Large 🎙️ ASK AI Voice Button
                Button(
                    onClick = {
                        if (isListening) {
                            viewModel.stopVoiceInput()
                        } else {
                            requestVoice(isPtt = false)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isListening) StatusExtreme else accent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("ai_screen_ask_voice_button")
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                // Push-To-Talk Button
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isListening) accent.copy(alpha = 0.25f) else DarkSurfaceElevated)
                        .border(1.dp, if (isListening) accent else Color(0x444A628A), RoundedCornerShape(10.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    requestVoice(isPtt = true)
                                    tryAwaitRelease()
                                    viewModel.stopVoiceInput()
                                }
                            )
                        }
                        .padding(horizontal = 10.dp)
                        .testTag("ai_screen_ptt_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "PTT",
                            tint = if (isListening) accent else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "HOLD TALK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isListening) accent else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                // Text input field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Type or talk to Gemini AI...",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp, color = TextMuted)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("ai_input_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceGlass,
                        unfocusedContainerColor = DarkSurfaceGlass,
                        focusedBorderColor = accent,
                        unfocusedBorderColor = DarkSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                if (isGenerating) {
                    IconButton(
                        onClick = { viewModel.stopAiGeneration() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusExtreme.copy(alpha = 0.25f))
                            .border(1.dp, StatusExtreme, RoundedCornerShape(8.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop", tint = StatusExtreme)
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendChatMessage(inputText, "User")
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accent)
                            .testTag("ai_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Right Column: Voice Assistant Settings & Chakra Tactical Presets
        Column(
            modifier = Modifier
                .weight(0.75f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Voice Settings Glass Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "VOICE ENGINE SETTINGS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        )
                    )

                    // Language Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val languages = listOf("English", "Hindi", "Hinglish")
                        languages.forEach { lang ->
                            val isSel = voiceLang.equals(lang, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) accent.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                    .border(1.dp, if (isSel) accent else Color.Transparent, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setVoiceLanguage(lang) }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) accent else TextSecondary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 8.5.sp
                                    )
                                )
                            }
                        }
                    }

                    // Auto Speak Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurfaceElevated)
                            .clickable { viewModel.toggleAutoSpeak() }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Auto-Speak Response",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, color = TextPrimary)
                        )
                        Text(
                            text = if (autoSpeak) "ON" else "OFF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (autoSpeak) StatusOptimal else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }

                    // Hands-Free Mode Toggle Row
                    val handsFree by viewModel.handsFreeModeEnabled.collectAsState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurfaceElevated)
                            .clickable { viewModel.toggleHandsFreeMode() }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hands-Free Mode",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, color = TextPrimary)
                        )
                        Text(
                            text = if (handsFree) "ON" else "OFF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (handsFree) StatusOptimal else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            // Anime Chakra Tactical Presets Card
            GlassCard(modifier = Modifier.fillMaxWidth(), glowAccent = true) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, "Chakra", tint = Color(0xFFFF5722), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CHAKRA JUTSU PRESETS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }

                    ChakraPresetButton(
                        title = "🔥 Sage Mode 120 FPS",
                        subtitle = "Thermal Graphics Override",
                        accent = accent,
                        onClick = {
                            viewModel.setPerformanceMode("Extreme")
                            viewModel.sendChatMessage("Activate Sage Mode Extreme 120 FPS optimization for $activeGame", "Performance")
                        }
                    )

                    ChakraPresetButton(
                        title = "🎯 Sharingan Aim Assist",
                        subtitle = "Auto Drag Headshot Sensi",
                        accent = accent,
                        onClick = {
                            viewModel.selectTab(com.example.ui.viewmodel.NavigationTab.AI_ANALYZER)
                        }
                    )

                    ChakraPresetButton(
                        title = "⚡ Chidori Low-Ping Turbo",
                        subtitle = "5GHz DNS Packet Routing",
                        accent = accent,
                        onClick = {
                            viewModel.selectTab(com.example.ui.viewmodel.NavigationTab.NETWORK)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChakraPresetButton(
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, Color(0x334A628A), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 10.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 8.sp
                )
            )
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    accentColor: Color,
    onCopy: () -> Unit,
    onSpeak: () -> Unit
) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .background(
                    if (isUser) accentColor.copy(alpha = 0.22f)
                    else DarkSurfaceGlassLight
                )
                .border(
                    1.dp,
                    if (isUser) accentColor.copy(alpha = 0.6f)
                    else DarkSurfaceElevated,
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Column {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SHINOBI AI COPILOT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                )
                            )
                        }
                        Row {
                            IconButton(onClick = onSpeak, modifier = Modifier.size(18.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Speak",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            IconButton(onClick = onCopy, modifier = Modifier.size(18.dp)) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }
    }
}
