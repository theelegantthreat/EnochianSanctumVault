package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.data.reference.EnochianCall
import java.util.Locale
import kotlin.concurrent.thread

class EnochianAudioPlayer(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _isPaused = mutableStateOf(false)
    val isPaused: State<Boolean> = _isPaused

    private val _activeCallId = mutableStateOf<Int?>(null)
    val activeCallId: State<Int?> = _activeCallId

    private val _speechProgress = mutableFloatStateOf(0f)
    val speechProgress: State<Float> = _speechProgress

    private var currentCall: EnochianCall? = null
    private var toneThread: Thread? = null
    private var isToneRunning = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let {
                val result = it.setLanguage(Locale.US)
                it.setSpeechRate(0.75f) // Slow solemn pacing for ritual chants
                it.setPitch(0.85f)      // Deep resonant pitch

                it.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isPlaying.value = true
                        _isPaused.value = false
                        _speechProgress.floatValue = 0.1f
                    }

                    override fun onDone(utteranceId: String?) {
                        _isPlaying.value = false
                        _isPaused.value = false
                        _speechProgress.floatValue = 1.0f
                        stopDroneTone()
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isPlaying.value = false
                        _isPaused.value = false
                        stopDroneTone()
                    }
                })
                isTtsInitialized = true
            }
        }
    }

    fun playCall(call: EnochianCall) {
        currentCall = call
        _activeCallId.value = call.id
        _speechProgress.floatValue = 0.0f

        val textToSpeak = "${call.title}. ${call.subtitle}. Enochian Invocation: ${call.eNochianPhonetic}. English: ${call.englishTranslation}"

        if (isTtsInitialized) {
            tts?.stop()
            val params = android.os.Bundle()
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "ENOCHIAN_UTTERANCE_${call.id}")
            _isPlaying.value = true
            _isPaused.value = false
            startDroneTone(call.frequencyHz)
        }
    }

    fun pauseCall() {
        if (_isPlaying.value) {
            tts?.stop()
            stopDroneTone()
            _isPlaying.value = false
            _isPaused.value = true
        }
    }

    fun replayCall() {
        currentCall?.let {
            playCall(it)
        }
    }

    fun stopCall() {
        tts?.stop()
        stopDroneTone()
        _isPlaying.value = false
        _isPaused.value = false
        _activeCallId.value = null
        _speechProgress.floatValue = 0f
    }

    private fun startDroneTone(frequencyHz: Float) {
        stopDroneTone()
        isToneRunning = true
        toneThread = thread(start = true) {
            val sampleRate = 44100
            val numSamples = sampleRate / 2
            val sample = DoubleArray(numSamples)
            val generatedSnd = ByteArray(2 * numSamples)

            for (i in 0 until numSamples) {
                sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / frequencyHz))
            }

            var idx = 0
            for (dVal in sample) {
                val shortVal = (dVal * 12000).toInt().toShort() // Gentle background drone
                generatedSnd[idx++] = (shortVal.toInt() and 0x00ff).toByte()
                generatedSnd[idx++] = (shortVal.toInt() and 0xff00 shr 8).toByte()
            }

            try {
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(generatedSnd.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()


                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.setLoopPoints(0, numSamples, -1)
                audioTrack.play()

                while (isToneRunning && _isPlaying.value) {
                    Thread.sleep(100)
                }

                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopDroneTone() {
        isToneRunning = false
        toneThread?.interrupt()
        toneThread = null
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        stopDroneTone()
    }
}
