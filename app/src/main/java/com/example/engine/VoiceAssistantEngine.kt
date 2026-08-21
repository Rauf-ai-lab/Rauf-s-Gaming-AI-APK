package com.example.engine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class VoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}

class VoiceAssistantEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private var speechRecognizer: SpeechRecognizer? = null

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _speechAmplitude = MutableStateFlow(0f)
    val speechAmplitude: StateFlow<Float> = _speechAmplitude.asStateFlow()

    private val _lastRecognizedText = MutableStateFlow("")
    val lastRecognizedText: StateFlow<String> = _lastRecognizedText.asStateFlow()

    private val _voiceErrorMessage = MutableStateFlow<String?>(null)
    val voiceErrorMessage: StateFlow<String?> = _voiceErrorMessage.asStateFlow()

    // Callbacks
    private var onSpeechResultCallback: ((String) -> Unit)? = null
    private var onSpeechPartialCallback: ((String) -> Unit)? = null
    private var onSpeechErrorCallback: ((String) -> Unit)? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("VoiceAssistantEngine", "Failed to initialize TTS: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("VoiceAssistantEngine", "Language US not supported or missing data")
                }
                engine.setPitch(1.08f) // Crisp gaming tone
                engine.setSpeechRate(1.05f)

                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                        _voiceState.value = VoiceState.SPEAKING
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _speechAmplitude.value = 0f
                        if (_voiceState.value == VoiceState.SPEAKING) {
                            _voiceState.value = VoiceState.IDLE
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _speechAmplitude.value = 0f
                        if (_voiceState.value == VoiceState.SPEAKING) {
                            _voiceState.value = VoiceState.IDLE
                        }
                    }
                })
                isTtsInitialized = true
            }
        }
    }

    fun isSpeechRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun setVoiceState(state: VoiceState) {
        _voiceState.value = state
    }

    fun startListening(
        language: String = "English",
        onPartial: ((String) -> Unit)? = null,
        onError: ((String) -> Unit)? = null,
        onResult: (String) -> Unit
    ) {
        // Always stop TTS when user starts speaking (interruptibility)
        stopSpeaking()

        this.onSpeechResultCallback = onResult
        this.onSpeechPartialCallback = onPartial
        this.onSpeechErrorCallback = onError
        _voiceErrorMessage.value = null

        if (!isSpeechRecognitionAvailable()) {
            val err = "Speech recognition service is not available on this device."
            _voiceErrorMessage.value = err
            _voiceState.value = VoiceState.ERROR
            onError?.invoke(err)
            return
        }

        try {
            // Destroy any previous instance
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

                val localeTag = when (language.lowercase()) {
                    "hindi" -> "hi-IN"
                    "hinglish" -> "en-IN"
                    else -> "en-US"
                }
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeTag)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeTag)
            }

            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _isListening.value = true
                    _voiceState.value = VoiceState.LISTENING
                }

                override fun onBeginningOfSpeech() {
                    _isListening.value = true
                    _voiceState.value = VoiceState.LISTENING
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Normalize RMS dB (typical range -2 to 10) to 0.0f..1.0f for visualizer
                    val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
                    _speechAmplitude.value = normalized
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    _isListening.value = false
                    _speechAmplitude.value = 0f
                }

                override fun onError(error: Int) {
                    _isListening.value = false
                    _speechAmplitude.value = 0f
                    val message = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                        SpeechRecognizer.ERROR_NETWORK -> "Network connection error during voice detection"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Voice detection network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "I couldn't understand that. Please try again."
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Voice recognizer is busy. Retrying..."
                        SpeechRecognizer.ERROR_SERVER -> "Voice recognition server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected. Tap microphone to speak."
                        else -> "Speech recognition error ($error)"
                    }

                    if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                        _voiceErrorMessage.value = message
                        _voiceState.value = VoiceState.ERROR
                    } else {
                        _voiceState.value = VoiceState.IDLE
                    }
                    onSpeechErrorCallback?.invoke(message)
                }

                override fun onResults(results: Bundle?) {
                    _isListening.value = false
                    _speechAmplitude.value = 0f
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull()?.trim() ?: ""

                    if (text.isNotEmpty()) {
                        _lastRecognizedText.value = text
                        _voiceState.value = VoiceState.THINKING
                        onSpeechResultCallback?.invoke(text)
                    } else {
                        _voiceState.value = VoiceState.IDLE
                        onSpeechErrorCallback?.invoke("I couldn't understand that. Please try again.")
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull()?.trim() ?: ""
                    if (text.isNotEmpty()) {
                        _lastRecognizedText.value = text
                        onSpeechPartialCallback?.invoke(text)
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("VoiceAssistantEngine", "Failed to start speech recognizer: ${e.message}", e)
            _isListening.value = false
            _voiceState.value = VoiceState.ERROR
            _voiceErrorMessage.value = e.localizedMessage ?: "Failed to start speech recognizer"
            onError?.invoke(e.localizedMessage ?: "Failed to start speech recognizer")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w("VoiceAssistantEngine", "Error stopping speech recognition: ${e.message}")
        }
        _isListening.value = false
        _speechAmplitude.value = 0f
    }

    fun cancelListening() {
        try {
            speechRecognizer?.cancel()
        } catch (e: Exception) {
            Log.w("VoiceAssistantEngine", "Error cancelling speech recognition: ${e.message}")
        }
        _isListening.value = false
        _speechAmplitude.value = 0f
        if (_voiceState.value == VoiceState.LISTENING) {
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun speak(text: String, language: String = "English", speed: Float = 1.05f) {
        if (!isTtsInitialized || tts == null) return

        // If listening, cancel it
        cancelListening()

        tts?.setSpeechRate(speed.coerceIn(0.7f, 1.6f))

        when (language.lowercase()) {
            "hindi" -> {
                val hindiLocale = Locale.forLanguageTag("hi-IN")
                if (tts?.isLanguageAvailable(hindiLocale) == TextToSpeech.LANG_AVAILABLE) {
                    tts?.language = hindiLocale
                }
            }
            "hinglish" -> {
                val indianEnglish = Locale.forLanguageTag("en-IN")
                if (tts?.isLanguageAvailable(indianEnglish) == TextToSpeech.LANG_AVAILABLE) {
                    tts?.language = indianEnglish
                } else {
                    tts?.language = Locale.US
                }
            }
            else -> {
                tts?.language = Locale.US
            }
        }

        // Clean formatting markers like markdown before speaking
        val cleanSpeech = text
            .replace(Regex("\\*\\*|\\*|#|_|`|\\[.*?\\]|\\(.*?\\)"), "")
            .replace("•", "")
            .replace("🔥", "")
            .replace("🦊", "")
            .replace("⚡", "")
            .replace("🎯", "")
            .replace("🤖", "")
            .trim()

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "GT_AI_${System.currentTimeMillis()}")
        tts?.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, params, "GT_AI_UTTERANCE")
        _isSpeaking.value = true
        _voiceState.value = VoiceState.SPEAKING
    }

    fun stopSpeaking() {
        if (tts != null && isTtsInitialized) {
            tts?.stop()
            _isSpeaking.value = false
            _speechAmplitude.value = 0f
            if (_voiceState.value == VoiceState.SPEAKING) {
                _voiceState.value = VoiceState.IDLE
            }
        }
    }

    fun shutdown() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (e: Exception) {
            Log.w("VoiceAssistantEngine", "Error shutting down: ${e.message}")
        }
    }
}

