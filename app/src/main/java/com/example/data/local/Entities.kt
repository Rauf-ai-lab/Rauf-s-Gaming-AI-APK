package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "game_profiles")
@JsonClass(generateAdapter = true)
data class GameProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val packageName: String,
    val genre: String,
    val targetFps: Int = 60,
    val performanceMode: String = "Performance", // Balanced, Performance, Extreme, Eco
    val touchSensitivityBoost: Int = 85, // 0-100%
    val dndEnabled: Boolean = true,
    val customNotes: String = "",
    val lastPlayedTime: Long = System.currentTimeMillis(),
    val iconResName: String = "ic_game_default"
)

@Entity(tableName = "chat_messages")
@JsonClass(generateAdapter = true)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "user" or "ai" or "system"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val category: String = "General" // "Sensitivity", "Performance", "Network", "Strategy"
)

@Entity(tableName = "performance_snapshots")
@JsonClass(generateAdapter = true)
data class PerformanceSnapshot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val fps: Int,
    val pingMs: Int,
    val tempCelsius: Float,
    val ramUsedMb: Long,
    val ramTotalMb: Long,
    val cpuUsagePercent: Int,
    val batteryPercent: Int,
    val performanceMode: String
)
