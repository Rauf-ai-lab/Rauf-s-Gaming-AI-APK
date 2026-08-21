package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel

@Composable
fun OnboardingScreen(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }
    val accent = LocalTurboAccent.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
    ) {
        // Background Hero Artwork
        Image(
            painter = painterResource(id = R.drawable.bg_game_turbo_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Dimmer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x99080B10),
                            Color(0xEE080B10),
                            Color(0xFF080B10)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: Visual Icon & Branding
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.2f))
                        .border(2.dp, accent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (step) {
                            1 -> Icons.Default.RocketLaunch
                            2 -> Icons.Default.Speed
                            3 -> Icons.Default.Security
                            else -> Icons.Default.SportsEsports
                        },
                        contentDescription = "Step Icon",
                        tint = accent,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "GAME TURBO AI",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "HyperOS Gaming Co-Pilot",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Step Indicators
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in 1..4) {
                        Box(
                            modifier = Modifier
                                .size(width = if (step == i) 24.dp else 8.dp, height = 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (step == i) accent else DarkSurfaceElevated)
                        )
                    }
                }
            }

            // Right Column: Step Content Card
            GlassCard(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight(),
                glowAccent = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    AnimatedContent(
                        targetState = step,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "onboarding_step"
                    ) { targetStep ->
                        when (targetStep) {
                            1 -> StepWelcomeContent(accent)
                            2 -> StepFeaturesContent(accent)
                            3 -> StepPermissionsContent(accent)
                            else -> StepReadyContent(accent)
                        }
                    }

                    // Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (step > 1) {
                            TextButton(onClick = { step-- }) {
                                Text("BACK", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                            }
                        } else {
                            TextButton(onClick = { viewModel.completeOnboarding() }) {
                                Text("SKIP", color = TextMuted, style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Button(
                            onClick = {
                                if (step < 4) {
                                    step++
                                } else {
                                    viewModel.completeOnboarding()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("onboarding_next_button")
                        ) {
                            Text(
                                text = if (step == 4) "ENTER DASHBOARD" else "NEXT",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (step == 4) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepWelcomeContent(accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Welcome to Game Turbo AI",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = TextPrimary)
        )
        Text(
            text = "Experience esports-level gaming optimization inspired by the minimal, frosted-glass visual architecture of Xiaomi HyperOS 3.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 11.5.sp, lineHeight = 16.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OnboardingBullet(Icons.Default.RocketLaunch, "Landscape-First Floating HUD", "Instant access to in-game telemetry and live controls.", accent)
        OnboardingBullet(Icons.Default.Psychology, "Gemini Flash AI Co-Pilot", "Calibrates headshot sensitivities and FPS optimization plans.", accent)
    }
}

@Composable
fun StepFeaturesContent(accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "HyperOS Gaming Architecture",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = TextPrimary)
        )
        Text(
            text = "Four dedicated performance tiers and real-time hardware telemetry give you full tournament control.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 11.5.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OnboardingBullet(Icons.Default.Speed, "Extreme 120 FPS & 90 FPS Profiles", "Smooth frame pacing and thermal throttling guard.", accent)
        OnboardingBullet(Icons.Default.Widgets, "Touch & DNS Latency Boost", "High-frequency touch polling and 1.1.1.1 DNS acceleration.", accent)
    }
}

@Composable
fun StepPermissionsContent(accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Permissions & Privacy",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = TextPrimary)
        )
        Text(
            text = "Game Turbo AI requires standard Android permissions to provide in-game floating overlay, DND notification suppression, and voice co-pilot features.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 11.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OnboardingBullet(Icons.Default.Widgets, "Floating Overlay HUD", "Display over games for quick in-match controls.", accent)
        OnboardingBullet(Icons.Default.Security, "Local Data Encryption", "Sensitivities and chat logs are stored 100% on-device in Room DB.", accent)
    }
}

@Composable
fun StepReadyContent(accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Ready for Tournament Play!",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = TextPrimary)
        )
        Text(
            text = "Your Game Turbo AI workspace is primed. Tap below to enter the live dashboard, configure your sensitivities, and boost your frame rates.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 11.5.sp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.15f))
                .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Text(
                text = "⚡ Tip: Slide open the Game HUD anytime to monitor FPS, clean RAM, or ask the AI Co-Pilot for tactical advice during matches.",
                style = MaterialTheme.typography.bodyMedium.copy(color = accent, fontSize = 10.5.sp)
            )
        }
    }
}

@Composable
fun OnboardingBullet(
    icon: ImageVector,
    title: String,
    desc: String,
    accent: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 10.5.sp))
            Text(desc, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 9.sp))
        }
    }
}
