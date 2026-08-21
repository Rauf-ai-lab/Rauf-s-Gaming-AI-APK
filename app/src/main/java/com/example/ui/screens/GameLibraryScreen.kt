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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.GameProfile
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.LocalTurboAccent
import com.example.ui.theme.StatusExtreme
import com.example.ui.theme.StatusOptimal
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
    val accent = LocalTurboAccent.current
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var newGameName by remember { mutableStateOf("") }
    var newGameGenre by remember { mutableStateOf("Battle Royale") }
    var newGameFps by remember { mutableIntStateOf(90) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Game List Column
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CONFIGURED GAME PROFILES (${profiles.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("add_game_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Game", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ADD GAME", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(profiles) { profile ->
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
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) accent.copy(alpha = 0.25f) else DarkSurfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = profile.name,
                                        tint = if (isSelected) accent else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = profile.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) accent else TextPrimary,
                                                fontSize = 13.sp
                                            )
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(accent)
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
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
                                    Text(
                                        text = "${profile.genre} • Target ${profile.targetFps} FPS • ${profile.customNotes}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextSecondary,
                                            fontSize = 9.5.sp
                                        )
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (!isSelected) {
                                    Button(
                                        onClick = { viewModel.setActiveGame(profile.name) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DarkSurfaceElevated,
                                            contentColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("ACTIVATE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp))
                                    }
                                }

                                Button(
                                    onClick = {
                                        Toast.makeText(context, "🚀 Launching ${profile.name} with Game Turbo Profile...", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accent,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Launch", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("LAUNCH", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp))
                                }

                                if (profile.id > 5) {
                                    IconButton(
                                        onClick = { viewModel.deleteGame(profile.id) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
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

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "ADD CUSTOM GAME PROFILE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newGameName,
                        onValueChange = { newGameName = it },
                        label = { Text("Game Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newGameGenre,
                        onValueChange = { newGameGenre = it },
                        label = { Text("Genre (e.g. Battle Royale, MOBA, Racing)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Target FPS:", style = MaterialTheme.typography.labelSmall)
                        listOf(60, 90, 120).forEach { fps ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (newGameFps == fps) accent else DarkSurfaceElevated)
                                    .clickable { newGameFps = fps }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$fps",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (newGameFps == fps) Color.Black else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGameName.isNotBlank()) {
                            viewModel.addCustomGame(newGameName, newGameGenre, newGameFps)
                            newGameName = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black)
                ) {
                    Text("ADD")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }
}
