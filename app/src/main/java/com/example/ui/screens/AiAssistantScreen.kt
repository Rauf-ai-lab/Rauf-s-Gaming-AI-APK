package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Auto-scroll to bottom when new messages or chunks arrive
    LaunchedEffect(messages.size, streamingText.length) {
        val targetIndex = (messages.size + (if (streamingText.isNotEmpty()) 1 else 0) - 1).coerceAtLeast(0)
        if (targetIndex > 0) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    val quickPrompts = listOf(
        "🎯 Best drag headshot sensitivity for $activeGame",
        "⚡ Fix 60FPS to 90FPS frame drops and thermal lag",
        "📶 Optimize ping & DNS for lower latency",
        "⚔️ Best 4-Finger claw HUD button layout",
        "🛡️ Low RAM graphics settings for tournament play",
        "🗣️ Give tips in Hinglish / Hindi"
    )

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left Column: Chat Area with Streaming Response
        Column(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar with AI Status and Clear Chat
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (aiStatus == AiConnectionStatus.ONLINE) StatusOptimal else Color(0xFF38BDF8))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GAME TURBO AI ENGINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (aiStatus == AiConnectionStatus.ONLINE) "• Gemini Flash Online" else "• Smart Offline Co-Pilot",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSpeaking) {
                            AudioVisualizerBar(isActive = true, color = accent, barCount = 8)
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { viewModel.stopSpeaking() },
                                modifier = Modifier.size(28.dp)
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
                            modifier = Modifier.size(28.dp)
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

            // Chat Message List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty() && streamingText.isEmpty()) {
                    // Empty state welcome
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.15f))
                                .border(1.dp, accent.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Co-pilot",
                                tint = accent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "GAME TURBO AI ASSISTANT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Specialized in sensitivity calibrations, FPS boosts, latency diagnostics & esports tactics for $activeGame.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
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
                                onSpeak = {
                                    viewModel.speakAiMessage(msg.message)
                                }
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

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Prompt Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(quickPrompts) { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.sendChatMessage(prompt, "Quick")
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Input Bar Row
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
                            text = "Ask gaming question, sensitivity or FPS fix...",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp, color = TextMuted)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("ai_input_field"),
                    shape = RoundedCornerShape(12.dp),
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StatusExtreme.copy(alpha = 0.2f))
                            .border(1.dp, StatusExtreme, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = StatusExtreme
                        )
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accent)
                            .testTag("ai_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Right Column: AI Engine Info & Capabilities
        Column(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "AI COPILOT SPECS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AiCapabilityRow("Neural Model", "Gemini 3.5 Flash")
                    AiCapabilityRow("Active Game", activeGame)
                    AiCapabilityRow("Latency Mode", "Ultra-Low / Stream")
                    AiCapabilityRow("Language Support", "EN / HI / Hinglish")
                    AiCapabilityRow("Precision", "Esports Calibrated")
                }
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.sendChatMessage("Explain how frame pacing, ping jitter, and thermal throttling affect headshots and aim assist in $activeGame", "Education")
                }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Tips",
                            tint = accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LEARN FPS & AIM MECHANICS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to have AI explain how touch sampling rate and frame drops impact flick shots.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    )
                }
            }
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
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
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
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GAME TURBO AI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                        Row {
                            IconButton(onClick = onSpeak, modifier = Modifier.size(20.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Speak",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            IconButton(onClick = onCopy, modifier = Modifier.size(20.dp)) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(13.dp)
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
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
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
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 9.5.sp,
                color = TextMuted
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
