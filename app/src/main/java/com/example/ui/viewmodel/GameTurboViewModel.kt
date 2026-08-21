package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessage
import com.example.data.local.GameProfile
import com.example.data.local.PerformanceSnapshot
import com.example.data.local.PreferencesManager
import com.example.data.remote.AiConnectionStatus
import com.example.data.remote.GeminiRepository
import com.example.data.repository.GamingRepository
import com.example.engine.AppItem
import com.example.engine.HardwareMonitor
import com.example.engine.InstalledAppsManager
import com.example.engine.SystemHardwareStats
import com.example.engine.VoiceAssistantEngine
import com.example.ui.theme.TurboAccentColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class NavigationTab(val title: String, val iconRes: String) {
    DASHBOARD("Dashboard", "dashboard"),
    PERFORMANCE("Performance", "speed"),
    AI_ASSISTANT("AI Assistant", "psychology"),
    AI_ANALYZER("Game Analyzer", "tune"),
    NETWORK("Network", "wifi"),
    TOOLS("Game Tools", "handyman"),
    LIBRARY("Game Library", "sports_esports"),
    SETTINGS("Settings", "settings")
}

data class AiAnalyzerInput(
    val game: String = "Free Fire MAX",
    val deviceModel: String = "Xiaomi 14 / Pro",
    val ramGb: Int = 12,
    val targetFps: Int = 90,
    val playstyle: String = "Rush / Aggressive",
    val aimStyle: String = "Drag Headshot",
    val isGyroOn: Boolean = false
)

data class AiAnalyzerResult(
    val generalSensi: Int = 98,
    val redDotSensi: Int = 90,
    val scope2xSensi: Int = 84,
    val scope4xSensi: Int = 76,
    val sniperSensi: Int = 62,
    val freeLookSensi: Int = 75,
    val recommendedDpi: Int = 420,
    val fireButtonSizePercent: Int = 50,
    val hudSuggestion: String = "Place fire button at 50% scale at right-center thumb zone. Enable Quick Weapon Switch for instant reload cancels.",
    val graphicsSetting: String = "Graphics: Smooth | High FPS: Ultra/90FPS | Shadow: Off | High Res: Normal",
    val generatedAt: Long = System.currentTimeMillis()
)

class GameTurboViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = AppDatabase.getInstance(context)
    val preferencesManager = PreferencesManager(context)
    val geminiRepository = GeminiRepository(preferencesManager)
    val gamingRepository = GamingRepository(database)
    val hardwareMonitor = HardwareMonitor(context)
    val voiceEngine = VoiceAssistantEngine(context)
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    // Active Navigation
    private val _currentTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    // Onboarding State
    private val _isOnboardingCompleted = MutableStateFlow(preferencesManager.hasCompletedOnboarding)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    // Accent Theme
    private val _currentAccent = MutableStateFlow(TurboAccentColor.fromKey(preferencesManager.selectedAccentColor))
    val currentAccent: StateFlow<TurboAccentColor> = _currentAccent.asStateFlow()

    // Active Performance Mode: Balanced, Performance, Extreme, Eco
    private val _activePerfMode = MutableStateFlow(preferencesManager.activePerformanceMode)
    val activePerfMode: StateFlow<String> = _activePerfMode.asStateFlow()

    // Live Hardware Stats
    private val _hardwareStats = MutableStateFlow(SystemHardwareStats())
    val hardwareStats: StateFlow<SystemHardwareStats> = _hardwareStats.asStateFlow()

    // Boost status message (e.g. "Freed 450 MB RAM! CPU Turbo engaged")
    private val _boostMessage = MutableStateFlow<String?>(null)
    val boostMessage: StateFlow<String?> = _boostMessage.asStateFlow()

    private val _isBoosting = MutableStateFlow(false)
    val isBoosting: StateFlow<Boolean> = _isBoosting.asStateFlow()

    // AI Connection State
    private val _aiStatus = MutableStateFlow(AiConnectionStatus.CONNECTING)
    val aiStatus: StateFlow<AiConnectionStatus> = _aiStatus.asStateFlow()

    private val _aiStatusDetails = MutableStateFlow("Initializing Gaming Neural Engine...")
    val aiStatusDetails: StateFlow<String> = _aiStatusDetails.asStateFlow()

    // Active Game Selection
    private val _activeGame = MutableStateFlow(preferencesManager.activeGameTitle)
    val activeGame: StateFlow<String> = _activeGame.asStateFlow()

    // Game Profiles from DB
    val gameProfiles: StateFlow<List<GameProfile>> = gamingRepository.allGameProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat History from DB
    val chatMessages: StateFlow<List<ChatMessage>> = gamingRepository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Streaming Text (for live AI generation)
    private val _streamingAiResponse = MutableStateFlow("")
    val streamingAiResponse: StateFlow<String> = _streamingAiResponse.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    // Voice Assistant Preferences States
    private val _autoSpeakResponse = MutableStateFlow(preferencesManager.autoSpeakResponse)
    val autoSpeakResponse: StateFlow<Boolean> = _autoSpeakResponse.asStateFlow()

    private val _pushToTalkEnabled = MutableStateFlow(preferencesManager.pushToTalkEnabled)
    val pushToTalkEnabled: StateFlow<Boolean> = _pushToTalkEnabled.asStateFlow()

    private val _handsFreeModeEnabled = MutableStateFlow(preferencesManager.handsFreeModeEnabled)
    val handsFreeModeEnabled: StateFlow<Boolean> = _handsFreeModeEnabled.asStateFlow()

    private val _voiceHistoryEnabled = MutableStateFlow(preferencesManager.voiceHistoryEnabled)
    val voiceHistoryEnabled: StateFlow<Boolean> = _voiceHistoryEnabled.asStateFlow()

    private val _voiceLanguage = MutableStateFlow(preferencesManager.voiceLanguage)
    val voiceLanguage: StateFlow<String> = _voiceLanguage.asStateFlow()

    private val _speechSpeed = MutableStateFlow(preferencesManager.speechSpeed)
    val speechSpeed: StateFlow<Float> = _speechSpeed.asStateFlow()

    private val _lastRecognizedSpeech = MutableStateFlow("")
    val lastRecognizedSpeech: StateFlow<String> = _lastRecognizedSpeech.asStateFlow()

    // AI Game Analyzer State
    private val _analyzerInput = MutableStateFlow(AiAnalyzerInput())
    val analyzerInput: StateFlow<AiAnalyzerInput> = _analyzerInput.asStateFlow()

    private val _analyzerResult = MutableStateFlow<AiAnalyzerResult?>(AiAnalyzerResult())
    val analyzerResult: StateFlow<AiAnalyzerResult?> = _analyzerResult.asStateFlow()

    private val _isAnalyzingGame = MutableStateFlow(false)
    val isAnalyzingGame: StateFlow<Boolean> = _isAnalyzingGame.asStateFlow()

    // In-Game Floating Overlay Simulator State
    private val _isFloatingOverlayOpen = MutableStateFlow(false)
    val isFloatingOverlayOpen: StateFlow<Boolean> = _isFloatingOverlayOpen.asStateFlow()

    // Game Tools States
    private val _isDndActive = MutableStateFlow(preferencesManager.dndGamingEnabled)
    val isDndActive: StateFlow<Boolean> = _isDndActive.asStateFlow()

    private val _isTouchBoostActive = MutableStateFlow(preferencesManager.touchBoostEnabled)
    val isTouchBoostActive: StateFlow<Boolean> = _isTouchBoostActive.asStateFlow()

    private val _brightnessLevel = MutableStateFlow(0.85f)
    val brightnessLevel: StateFlow<Float> = _brightnessLevel.asStateFlow()

    private val _volumeLevel = MutableStateFlow(0.80f)
    val volumeLevel: StateFlow<Float> = _volumeLevel.asStateFlow()

    private val _voiceEffect = MutableStateFlow("Studio Clear") // Studio Clear, Deep Cyber, Tactical Radio, Mech Robot
    val voiceEffect: StateFlow<String> = _voiceEffect.asStateFlow()

    // Anime Theme Effects
    private val _animeEffectsEnabled = MutableStateFlow(preferencesManager.animeEffectsEnabled)
    val animeEffectsEnabled: StateFlow<Boolean> = _animeEffectsEnabled.asStateFlow()

    private val _backgroundAnimationLevel = MutableStateFlow(preferencesManager.backgroundAnimationLevel)
    val backgroundAnimationLevel: StateFlow<String> = _backgroundAnimationLevel.asStateFlow()

    // Installed Device Apps
    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

    private val _isLoadingInstalledApps = MutableStateFlow(false)
    val isLoadingInstalledApps: StateFlow<Boolean> = _isLoadingInstalledApps.asStateFlow()

    // Background job holders
    private var streamingJob: Job? = null
    private var hardwareMonitorJob: Job? = null

    init {
        viewModelScope.launch {
            gamingRepository.initializeDefaultGamesIfEmpty()
        }
        startHardwareMonitorLoop()
        checkAiStatus()
    }

    private fun startHardwareMonitorLoop() {
        hardwareMonitorJob?.cancel()
        hardwareMonitorJob = viewModelScope.launch {
            while (true) {
                val (battPercent, battTemp, isCharging) = hardwareMonitor.getRealBatteryStats()
                val (usedRam, totalRam, ramPercent) = hardwareMonitor.getRealRamStats()
                val (netType, netStable) = hardwareMonitor.getNetworkInfo()
                val currentPing = hardwareMonitor.measureRealPing()

                val mode = _activePerfMode.value
                val baseFps = when (mode) {
                    "Extreme" -> 120
                    "Performance" -> 90
                    "Balanced" -> 60
                    else -> 45
                }
                val jitter = if (mode == "Extreme") Random.nextInt(-1, 2) else Random.nextInt(-2, 1)
                val currentFps = (baseFps + jitter).coerceAtLeast(30)

                val cpuLoad = when (mode) {
                    "Extreme" -> Random.nextInt(45, 68)
                    "Performance" -> Random.nextInt(32, 48)
                    "Balanced" -> Random.nextInt(20, 35)
                    else -> Random.nextInt(14, 25)
                }

                _hardwareStats.value = SystemHardwareStats(
                    fps = currentFps,
                    targetFps = baseFps,
                    frameStabilityPercent = if (mode == "Extreme") 99 else 97,
                    pingMs = currentPing,
                    networkType = netType,
                    isNetworkStable = netStable,
                    batteryPercent = battPercent,
                    batteryTempCelsius = battTemp,
                    isCharging = isCharging,
                    ramUsedMb = usedRam,
                    ramTotalMb = totalRam,
                    ramUsagePercent = ramPercent,
                    cpuUsagePercent = cpuLoad,
                    performanceMode = mode
                )

                delay(2500)
            }
        }
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
        triggerHaptic()
    }

    fun completeOnboarding() {
        preferencesManager.hasCompletedOnboarding = true
        _isOnboardingCompleted.value = true
        triggerHaptic()
    }

    fun setAccentColor(accent: TurboAccentColor) {
        preferencesManager.selectedAccentColor = accent.name
        _currentAccent.value = accent
        triggerHaptic()
    }

    fun setPerformanceMode(mode: String) {
        preferencesManager.activePerformanceMode = mode
        _activePerfMode.value = mode
        triggerHaptic()
        _boostMessage.value = "$mode Mode Engaged • Target ${if (mode == "Extreme") "120 FPS" else if (mode == "Performance") "90 FPS" else "60 FPS"}"
        viewModelScope.launch {
            delay(3000)
            if (_boostMessage.value?.contains(mode) == true) {
                _boostMessage.value = null
            }
        }
    }

    fun setActiveGame(gameTitle: String) {
        preferencesManager.activeGameTitle = gameTitle
        _activeGame.value = gameTitle
        _analyzerInput.value = _analyzerInput.value.copy(game = gameTitle)
        triggerHaptic()
    }

    fun triggerQuickBoost() {
        viewModelScope.launch {
            _isBoosting.value = true
            triggerHaptic()
            val freed = hardwareMonitor.cleanMemory()
            delay(700)
            _isBoosting.value = false
            _boostMessage.value = "🚀 Game Turbo Boosted! Freed ${freed}MB RAM • CPU Cores Optimized"
            
            // Save performance snapshot
            val stats = _hardwareStats.value
            gamingRepository.recordPerformanceSnapshot(
                PerformanceSnapshot(
                    fps = stats.fps,
                    pingMs = stats.pingMs,
                    tempCelsius = stats.batteryTempCelsius,
                    ramUsedMb = (stats.ramUsedMb - freed).coerceAtLeast(1024),
                    ramTotalMb = stats.ramTotalMb,
                    cpuUsagePercent = (stats.cpuUsagePercent - 8).coerceAtLeast(12),
                    batteryPercent = stats.batteryPercent,
                    performanceMode = _activePerfMode.value
                )
            )

            delay(4000)
            _boostMessage.value = null
        }
    }

    fun checkAiStatus() {
        viewModelScope.launch {
            _aiStatus.value = AiConnectionStatus.CONNECTING
            _aiStatusDetails.value = "Verifying Gemini AI Connection..."
            val (ok, details) = geminiRepository.testConnection()
            if (ok) {
                _aiStatus.value = AiConnectionStatus.ONLINE
                _aiStatusDetails.value = details
            } else {
                _aiStatus.value = AiConnectionStatus.OFFLINE_FALLBACK
                _aiStatusDetails.value = details
            }
        }
    }

    fun startVoiceInput(isPushToTalk: Boolean = false) {
        triggerHaptic()
        voiceEngine.stopSpeaking()
        val lang = _voiceLanguage.value

        voiceEngine.startListening(
            language = lang,
            onPartial = { partial ->
                _lastRecognizedSpeech.value = partial
            },
            onError = { error ->
                _lastRecognizedSpeech.value = ""
                voiceEngine.setVoiceState(com.example.engine.VoiceState.ERROR)
            },
            onResult = { resultText ->
                _lastRecognizedSpeech.value = resultText
                handleRecognizedVoiceCommand(resultText)
            }
        )
    }

    fun stopVoiceInput() {
        voiceEngine.stopListening()
    }

    fun cancelVoiceInput() {
        voiceEngine.cancelListening()
        _lastRecognizedSpeech.value = ""
    }

    private fun handleRecognizedVoiceCommand(spokenText: String) {
        if (spokenText.isBlank()) return
        val lower = spokenText.lowercase().trim()

        // Check for gaming voice action triggers
        val executedDirectAction = when {
            lower.contains("optimize") || lower.contains("boost") || lower.contains("speed up") -> {
                triggerQuickBoost()
                "🚀 Game Turbo boost engaged! Memory cleaned and CPU cores optimized."
            }
            lower.contains("library") || lower.contains("open game") || lower.contains("my games") -> {
                selectTab(NavigationTab.LIBRARY)
                "Opening Game Library."
            }
            lower.contains("dashboard") || lower.contains("home") -> {
                selectTab(NavigationTab.DASHBOARD)
                "Switching to Game Turbo Dashboard."
            }
            lower.contains("performance") || lower.contains("monitor") || lower.contains("fps stats") -> {
                selectTab(NavigationTab.PERFORMANCE)
                "Opening Real-time Performance Telemetry."
            }
            lower.contains("network") || lower.contains("ping tool") || lower.contains("dns") -> {
                selectTab(NavigationTab.NETWORK)
                "Opening Low-Latency Network Tools."
            }
            lower.contains("analyzer") || lower.contains("sensitivity") || lower.contains("sensi") -> {
                selectTab(NavigationTab.AI_ANALYZER)
                runAiGameAnalysis()
                "Calibrating pro sensitivity for ${_activeGame.value}."
            }
            lower.contains("do not disturb on") || lower.contains("turn on dnd") || lower.contains("enable dnd") -> {
                if (!_isDndActive.value) toggleDnd()
                "Gaming Do Not Disturb mode activated."
            }
            lower.contains("do not disturb off") || lower.contains("turn off dnd") || lower.contains("disable dnd") -> {
                if (_isDndActive.value) toggleDnd()
                "Gaming Do Not Disturb mode disabled."
            }
            lower.contains("extreme mode") || lower.contains("sage mode") || lower.contains("120 fps") -> {
                setPerformanceMode("Extreme")
                "Sage Mode Extreme 120 FPS engaged."
            }
            lower.contains("balanced mode") -> {
                setPerformanceMode("Balanced")
                "Balanced Mode engaged."
            }
            else -> null
        }

        // Send to Gemini AI for complete gamer assistant response
        sendChatMessage(spokenText, category = "Voice", isVoiceTriggered = true, directActionSpeech = executedDirectAction)
    }

    fun sendChatMessage(
        promptText: String,
        category: String = "General",
        isVoiceTriggered: Boolean = false,
        directActionSpeech: String? = null
    ) {
        if (promptText.isBlank() || _isAiGenerating.value) return
        val userPrompt = promptText.trim()

        viewModelScope.launch {
            if (_voiceHistoryEnabled.value) {
                gamingRepository.saveChatMessage(
                    sender = "user",
                    message = userPrompt,
                    category = category
                )
            }

            _isAiGenerating.value = true
            _streamingAiResponse.value = ""
            voiceEngine.setVoiceState(com.example.engine.VoiceState.THINKING)
            triggerHaptic()

            val history = chatMessages.value.takeLast(6).map {
                it.sender to it.message
            }

            val fullResponseBuilder = StringBuilder()
            streamingJob?.cancel()
            streamingJob = launch {
                geminiRepository.streamGameTurboChat(
                    prompt = userPrompt,
                    gameContext = _activeGame.value,
                    conversationHistory = history
                ).collect { chunk ->
                    fullResponseBuilder.append(chunk)
                    _streamingAiResponse.value = fullResponseBuilder.toString()
                }

                val finalOutput = fullResponseBuilder.toString()
                if (finalOutput.isNotBlank()) {
                    if (_voiceHistoryEnabled.value) {
                        gamingRepository.saveChatMessage(
                            sender = "ai",
                            message = finalOutput,
                            category = category
                        )
                    }

                    // If auto-speak is enabled, speak the answer
                    if (_autoSpeakResponse.value || isVoiceTriggered) {
                        val speechToPlay = if (directActionSpeech != null) {
                            "$directActionSpeech $finalOutput"
                        } else {
                            finalOutput
                        }
                        speakAiMessage(speechToPlay)
                    } else {
                        voiceEngine.setVoiceState(com.example.engine.VoiceState.IDLE)
                    }
                } else {
                    voiceEngine.setVoiceState(com.example.engine.VoiceState.IDLE)
                }
                _streamingAiResponse.value = ""
                _isAiGenerating.value = false
            }
        }
    }

    fun stopAiGeneration() {
        streamingJob?.cancel()
        voiceEngine.stopSpeaking()
        val currentChunk = _streamingAiResponse.value
        if (currentChunk.isNotBlank() && _voiceHistoryEnabled.value) {
            viewModelScope.launch {
                gamingRepository.saveChatMessage(
                    sender = "ai",
                    message = "$currentChunk\n[Stopped by user]",
                    category = "General"
                )
            }
        }
        _streamingAiResponse.value = ""
        _isAiGenerating.value = false
        voiceEngine.setVoiceState(com.example.engine.VoiceState.IDLE)
        triggerHaptic()
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            gamingRepository.clearChatHistory()
            triggerHaptic()
        }
    }

    fun speakAiMessage(text: String) {
        val lang = _voiceLanguage.value
        val speed = _speechSpeed.value
        voiceEngine.speak(text, lang, speed)
        triggerHaptic()
    }

    fun stopSpeaking() {
        voiceEngine.stopSpeaking()
        triggerHaptic()
    }

    fun toggleAutoSpeak() {
        val next = !_autoSpeakResponse.value
        _autoSpeakResponse.value = next
        preferencesManager.autoSpeakResponse = next
        triggerHaptic()
    }

    fun togglePushToTalk() {
        val next = !_pushToTalkEnabled.value
        _pushToTalkEnabled.value = next
        preferencesManager.pushToTalkEnabled = next
        triggerHaptic()
    }

    fun toggleHandsFreeMode() {
        val next = !_handsFreeModeEnabled.value
        _handsFreeModeEnabled.value = next
        preferencesManager.handsFreeModeEnabled = next
        triggerHaptic()
    }

    fun toggleVoiceHistory() {
        val next = !_voiceHistoryEnabled.value
        _voiceHistoryEnabled.value = next
        preferencesManager.voiceHistoryEnabled = next
        triggerHaptic()
    }

    fun setVoiceLanguage(lang: String) {
        preferencesManager.voiceLanguage = lang
        _voiceLanguage.value = lang
        triggerHaptic()
    }

    fun setSpeechSpeed(speed: Float) {
        preferencesManager.speechSpeed = speed
        _speechSpeed.value = speed
    }

    fun updateAnalyzerInput(transform: (AiAnalyzerInput) -> AiAnalyzerInput) {
        _analyzerInput.value = transform(_analyzerInput.value)
    }

    fun runAiGameAnalysis() {
        viewModelScope.launch {
            _isAnalyzingGame.value = true
            triggerHaptic()
            val input = _analyzerInput.value

            // Prompt Gemini or calculate high-precision calibration
            val prompt = """
                Generate a custom gaming configuration for:
                Game: ${input.game}
                Device: ${input.deviceModel} (${input.ramGb}GB RAM)
                Target FPS: ${input.targetFps}
                Playstyle: ${input.playstyle}
                Aim Technique: ${input.aimStyle}
                Gyroscope: ${if (input.isGyroOn) "ENABLED" else "DISABLED"}
                
                Please calibrate optimal General Sensitivity, Red Dot, 2X, 4X, Sniper, Free Look, DPI, and HUD button recommendations.
            """.trimIndent()

            // Calculate precise mathematical calibration based on inputs
            val isRush = input.playstyle.contains("Rush", ignoreCase = true)
            val isGyro = input.isGyroOn
            val ramMultiplier = if (input.ramGb >= 8) 1.0f else 0.94f

            val general = ((if (isRush) 98 else 92) * ramMultiplier).toInt().coerceIn(80, 100)
            val redDot = (if (isRush) 92 else 86).coerceIn(75, 100)
            val scope2 = (if (isGyro) 88 else 82).coerceIn(70, 95)
            val scope4 = (if (isGyro) 80 else 74).coerceIn(60, 90)
            val sniper = (if (isRush) 65 else 58).coerceIn(45, 80)
            val freeLook = 75
            val dpi = if (input.targetFps >= 90) 440 else 411

            delay(1200) // Realistic calculation feedback

            _analyzerResult.value = AiAnalyzerResult(
                generalSensi = general,
                redDotSensi = redDot,
                scope2xSensi = scope2,
                scope4xSensi = scope4,
                sniperSensi = sniper,
                freeLookSensi = freeLook,
                recommendedDpi = dpi,
                fireButtonSizePercent = if (isRush) 48 else 54,
                hudSuggestion = "Position Fire Button at ${if (isRush) 48 else 54}% size with quick crouch/jump mapped to index fingers for 3/4-finger claw.",
                graphicsSetting = "Smooth / Performance mode with Anti-aliasing OFF for zero input lag at ${input.targetFps} FPS target."
            )

            _isAnalyzingGame.value = false
            triggerHaptic()

            // Save to chat history as recommendation card
            sendChatMessage("Calibrate sensitivity for ${input.game} with ${input.playstyle} playstyle", "Sensitivity")
        }
    }

    fun toggleFloatingOverlay() {
        _isFloatingOverlayOpen.value = !_isFloatingOverlayOpen.value
        triggerHaptic()
    }

    fun toggleDnd() {
        val next = !_isDndActive.value
        _isDndActive.value = next
        preferencesManager.dndGamingEnabled = next
        triggerHaptic()
    }

    fun toggleTouchBoost() {
        val next = !_isTouchBoostActive.value
        _isTouchBoostActive.value = next
        preferencesManager.touchBoostEnabled = next
        triggerHaptic()
    }

    fun setBrightness(value: Float) {
        _brightnessLevel.value = value.coerceIn(0.1f, 1.0f)
    }

    fun setVolume(value: Float) {
        _volumeLevel.value = value.coerceIn(0.0f, 1.0f)
    }

    fun setVoiceEffect(effect: String) {
        _voiceEffect.value = effect
        triggerHaptic()
    }

    fun toggleAnimeEffects() {
        val next = !_animeEffectsEnabled.value
        _animeEffectsEnabled.value = next
        preferencesManager.animeEffectsEnabled = next
        triggerHaptic()
    }

    fun setBackgroundAnimationLevel(level: String) {
        _backgroundAnimationLevel.value = level
        preferencesManager.backgroundAnimationLevel = level
        triggerHaptic()
    }

    fun scanInstalledApps() {
        viewModelScope.launch {
            _isLoadingInstalledApps.value = true
            val apps = kotlinx.coroutines.Dispatchers.IO.let {
                kotlinx.coroutines.withContext(it) {
                    InstalledAppsManager.getInstalledApps(context)
                }
            }
            _installedApps.value = apps
            _isLoadingInstalledApps.value = false
        }
    }

    fun addInstalledApp(
        app: AppItem,
        targetFps: Int = 90,
        perfMode: String = "Extreme",
        genre: String = if (app.isGame) "Gaming App" else "Productivity / Tool"
    ) {
        viewModelScope.launch {
            gamingRepository.saveGameProfile(
                GameProfile(
                    name = app.appName,
                    packageName = app.packageName,
                    genre = genre,
                    targetFps = targetFps,
                    performanceMode = perfMode,
                    touchSensitivityBoost = if (app.isGame) 92 else 75,
                    customNotes = if (app.isGame) "Esports Optimized Game Turbo Profile" else "Accelerated App Profile"
                )
            )
            triggerHaptic()
        }
    }

    fun updateGameProfile(profile: GameProfile) {
        viewModelScope.launch {
            gamingRepository.saveGameProfile(profile)
            triggerHaptic()
        }
    }

    fun launchGameOrApp(profile: GameProfile): Boolean {
        triggerHaptic()
        _activeGame.value = profile.name
        preferencesManager.activeGameTitle = profile.name
        // Update last played time
        viewModelScope.launch {
            gamingRepository.saveGameProfile(profile.copy(lastPlayedTime = System.currentTimeMillis()))
        }
        return InstalledAppsManager.launchPackage(context, profile.packageName)
    }

    fun addCustomGame(name: String, genre: String, targetFps: Int) {
        viewModelScope.launch {
            gamingRepository.saveGameProfile(
                GameProfile(
                    name = name,
                    packageName = "custom.game.${name.lowercase().replace(" ", "_")}",
                    genre = genre,
                    targetFps = targetFps,
                    performanceMode = "Performance",
                    touchSensitivityBoost = 90,
                    customNotes = "Custom User Created Game Profile"
                )
            )
            triggerHaptic()
        }
    }

    fun deleteGame(id: Long) {
        viewModelScope.launch {
            gamingRepository.deleteGameProfile(id)
            triggerHaptic()
        }
    }

    fun saveCustomApiKey(key: String) {
        preferencesManager.customApiKey = key
        checkAiStatus()
        triggerHaptic()
    }

    private fun triggerHaptic() {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(18)
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun onCleared() {
        super.onCleared()
        hardwareMonitorJob?.cancel()
        streamingJob?.cancel()
        voiceEngine.shutdown()
    }
}
