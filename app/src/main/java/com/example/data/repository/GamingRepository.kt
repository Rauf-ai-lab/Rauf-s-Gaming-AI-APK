package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessage
import com.example.data.local.GameProfile
import com.example.data.local.PerformanceSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GamingRepository(private val database: AppDatabase) {

    val allGameProfiles: Flow<List<GameProfile>> = database.gameProfileDao().getAllProfiles()
    val allChatMessages: Flow<List<ChatMessage>> = database.chatMessageDao().getAllMessages()
    val recentPerformanceSnapshots: Flow<List<PerformanceSnapshot>> =
        database.performanceDao().getRecentSnapshots()

    suspend fun initializeDefaultGamesIfEmpty() {
        val count = database.gameProfileDao().getCount()
        if (count == 0) {
            val defaults = listOf(
                GameProfile(
                    name = "Free Fire MAX",
                    packageName = "com.dts.freefiremax",
                    genre = "Battle Royale",
                    targetFps = 90,
                    performanceMode = "Extreme",
                    touchSensitivityBoost = 95,
                    dndEnabled = true,
                    customNotes = "Drag Headshot & High Sensitivity Profile Active",
                    iconResName = "free_fire"
                ),
                GameProfile(
                    name = "BGMI / PUBG Mobile",
                    packageName = "com.pubg.imobile",
                    genre = "Battle Royale",
                    targetFps = 90,
                    performanceMode = "Performance",
                    touchSensitivityBoost = 88,
                    dndEnabled = true,
                    customNotes = "4-Finger Claw Gyroscope Calibration Loaded",
                    iconResName = "bgmi"
                ),
                GameProfile(
                    name = "Call of Duty: Mobile",
                    packageName = "com.activision.callofduty.shooter",
                    genre = "FPS / Multiplayer",
                    targetFps = 120,
                    performanceMode = "Extreme",
                    touchSensitivityBoost = 92,
                    dndEnabled = true,
                    customNotes = "Ultra Frame Rate + Fast Slide Aim Assist",
                    iconResName = "codm"
                ),
                GameProfile(
                    name = "Genshin Impact",
                    packageName = "com.miHoYo.GenshinImpact",
                    genre = "Open World RPG",
                    targetFps = 60,
                    performanceMode = "Balanced",
                    touchSensitivityBoost = 75,
                    dndEnabled = true,
                    customNotes = "Balanced Thermal Throttling Prevention Mode",
                    iconResName = "genshin"
                ),
                GameProfile(
                    name = "Mobile Legends: Bang Bang",
                    packageName = "com.mobile.legends",
                    genre = "MOBA",
                    targetFps = 120,
                    performanceMode = "Performance",
                    touchSensitivityBoost = 90,
                    dndEnabled = true,
                    customNotes = "High Frame Rate & Low Ping Buffer Optimization",
                    iconResName = "mlbb"
                )
            )

            defaults.forEach { profile ->
                database.gameProfileDao().insertProfile(profile)
            }
        }
    }

    suspend fun saveGameProfile(profile: GameProfile): Long {
        return database.gameProfileDao().insertProfile(profile)
    }

    suspend fun deleteGameProfile(id: Long) {
        database.gameProfileDao().deleteProfile(id)
    }

    suspend fun saveChatMessage(sender: String, message: String, category: String = "General"): Long {
        return database.chatMessageDao().insertMessage(
            ChatMessage(
                sender = sender,
                message = message,
                category = category
            )
        )
    }

    suspend fun clearChatHistory() {
        database.chatMessageDao().clearHistory()
    }

    suspend fun recordPerformanceSnapshot(snapshot: PerformanceSnapshot) {
        database.performanceDao().insertSnapshot(snapshot)
    }
}
