package com.example.engine

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceAssistantEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechAmplitude = MutableStateFlow(0f)
    val speechAmplitude: StateFlow<Float> = _speechAmplitude.asStateFlow()

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
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _speechAmplitude.value = 0f
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _speechAmplitude.value = 0f
                    }
                })
                isInitialized = true
            }
        }
    }

    fun speak(text: String, language: String = "English", speed: Float = 1.05f) {
        if (!isInitialized || tts == null) return

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
            .trim()

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "GT_AI_${System.currentTimeMillis()}")
        tts?.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, params, "GT_AI_UTTERANCE")
        _isSpeaking.value = true
    }

    fun stopSpeaking() {
        if (tts != null && isInitialized) {
            tts?.stop()
            _isSpeaking.value = false
            _speechAmplitude.value = 0f
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
