package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

enum class AiConnectionStatus {
    ONLINE,
    OFFLINE_FALLBACK,
    CONNECTING,
    ERROR
}

class GeminiRepository(private val preferencesManager: PreferencesManager) {

    private val systemPromptText = """
        You are GAME TURBO AI — a specialized, high-performance futuristic gaming co-pilot inspired by HyperOS Game Turbo.
        Your mission is to provide pro-level gaming optimization advice, tactical strategies, sensitivity calibrations, FPS stabilization guidance, and network diagnostics.
        
        Guidelines:
        1. Keep responses clear, structured, esports-focused, and concise for mobile gaming screens.
        2. Support multiple languages: English, Hindi, and Hinglish naturally based on user language preference or input.
        3. For Sensitivity questions: Provide explicit, calibrated values (General, Red Dot, 2X, 4X, Sniper, Free Look) along with DPI and HUD tips.
        4. For FPS / Lag / Thermal throttling: Provide actionable graphics settings (Resolution, Texture, Shadows, Battery saver off, Game Turbo Boost) and explain hardware limits honestly.
        5. Never claim to overclock hardware or bypass Android kernel limits. Frame optimization suggestions as legitimate Android & in-game configuration best practices.
    """.trimIndent()

    fun getEffectiveApiKey(): String {
        val customKey = preferencesManager.customApiKey.trim()
        if (customKey.isNotEmpty()) return customKey
        
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        
        return if (buildKey.isNotEmpty() && buildKey != "MY_GEMINI_API_KEY") {
            buildKey
        } else {
            ""
        }
    }

