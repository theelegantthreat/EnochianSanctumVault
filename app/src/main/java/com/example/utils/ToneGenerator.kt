package com.example.utils

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.sin

class ToneGenerator {
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var toneJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun playTone(frequencyHz: Float, durationMs: Long = 2000L) {
        stopTone()
        isPlaying = true

        toneJob = scope.launch {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val sample = FloatArray(numSamples)

            for (i in 0 until numSamples) {
                val angle = 2.0 * Math.PI * i / (sampleRate / frequencyHz)
                // Envelope fade in/out
                val fade = when {
                    i < 1000 -> i / 1000.0f
                    i > numSamples - 1000 -> (numSamples - i) / 1000.0f
                    else -> 1.0f
                }
                sample[i] = (sin(angle) * 0.3 * fade).toFloat()
            }

            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(numSamples * 4)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                synchronized(this@ToneGenerator) {
                    audioTrack = track
                }
                track.write(sample, 0, numSamples, AudioTrack.WRITE_BLOCKING)
                if (isPlaying) {
                    track.play()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopTone() {
        isPlaying = false
        toneJob?.cancel()
        synchronized(this) {
            try {
                audioTrack?.let {
                    if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                        it.stop()
                    }
                    it.release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            audioTrack = null
        }
    }
}

