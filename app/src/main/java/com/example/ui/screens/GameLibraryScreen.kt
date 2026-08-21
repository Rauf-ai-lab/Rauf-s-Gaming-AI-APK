package com.example.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GameProfile
import com.example.engine.AppItem
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusExtreme
import com.example.ui.theme.StatusOptimal
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameTurboViewModel

@Composable
fun GameLibraryScreen(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val profiles by viewModel.gameProfiles.collectAsState()
    val activeGame by viewModel.activeGame.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val isLoadingApps by viewModel.isLoadingInstalledApps.collectAsState()
    val accent = LocalTurboAccent.current
    val context = LocalContext.current

    var showAppPickerDialog by remember { mutableStateOf(false) }
    var editingProfile by remember { mutableStateOf<GameProfile?>(null) }
    var filterQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") } // ALL, GAMES, APPS

    LaunchedEffect(Unit) {
        if (installedApps.isEmpty()) {
            viewModel.scanInstalledApps()
        }
    }

    val filteredProfiles = remember(profiles, filterQuery, selectedCategoryFilter) {
        profiles.filter { profile ->
            val matchQuery = profile.name.contains(filterQuery, ignoreCase = true) ||
                    profile.genre.contains(filterQuery, ignoreCase = true)
            val matchCat = when (selectedCategoryFilter) {
                "GAMES" -> !profile.genre.contains("Tool", ignoreCase = true) && !profile.genre.contains("App", ignoreCase = true)
                "APPS" -> profile.genre.contains("Tool", ignoreCase = true) || profile.genre.contains("App", ignoreCase = true)
                else -> true
            }
            matchQuery && matchCat
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header Bar: Title, Search, Category Filter, and Prominent "+ ADD APP" Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "GAME LIBRARY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accent.copy(alpha = 0.2f))
                        .border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${profiles.size} CONFIGURED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category Filter Pills
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .padding(2.dp)
                ) {
                    listOf("ALL", "GAMES", "APPS").forEach { cat ->
                        val isSel = selectedCategoryFilter == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) accent else Color.Transparent)
                                .clickable { selectedCategoryFilter = cat }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) Color.Black else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                // Prominent "+ ADD APP" Button
                Button(
                    onClick = {
                        viewModel.scanInstalledApps()
                        showAppPickerDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("add_app_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add App",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+ ADD APP",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Search bar
        OutlinedTextField(
            value = filterQuery,
            onValueChange = { filterQuery = it },
            placeholder = {
                Text(
                    "Search installed game profiles or package name...",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                if (filterQuery.isNotEmpty()) {
                    IconButton(onClick = { filterQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceGlass,
                unfocusedContainerColor = DarkSurfaceGlass,
                focusedBorderColor = accent,
                unfocusedBorderColor = Color(0x334A628A),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        // Game Profiles List
        if (filteredProfiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceGlass)
                    .border(1.dp, Color(0x224A628A), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "No Games",
                        tint = TextMuted,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = if (filterQuery.isEmpty()) "No games configured yet." else "No matching apps found.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                    Button(
                        onClick = {
                            viewModel.scanInstalledApps()
                            showAppPickerDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ADD INSTALLED APP", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredProfiles, key = { it.id }) { profile ->
                    val isSelected = profile.name == activeGame
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (isSelected) accent else null,
                        borderWidth = if (isSelected) 1.5.dp else 1.dp,
                        glowAccent = isSelected
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Section: App Icon & Details
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Find app icon if matching installed app exists
                                val matchedInstalled = installedApps.find { it.packageName == profile.packageName }
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) accent.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                        .border(
                                            1.dp,
                                            if (isSelected) accent else Color(0x334A628A),
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (matchedInstalled?.iconBitmap != null) {
                                        Image(
                                            bitmap = matchedInstalled.iconBitmap.asImageBitmap(),
                                            contentDescription = profile.name,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (profile.genre.contains("App", ignoreCase = true) || profile.genre.contains("Tool", ignoreCase = true)) Icons.Default.Apps else Icons.Default.SportsEsports,
                                            contentDescription = profile.name,
                                            tint = if (isSelected) accent else TextSecondary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = profile.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) accent else TextPrimary,
                                                fontSize = 13.5.sp
                                            )
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(accent)
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "ACTIVE",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 8.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    // Profile Badges
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // Mode Badge
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(DarkSurfaceElevated)
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "${profile.performanceMode.uppercase()} MODE",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = when (profile.performanceMode) {
                                                        "Extreme" -> StatusExtreme
                                                        "Performance" -> accent
                                                        else -> StatusOptimal
                                                    },
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }

                                        // FPS Target
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(DarkSurfaceElevated)
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "${profile.targetFps} FPS",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF38BDF8),
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }

                                        // Touch Sensitivity
                                        Text(
                                            text = "Touch: ${profile.touchSensitivityBoost}%",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }

                            // Right Section: Action Controls
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Activate Profile
                                if (!isSelected) {
                                    Button(
                                        onClick = { viewModel.setActiveGame(profile.name) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DarkSurfaceElevated,
                                            contentColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(
                                            "ACTIVATE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp)
                                        )
                                    }
                                }

                                // Edit Profile Details
                                IconButton(
                                    onClick = { editingProfile = profile },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurfaceElevated)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = "Edit Profile",
                                        tint = accent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Launch Game/App
                                Button(
                                    onClick = {
                                        val launched = viewModel.launchGameOrApp(profile)
                                        if (launched) {
                                            Toast.makeText(context, "🚀 Launching ${profile.name} with Turbo Boost...", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "⚡ Game Turbo Profile Activated for ${profile.name}", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accent,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Launch",
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        "LAUNCH",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                // Delete custom / added apps
                                if (profile.id > 3) {
                                    IconButton(
                                        onClick = { viewModel.deleteGame(profile.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove App",
                                            tint = StatusExtreme,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // ADD APPS PICKER DIALOG
    // ==========================================
    if (showAppPickerDialog) {
        InstalledAppsPickerDialog(
            installedApps = installedApps,
            configuredPackageNames = profiles.map { it.packageName }.toSet(),
            isLoading = isLoadingApps,
            accent = accent,
            onDismiss = { showAppPickerDialog = false },
            onRefresh = { viewModel.scanInstalledApps() },
            onAddApp = { app, fps, mode, genre ->
                viewModel.addInstalledApp(app, fps, mode, genre)
            },
            onAddCustomGame = { name, genre, fps ->
                viewModel.addCustomGame(name, genre, fps)
            }
        )
    }

    // ==========================================
    // EDIT GAME PROFILE DIALOG
    // ==========================================
    editingProfile?.let { prof ->
        EditGameProfileDialog(
            profile = prof,
            accent = accent,
            onDismiss = { editingProfile = null },
            onSave = { updated ->
                viewModel.updateGameProfile(updated)
                editingProfile = null
            }
        )
    }
}

@Composable
fun InstalledAppsPickerDialog(
    installedApps: List<AppItem>,
    configuredPackageNames: Set<String>,
    isLoading: Boolean,
    accent: Color,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
    onAddApp: (AppItem, Int, String, String) -> Unit,
    onAddCustomGame: (String, String, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") } // ALL, GAMES, APPS, CUSTOM
    var targetFps by remember { mutableIntStateOf(90) }
    var perfMode by remember { mutableStateOf("Extreme") }

    // Custom App Tab
    var customName by remember { mutableStateOf("") }
    var customGenre by remember { mutableStateOf("Battle Royale") }

    val filteredApps = remember(installedApps, searchQuery, filterType) {
        installedApps.filter { app ->
            val matchQuery = app.appName.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)
            val matchType = when (filterType) {
                "GAMES" -> app.isGame
                "APPS" -> !app.isGame
                else -> true
            }
            matchQuery && matchType
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Apps, "Apps", tint = accent, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ADD APPS & GAMES TO TURBO",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = accent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
                IconButton(onClick = onRefresh, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Refresh, "Refresh", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category Tabs: ALL APPS, GAMES, OTHER APPS, CUSTOM GAME
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("ALL", "GAMES", "APPS", "CUSTOM").forEach { tab ->
                        val isSel = filterType == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) accent else Color.Transparent)
                                .clickable { filterType = tab }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) Color.Black else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                )
                            )
                        }
                    }
                }

                if (filterType == "CUSTOM") {
                    // Custom Profile Creator
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("Game or App Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = customGenre,
                            onValueChange = { customGenre = it },
                            label = { Text("Genre (e.g. Battle Royale, MOBA, Simulator)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Target FPS:", style = MaterialTheme.typography.labelSmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(60, 90, 120).forEach { fps ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (targetFps == fps) accent else DarkSurfaceElevated)
                                            .clickable { targetFps = fps }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "$fps FPS",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (targetFps == fps) Color.Black else TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                if (customName.isNotBlank()) {
                                    onAddCustomGame(customName, customGenre, targetFps)
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CREATE GAME PROFILE", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Search Installed Apps
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search device apps...", fontSize = 10.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, "Search", tint = accent, modifier = Modifier.size(14.dp)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
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

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = accent, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Scanning Installed Apps...", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    } else if (filteredApps.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No matching installed apps found.", fontSize = 11.sp, color = TextMuted)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(filteredApps) { app ->
                                val isAlreadyAdded = configuredPackageNames.contains(app.packageName)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, if (isAlreadyAdded) accent.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF1E293B)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (app.iconBitmap != null) {
                                                Image(
                                                    bitmap = app.iconBitmap.asImageBitmap(),
                                                    contentDescription = app.appName,
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = if (app.isGame) Icons.Default.SportsEsports else Icons.Default.Apps,
                                                    contentDescription = app.appName,
                                                    tint = accent,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = app.appName,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary,
                                                    fontSize = 11.sp
                                                ),
                                                maxLines = 1
                                            )
                                            Text(
                                                text = if (app.isGame) "Game • ${app.packageName}" else "App • ${app.packageName}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = TextMuted,
                                                    fontSize = 8.5.sp
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    if (isAlreadyAdded) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(accent.copy(alpha = 0.2f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "ADDED",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = accent,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                onAddApp(
                                                    app,
                                                    targetFps,
                                                    perfMode,
                                                    if (app.isGame) "Esports Game" else "Accelerated App"
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = accent,
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("+ ADD", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated, contentColor = TextPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("DONE")
            }
        },
        containerColor = DarkSurfaceCard
    )
}

@Composable
fun EditGameProfileDialog(
    profile: GameProfile,
    accent: Color,
    onDismiss: () -> Unit,
    onSave: (GameProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var genre by remember { mutableStateOf(profile.genre) }
    var targetFps by remember { mutableIntStateOf(profile.targetFps) }
    var perfMode by remember { mutableStateOf(profile.performanceMode) }
    var touchBoost by remember { mutableFloatStateOf(profile.touchSensitivityBoost.toFloat()) }
    var dndEnabled by remember { mutableStateOf(profile.dndEnabled) }
    var customNotes by remember { mutableStateOf(profile.customNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "CONFIGURE GAME PROFILE",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Profile Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Performance Mode Selector
                Text("Performance Mode:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Extreme", "Performance", "Balanced", "Eco").forEach { mode ->
                        val isSel = perfMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) accent else DarkSurfaceElevated)
                                .clickable { perfMode = mode }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) Color.Black else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                // Target FPS Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Target FPS:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(60, 90, 120).forEach { fps ->
                            val isSel = targetFps == fps
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) accent else DarkSurfaceElevated)
                                    .clickable { targetFps = fps }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$fps FPS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) Color.Black else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Touch Sensitivity Boost Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Touch Boost:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    Text("${touchBoost.toInt()}%", style = MaterialTheme.typography.labelSmall.copy(color = accent, fontWeight = FontWeight.Bold))
                }
                Slider(
                    value = touchBoost,
                    onValueChange = { touchBoost = it },
                    valueRange = 50f..100f,
                    colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent),
                    modifier = Modifier.height(20.dp)
                )

                // DND Gaming switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto DND & Call Block", style = MaterialTheme.typography.labelSmall)
                    Switch(
                        checked = dndEnabled,
                        onCheckedChange = { dndEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = accent, checkedTrackColor = accent.copy(alpha = 0.4f))
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        profile.copy(
                            name = name,
                            genre = genre,
                            targetFps = targetFps,
                            performanceMode = perfMode,
                            touchSensitivityBoost = touchBoost.toInt(),
                            dndEnabled = dndEnabled,
                            customNotes = customNotes
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("SAVE PROFILE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        },
        containerColor = DarkSurfaceCard
    )
}
