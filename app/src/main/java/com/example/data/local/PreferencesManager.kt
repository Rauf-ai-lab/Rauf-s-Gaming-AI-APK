package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("game_turbo_prefs", Context.MODE_PRIVATE)

    var hasCompletedOnboarding: Boolean
        get() = prefs.getBoolean("has_completed_onboarding", false)
        set(value) = prefs.edit().putBoolean("has_completed_onboarding", value).apply()

    var selectedAccentColor: String
        get() = prefs.getString("accent_color", "CYAN") ?: "CYAN"
        set(value) = prefs.edit().putString("accent_color", value).apply()

    var activePerformanceMode: String
        get() = prefs.getString("perf_mode", "Performance") ?: "Performance"
        set(value) = prefs.edit().putString("perf_mode", value).apply()

    var customApiKey: String
        get() = prefs.getString("custom_api_key", "") ?: ""
        set(value) = prefs.edit().putString("custom_api_key", value).apply()

    var voiceLanguage: String
        get() = prefs.getString("voice_lang", "English") ?: "English" // English, Hindi, Hinglish, Auto Detect
        set(value) = prefs.edit().putString("voice_lang", value).apply()

    var speechSpeed: Float
        get() = prefs.getFloat("speech_speed", 1.05f)
        set(value) = prefs.edit().putFloat("speech_speed", value).apply()

    var autoSpeakResponse: Boolean
        get() = prefs.getBoolean("auto_speak_response", true)
        set(value) = prefs.edit().putBoolean("auto_speak_response", value).apply()

    var pushToTalkEnabled: Boolean
        get() = prefs.getBoolean("push_to_talk_enabled", false)
        set(value) = prefs.edit().putBoolean("push_to_talk_enabled", value).apply()

    var handsFreeModeEnabled: Boolean
        get() = prefs.getBoolean("hands_free_mode_enabled", false)
        set(value) = prefs.edit().putBoolean("hands_free_mode_enabled", value).apply()

    var voiceHistoryEnabled: Boolean
        get() = prefs.getBoolean("voice_history_enabled", true)
        set(value) = prefs.edit().putBoolean("voice_history_enabled", value).apply()

    var isFloatingOverlayEnabled: Boolean
        get() = prefs.getBoolean("floating_overlay_enabled", true)
        set(value) = prefs.edit().putBoolean("floating_overlay_enabled", value).apply()

    var dndGamingEnabled: Boolean
        get() = prefs.getBoolean("dnd_gaming_enabled", true)
        set(value) = prefs.edit().putBoolean("dnd_gaming_enabled", value).apply()

    var touchBoostEnabled: Boolean
        get() = prefs.getBoolean("touch_boost_enabled", true)
        set(value) = prefs.edit().putBoolean("touch_boost_enabled", value).apply()

    var activeGameTitle: String
        get() = prefs.getString("active_game_title", "Free Fire MAX") ?: "Free Fire MAX"
        set(value) = prefs.edit().putString("active_game_title", value).apply()

    var userGamerTag: String
        get() = prefs.getString("user_gamer_tag", "VortexPlayer") ?: "VortexPlayer"
        set(value) = prefs.edit().putString("user_gamer_tag", value).apply()

    var playstyle: String
        get() = prefs.getString("user_playstyle", "Aggressive Rusher") ?: "Aggressive Rusher"
        set(value) = prefs.edit().putString("user_playstyle", value).apply()

    var animeEffectsEnabled: Boolean
        get() = prefs.getBoolean("anime_effects_enabled", true)
        set(value) = prefs.edit().putBoolean("anime_effects_enabled", value).apply()

    var backgroundAnimationLevel: String
        get() = prefs.getString("bg_animation_level", "FULL") ?: "FULL" // FULL, LOW, OFF
        set(value) = prefs.edit().putString("bg_animation_level", value).apply()

    var animeAiChakraVoice: Boolean
        get() = prefs.getBoolean("anime_ai_chakra_voice", true)
        set(value) = prefs.edit().putBoolean("anime_ai_chakra_voice", value).apply()

    var gameSortOrder: String
        get() = prefs.getString("game_sort_order", "RECENT") ?: "RECENT"
        set(value) = prefs.edit().putString("game_sort_order", value).apply()

    fun clearAllData() {
        prefs.edit().clear().apply()
    }
}
