package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ChatMessage
import com.example.data.remote.AiConnectionStatus
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
    val aiStatusDetails by viewModel.aiStatusDetails.collectAsState()
    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
    val accent = LocalTurboAccent.current
    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Chakra aura pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "chakra_avatar_pulse")
    val avatarGlowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isGenerating || isSpeaking) 600 else 1800, easing = LinearEasing),
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
        "🦊 Nine-Tails Mode: Extreme FPS calibration for $activeGame",
        "🎯 Best drag headshot sensitivity & DPI settings",
        "⚡ Chidori Speed: Fix frame drops & thermal throttling",
        "🌀 Rasengan: Clear RAM background cache & boost CPU",
        "⚔️ 4-Finger Claw HUD layout & quick switch setup",
        "🗣️ Give pro esports advice in Hinglish"
    )

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left Column: Chat History & Input
        Column(
            modifier = Modifier
                .weight(1.35f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar with Anime Avatar & AI Status
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
                        // Anime Ninja Avatar with Chakra Glow Ring
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .scale(if (isGenerating || isSpeaking) avatarGlowScale else 1.0f)
                                .clip(CircleShape)
                                .border(1.5.dp, if (isGenerating) StatusExtreme else accent, CircleShape),
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
                                    text = "SHINOBI NEURAL COPILOT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = accent,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (aiStatus == AiConnectionStatus.ONLINE) StatusOptimal else Color(0xFF38BDF8))
                                )
                            }
                            Text(
                                text = if (aiStatus == AiConnectionStatus.ONLINE) "Gemini Flash • Nine-Tails Chakra Active" else "Offline Neural Engine • Ready",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 8.5.sp
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSpeaking) {
                            AudioVisualizerBar(isActive = true, color = accent, barCount = 6)
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

            Spacer(modifier = Modifier.height(6.dp))

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
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "NINE-TAILS CHAKRA GAMING ASSISTANT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = accent,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Calibrated for $activeGame • Ask for custom drag sensitivities, FPS uncap configurations, or esports game rotations.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 20.dp)
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
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .clickable { viewModel.sendChatMessage(prompt, "Quick") }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Input Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask for sensitivity, FPS optimization, or ninja tactics...",
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

        // Right Column: Chakra Tactical Jutsu Cards & Engine Specs
        Column(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Anime Chakra Tactical Presets Card
            GlassCard(modifier = Modifier.fillMaxWidth(), glowAccent = true) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, "Chakra", tint = Color(0xFFFF5722), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CHAKRA JUTSU PRESETS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
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

            // Specs Glass Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "NEURAL SPECS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        )
                    )
                    AiCapabilityRow("AI Engine", "Gemini Flash")
                    AiCapabilityRow("Profile Target", activeGame)
                    AiCapabilityRow("Voice", viewModel.preferencesManager.voiceLanguage)
                    AiCapabilityRow("Telemetry", "Active 60-120Hz")
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
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 10.5.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 8.5.sp
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

@Composable
fun AiCapabilityRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 9.sp,
                color = TextMuted
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.5.sp,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
