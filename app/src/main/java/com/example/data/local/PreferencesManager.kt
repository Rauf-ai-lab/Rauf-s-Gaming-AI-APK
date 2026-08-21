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
        get() = prefs.getString("voice_lang", "English") ?: "English" // English, Hindi, Hinglish
        set(value) = prefs.edit().putString("voice_lang", value).apply()

    var speechSpeed: Float
        get() = prefs.getFloat("speech_speed", 1.05f)
        set(value) = prefs.edit().putFloat("speech_speed", value).apply()

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

    fun clearAllData() {
        prefs.edit().clear().apply()
    }
}
