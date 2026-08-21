package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusExtreme
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel

@Composable
fun NetworkToolsScreen(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.hardwareStats.collectAsState()
    val accent = LocalTurboAccent.current
    var selectedDns by remember { mutableStateOf("Cloudflare 1.1.1.1") }
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Live Ping & Connection Diagnostics
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "NETWORK LATENCY TELEMETRY",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // Live Ping Big Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                glowAccent = true
            ) {
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
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (stats.pingMs <= 45) StatusOptimal else StatusWarning)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stats.networkType,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Text(
                            text = if (stats.pingMs <= 40) "EXCELLENT" else if (stats.pingMs <= 80) "GOOD" else "UNSTABLE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (stats.pingMs <= 40) StatusOptimal else StatusWarning,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Large Ping Number Display
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${stats.pingMs}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = if (stats.pingMs <= 40) StatusOptimal else if (stats.pingMs <= 80) StatusWarning else StatusExtreme
                            )
                        )
                        Text(
                            text = "MILLISECONDS (MS) LATENCY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    // Diagnostic Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NetMetricPill("Jitter", "2.1 ms", StatusOptimal, Modifier.weight(1f))
                        NetMetricPill("Loss", "0.0%", StatusOptimal, Modifier.weight(1f))
                        NetMetricPill("Stability", "99.8%", accent, Modifier.weight(1f))
                    }
                }
            }
        }

        // Right Column: DNS Switcher & Latency Optimization Tips
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "ESPORTS GAMING DNS ACCELERATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // DNS Profiles
            DnsSelectionCard(
                name = "Cloudflare Gaming DNS",
                ip = "1.1.1.1 / 1.0.0.1",
                features = "Fastest global routing & ultra-low jitter",
                isSelected = selectedDns == "Cloudflare 1.1.1.1",
                accentColor = accent,
                onClick = { selectedDns = "Cloudflare 1.1.1.1" }
            )

            DnsSelectionCard(
                name = "Google Public DNS",
                ip = "8.8.8.8 / 8.8.4.4",
                features = "High regional stability & packet fault tolerance",
                isSelected = selectedDns == "Google 8.8.8.8",
                accentColor = accent,
                onClick = { selectedDns = "Google 8.8.8.8" }
            )

            // Network Advice Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "💡 PRO GAMING NETWORK TIPS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "• Connect to 5GHz Wi-Fi channels (avoid 2.4GHz microwave interference).\n• Keep router MTU size at standard 1500 for optimal packet fragmentation.\n• Turn on Game Turbo DND to block background sync from cloud backups.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 9.5.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun NetMetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 8.5.sp))
            Text(value, style = MaterialTheme.typography.labelMedium.copy(color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp))
        }
    }
}

@Composable
fun DnsSelectionCard(
    name: String,
    ip: String,
    features: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isSelected) accentColor else null,
        glowAccent = isSelected,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) accentColor else TextPrimary,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = ip,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = features,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 9.5.sp
                    )
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
