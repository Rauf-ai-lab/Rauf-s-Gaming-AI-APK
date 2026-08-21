package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingOverlayHud
import com.example.ui.components.HudBatteryBadge
import com.example.ui.components.HudFpsBadge
import com.example.ui.components.HudPingBadge
import com.example.ui.components.HudTempBadge
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AiGameAnalyzerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GameLibraryScreen
import com.example.ui.screens.GamingToolsScreen
import com.example.ui.screens.NetworkToolsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PerformanceScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.GameTurboTheme
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {

    private val viewModel: GameTurboViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val accent by viewModel.currentAccent.collectAsState()
            val isOnboardingDone by viewModel.isOnboardingCompleted.collectAsState()

            GameTurboTheme(accentColor = accent) {
                if (!isOnboardingDone) {
                    OnboardingScreen(viewModel = viewModel)
                } else {
                    GameTurboMainContainer(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GameTurboMainContainer(viewModel: GameTurboViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val stats by viewModel.hardwareStats.collectAsState()
    val isBoosting by viewModel.isBoosting.collectAsState()
    val accent = LocalTurboAccent.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = ObsidianDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Navigation Rail (Futuristic HyperOS Glass)
                NavigationRailBar(
                    currentTab = currentTab,
                    onSelectTab = { viewModel.selectTab(it) },
                    accentColor = accent
                )

                // Main Content Workspace
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    // Top Telemetry Header Bar
                    TopTelemetryHeader(
                        stats = stats,
                        isBoosting = isBoosting,
                        onBoost = { viewModel.triggerQuickBoost() },
                        accentColor = accent
                    )

                    // Active Tab Screen View
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_transition"
                        ) { tab ->
                            when (tab) {
                                NavigationTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                                NavigationTab.PERFORMANCE -> PerformanceScreen(viewModel = viewModel)
                                NavigationTab.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel)
                                NavigationTab.AI_ANALYZER -> AiGameAnalyzerScreen(viewModel = viewModel)
                                NavigationTab.NETWORK -> NetworkToolsScreen(viewModel = viewModel)
                                NavigationTab.TOOLS -> GamingToolsScreen(viewModel = viewModel)
                                NavigationTab.LIBRARY -> GameLibraryScreen(viewModel = viewModel)
                                NavigationTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }

            // Slide-out Floating Overlay HUD
            FloatingOverlayHud(
                viewModel = viewModel,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
    }
}

@Composable
fun NavigationRailBar(
    currentTab: NavigationTab,
    onSelectTab: (NavigationTab) -> Unit,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(68.dp)
            .background(DarkSurfaceGlass)
            .border(
                width = 1.dp,
                color = Color(0x334A628A),
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            )
            .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .padding(vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App Logo Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.dp, accentColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Game Turbo",
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Navigation Items
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NavRailItem(
                    tab = NavigationTab.DASHBOARD,
                    icon = Icons.Default.Dashboard,
                    isSelected = currentTab == NavigationTab.DASHBOARD,
                    accentColor = accentColor,
                    onSelect = { onSelectTab(NavigationTab.DASHBOARD) }
                )
                NavRailItem(
                    tab = NavigationTab.PERFORMANCE,
                    icon = Icons.Default.Speed,
                    isSelected = currentTab == NavigationTab.PERFORMANCE,
                    accentColor = accentColor,
                    onSelect = { onSelectTab(NavigationTab.PERFORMANCE) }
                )
                NavRailItem(
                    tab = NavigationTab.AI_ASSISTANT,
                    icon = Icons.Default.Psychology,
                    isSelected = currentTab == NavigationTab.AI_ASSISTANT,
                    accentColor = accentColor,
                    onSelect = { onSelectTab(NavigationTab.AI_ASSISTANT) }
                )
                NavRailItem(
                    tab = NavigationTab.AI_ANALYZER,
                    icon = Icons.Default.Tune,
                    isSelected = currentTab == NavigationTab.AI_ANALYZER,
                    accentColor = accentColor,
                    onSelect = { onSelectTab(NavigationTab.AI_ANALYZER) }
                )
                NavRailItem(
                    tab = NavigationTab.NETWORK,
                    icon = Icons.Default.Wifi,
                    isSelected = currentTab == NavigationTab.NETWORK,
                    accentColor = accentColor,
                    onSelect = { onSelectTab(NavigationTab.NETWORK) }
                )
                NavRailItem(
                    tab = NavigationTab.TOOLS,
                    icon = Icons.Default.Handyman,
                    isSelected = currentTab == NavigationTab.TOOLS,
                    accentColor = accentColor,
                    onSelect = { onSelectTab(NavigationTab.TOOLS) }
                )
                NavRailItem(
                    tab = NavigationTab.LIBRARY,
                    icon = Icons.Default.SportsEsports,
                    isSelected = currentTab == NavigationTab.LIBRARY,
                    accentColor = accentColor,
                    onSelect = { onSelectTab(NavigationTab.LIBRARY) }
                )
            }

            // Settings item at bottom
            NavRailItem(
                tab = NavigationTab.SETTINGS,
                icon = Icons.Default.Settings,
                isSelected = currentTab == NavigationTab.SETTINGS,
                accentColor = accentColor,
                onSelect = { onSelectTab(NavigationTab.SETTINGS) }
            )
        }
    }
}

@Composable
fun NavRailItem(
    tab: NavigationTab,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.22f) else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) accentColor.copy(alpha = 0.8f) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .testTag("nav_${tab.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.title,
            tint = if (isSelected) accentColor else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun TopTelemetryHeader(
    stats: com.example.engine.SystemHardwareStats,
    isBoosting: Boolean,
    onBoost: () -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "GAME TURBO",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(accentColor)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp
                    )
                )
            }
        }

        // Live Telemetry Badges
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HudFpsBadge(fps = stats.fps, targetFps = stats.targetFps)
            HudPingBadge(pingMs = stats.pingMs)
            HudTempBadge(tempCelsius = stats.batteryTempCelsius)
            HudBatteryBadge(batteryPercent = stats.batteryPercent, isCharging = stats.isCharging)

            // Quick Boost Action Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.dp, accentColor, RoundedCornerShape(20.dp))
                    .clickable(enabled = !isBoosting, onClick = onBoost)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("header_boost_pill")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isBoosting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = accentColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "Boost",
                            tint = accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBoosting) "BOOSTING" else "BOOST",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }
        }
    }
}
