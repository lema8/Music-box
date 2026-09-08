package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.data.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

data class AudioPlaybackState(
    val currentTrack: TrackEntity? = null,
    val isPlaying: Boolean = false,
    val currentPositionSec: Long = 0,
    val durationSec: Long = 240,
    val volume: Float = 0.85f,
    val eqPreset: String = "Studio Warmth",
    val outputRoute: String = "USB DAC / ASIO",
    val isShuffle: Boolean = false,
    val repeatMode: String = "all", // "all", "one", "off"
    val isSynthesizing: Boolean = false
)

class AudioEngine(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var synthJob: Job? = null
    private var progressJob: Job? = null
    private var synthAudioTrack: AudioTrack? = null

    var onTrackCompletion: (() -> Unit)? = null

    init {
        startProgressTracker()
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val state = _playbackState.value
                if (state.isPlaying) {
                    if (state.isSynthesizing) {
                        val nextPos = state.currentPositionSec + 1
                        if (nextPos >= state.durationSec) {
                            if (state.repeatMode == "one") {
                                _playbackState.value = state.copy(currentPositionSec = 0)
                            } else {
                                onTrackCompletion?.invoke()
                            }
                        } else {
                            _playbackState.value = state.copy(currentPositionSec = nextPos)
                        }
                    } else {
                        mediaPlayer?.let { player ->
                            try {
                                if (player.isPlaying) {
                                    val currentMs = player.currentPosition
                                    val durationMs = player.duration
                                    val currentSec = (currentMs / 1000).toLong()
                                    val durationSec = if (durationMs > 0) (durationMs / 1000).toLong() else state.durationSec
                                    _playbackState.value = state.copy(
                                        currentPositionSec = currentSec,
                                        durationSec = durationSec
                                    )
                                }
                            } catch (e: Exception) {
                                Log.w("AudioEngine", "Error reading position", e)
                            }
                        }
                    }
                }
                delay(1000)
            }
        }
    }

    fun playTrack(track: TrackEntity, autoPlay: Boolean = true) {
        stopPlayback()

        val trackDuration = if (track.duration > 0) track.duration else 240L
        _playbackState.value = _playbackState.value.copy(
            currentTrack = track,
            currentPositionSec = 0,
            durationSec = trackDuration,
            isSynthesizing = track.audioUri == null
        )

        if (!track.audioUri.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(track.audioUri)
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, uri)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setVolume(_playbackState.value.volume, _playbackState.value.volume)
                    prepareAsync()
                    setOnPreparedListener { player ->
                        val dur = (player.duration / 1000).toLong()
                        _playbackState.value = _playbackState.value.copy(
                            durationSec = if (dur > 0) dur else trackDuration
                        )
                        if (autoPlay) {
                            player.start()
                            _playbackState.value = _playbackState.value.copy(isPlaying = true)
                        }
                    }
                    setOnCompletionListener {
                        val curr = _playbackState.value
                        if (curr.repeatMode == "one") {
                            seekTo(0)
                            start()
                        } else {
                            _playbackState.value = curr.copy(isPlaying = false, currentPositionSec = 0)
                            onTrackCompletion?.invoke()
                        }
                    }
                    setOnErrorListener { _, _, _ ->
                        // Fallback to synth if file cannot be decoded
                        startSynth(autoPlay)
                        true
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioEngine", "Failed to init MediaPlayer, falling back to synth", e)
                startSynth(autoPlay)
            }
        } else {
            // Procedural hi-res generative ambient synth
            startSynth(autoPlay)
        }
    }

    private fun startSynth(autoPlay: Boolean) {
        _playbackState.value = _playbackState.value.copy(isSynthesizing = true)
        if (autoPlay) {
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
            launchSynthAudio()
        }
    }

    private fun launchSynthAudio() {
        synthJob?.cancel()
        synthJob = CoroutineScope(Dispatchers.Default).launch {
            val sampleRate = 44100
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufSize, 4096)
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            synthAudioTrack = track
            track.play()

            val chords = listOf(
                floatArrayOf(146.83f, 220.0f, 261.63f, 329.63f), // Dm7
                floatArrayOf(130.81f, 196.0f, 261.63f, 329.63f), // C9
                floatArrayOf(116.54f, 174.61f, 220.0f, 293.66f), // Bbmaj7
                floatArrayOf(98.0f, 146.83f, 220.0f, 293.66f)    // Gm7
            )

            var chordIndex = 0
            val buffer = ShortArray(1024)
            var phase = 0.0

            try {
                while (isActive && _playbackState.value.isPlaying) {
                    val currentNotes = chords[chordIndex % chords.size]
                    val freq = currentNotes[0]
                    val vol = _playbackState.value.volume * 0.15f

                    for (i in buffer.indices) {
                        phase += 2.0 * Math.PI * freq / sampleRate
                        if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI
                        val sample = (sin(phase) * 32767.0 * vol).toInt().toShort()
                        buffer[i] = sample
                    }

                    track.write(buffer, 0, buffer.size)
                    delay(10)
                }
            } catch (e: Exception) {
                Log.w("AudioEngine", "Synth interrupted", e)
            } finally {
                try {
                    track.stop()
                    track.release()
                } catch (ignored: Exception) {}
            }
        }
    }

    fun togglePlay() {
        if (_playbackState.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if (_playbackState.value.currentTrack == null) return
        _playbackState.value = _playbackState.value.copy(isPlaying = true)

        if (_playbackState.value.isSynthesizing) {
            launchSynthAudio()
        } else {
            try {
                mediaPlayer?.start()
            } catch (e: Exception) {
                Log.e("AudioEngine", "Error playing media", e)
            }
        }
    }

    fun pause() {
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
        synthJob?.cancel()
        try {
            synthAudioTrack?.pause()
            mediaPlayer?.pause()
        } catch (ignored: Exception) {}
    }

    fun seekTo(seconds: Long) {
        val clamped = seconds.coerceIn(0, _playbackState.value.durationSec)
        _playbackState.value = _playbackState.value.copy(currentPositionSec = clamped)

        if (!_playbackState.value.isSynthesizing) {
            try {
                mediaPlayer?.seekTo((clamped * 1000).toInt())
            } catch (ignored: Exception) {}
        }
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _playbackState.value = _playbackState.value.copy(volume = clamped)
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (ignored: Exception) {}
    }

    fun setEQPreset(preset: String) {
        _playbackState.value = _playbackState.value.copy(eqPreset = preset)
    }

    fun setOutputRoute(route: String) {
        _playbackState.value = _playbackState.value.copy(outputRoute = route)
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(isShuffle = !_playbackState.value.isShuffle)
    }

    fun cycleRepeatMode() {
        val next = when (_playbackState.value.repeatMode) {
            "all" -> "one"
            "one" -> "off"
            else -> "all"
        }
        _playbackState.value = _playbackState.value.copy(repeatMode = next)
    }

    private fun stopPlayback() {
        synthJob?.cancel()
        try {
            synthAudioTrack?.stop()
            synthAudioTrack?.release()
        } catch (ignored: Exception) {}
        synthAudioTrack = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (ignored: Exception) {}
        mediaPlayer = null
    }

    fun release() {
        stopPlayback()
        progressJob?.cancel()
    }
}