    fun isApiKeyConfigured(): Boolean {
        val key = getEffectiveApiKey()
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun testConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Pair(false, "API Key is not configured. Using Smart Offline Gaming AI Engine.")
        }
        try {
            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = "Respond with 'GAME_TURBO_READY' in one word."))
                    )
                ),
                generationConfig = GeminiGenerationConfig(maxOutputTokens = 10)
            )
            val response = RetrofitInstance.geminiService.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                Pair(true, "Connected to Gemini Flash AI Engine")
            } else {
                Pair(false, response.error?.message ?: "Received empty response from Gemini API")
            }
        } catch (e: Exception) {
            Log.w("GeminiRepository", "Test connection failed: ${e.message}")
            Pair(false, e.localizedMessage ?: "Network connection error")
        }
    }

    fun streamGameTurboChat(
        prompt: String,
        gameContext: String = "Free Fire MAX",
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Flow<String> = flow {
        val apiKey = getEffectiveApiKey()

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // High-precision offline gaming engine response
            val offlineResponse = generateOfflineGamingResponse(prompt, gameContext)
            for (chunk in offlineResponse.chunked(12)) {
                emit(chunk)
                kotlinx.coroutines.delay(20)
            }
            return@flow
        }

        try {
            val contentsList = mutableListOf<GeminiContent>()
            
            // Add previous conversational context (up to 4 turns)
            conversationHistory.takeLast(4).forEach { (role, msg) ->
                contentsList.add(
                    GeminiContent(
                        role = if (role == "user") "user" else "model",
                        parts = listOf(GeminiPart(text = msg))
                    )
                )
            }

            // Append current user prompt with game context
            val enrichedPrompt = if (contentsList.isEmpty()) {
                "[Active Game: $gameContext]\nUser: $prompt"
            } else {
                prompt
            }

            contentsList.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = enrichedPrompt))
                )
            )

            val request = GeminiGenerateRequest(
                contents = contentsList,
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPromptText))),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.65f,
                    maxOutputTokens = 1500
                )
            )

            val responseBody = RetrofitInstance.geminiService.streamGenerateContent(apiKey, request)
            val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val currentLine = line?.trim() ?: continue
                if (currentLine.isEmpty() || currentLine == "[" || currentLine == "]" || currentLine == ",") {
                    continue
                }

                try {
                    val cleanJson = if (currentLine.startsWith(",")) currentLine.substring(1) else currentLine
                    val jsonObject = JSONObject(cleanJson)
                    val candidates = jsonObject.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val part = parts.getJSONObject(0)
                            val textChunk = part.optString("text", "")
                            if (textChunk.isNotEmpty()) {
                                emit(textChunk)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignore non-json delimiters in SSE stream
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Streaming API error: ${e.message}", e)
            val fallback = "\n\n[Offline Mode Alert: Network/API limited]\n" +
                    generateOfflineGamingResponse(prompt, gameContext)
            emit(fallback)
        }
    }.flowOn(Dispatchers.IO)

    private fun generateOfflineGamingResponse(prompt: String, game: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("sensitivity") || lower.contains("sensi") || lower.contains("aim") || lower.contains("headshot") -> {
                """
                🎮 **GAME TURBO AI — Calibrated Sensitivity Setup for $game**
                
                ⚡ **Recommended Sensitivity (Touch Boost: ON)**:
                • **General / Camera Look**: 96 - 100 (Instant 180° flick turns)
                • **Red Dot / Iron Sight**: 88 - 92 (Smooth tracking)
                • **2X Scope**: 82 - 86 (Balanced micro-corrections)
                • **4X Scope**: 74 - 78 (Steady sniper drag)
                • **Sniper / 8X Scope**: 58 - 64 (Precision recoil dampening)
                • **Free Look / 360°**: 75
                
                🎯 **DPI & Touch Response**:
                • Recommended Device DPI: **411 to 440 DPI**
                • Fire Button Size: **48% - 52%** positioned at lower-middle right
                • Swipe Technique: Fast upward 'J-shape' or straight vertical drag for instant headshots.
                
                *(Note: Values are AI calibrations optimized for low touch latency.)*
                """.trimIndent()
            }
            lower.contains("fps") || lower.contains("lag") || lower.contains("frame drop") || lower.contains("boost") || lower.contains("performance") -> {
                """
                ⚡ **GAME TURBO AI — Extreme FPS Optimization Plan**
                
                1. **In-Game Graphics Calibration**:
                   • Graphics Quality: **Smooth / Low** (reduces GPU render latency by ~35%)
                   • Frame Rate: **Ultra / Extreme / 90Hz Max**
                   • Shadow & Anti-Aliasing: **Disabled** (saves ~18% GPU compute)
                   • High Resolution Textures: **Standard**
                
                2. **Device Level Game Turbo Boost**:
                   • Performance Mode: Switch to **Turbo / Extreme Mode** in Game Turbo AI
                   • Memory Cleaner: Clear cached background processes before launching
                   • Do Not Disturb: Enabled (prevents background notification lag spikes)
                   • Touch Sampling: 240Hz/480Hz Touch response boost active
                
                3. **Thermal Management**:
                   • Remove heavy phone cases during prolonged tournament gaming.
                   • Avoid fast charging while gaming to prevent thermal throttling.
                """.trimIndent()
            }
            lower.contains("ping") || lower.contains("network") || lower.contains("wifi") || lower.contains("ms") || lower.contains("loss") -> {
                """
                📶 **GAME TURBO AI — Ultra-Low Latency Diagnostics**
                
                • **Recommended Wi-Fi Band**: **5 GHz / Wi-Fi 6** (reduces jitter from 45ms to <18ms)
                • **Gaming DNS Recommendation**: 
                  - Cloudflare 1.1.1.1 (Fastest packet routing)
                  - Google 8.8.8.8 (High regional stability)
                • **Background Data Lock**: Enabled in Game Turbo Network Tools
                • **Dual-Channel Acceleration**: Combine Wi-Fi + Mobile data for 0% packet loss during ranked matches.
                """.trimIndent()
            }
            lower.contains("strategy") || lower.contains("rush") || lower.contains("tips") || lower.contains("hud") -> {
                """
                ⚔️ **GAME TURBO AI — Tactical Esports Strategy for $game**
                
                • **HUD Layout**: 4-Finger Claw or 3-Finger Hybrid recommended for simultaneous Jump + Shoot + Crouch.
                • **Crosshair Placement**: Always pre-aim at head level (around 5.5ft virtual height) when clearing corners.
                • **Sound Spatialization**: Use stereo headphone audio to pinpoint enemy footsteps up to 25 meters away.
                • **Zone Rotation**: Rotate along the short edge of the safe zone to pinch retreating squads.
                """.trimIndent()
            }
            else -> {
                """
                🤖 **GAME TURBO AI Assistant Active**
                
                I am your dedicated gaming co-pilot for **$game**. 
                Here is what I can do for you right now:
                • **Calibrate Sensitivity**: Type your phone model & weapon preference for custom values.
                • **FPS Booster**: Get custom graphics and thermal tweaks.
                • **Ping Optimizer**: Diagnose latency, jitter, and DNS routing.
                • **Claw & HUD Strategy**: Request customized button placements and rush guides.
                
                Ask me any gaming question or tap one of the quick booster chips!
                """.trimIndent()
            }
        }
    }
}
