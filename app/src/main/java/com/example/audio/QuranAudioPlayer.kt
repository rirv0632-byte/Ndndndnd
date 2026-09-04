package com.example.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurah: Int = 1,
    val currentAyah: Int = 1,
    val fromAyah: Int = 1,
    val toAyah: Int = 7,
    val positionMs: Int = 0,
    val durationMs: Int = 0,
    val errorMessage: String? = null
) {
    val progress: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}

class QuranAudioPlayer(
    private val scope: CoroutineScope
) {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _state = MutableStateFlow(AudioPlayerState())
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private var currentReciterSubpath: String = "Alafasy_128kbps"

    private fun initPlayerIfNeeded() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnCompletionListener {
                    onAyahCompleted()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("QuranAudioPlayer", "MediaPlayer error: what=$what extra=$extra")
                    _state.value = _state.value.copy(
                        isPlaying = false,
                        isLoading = false,
                        errorMessage = "تعذر تشغيل الصوت. يرجى التحقق من الاتصال بالإنترنت."
                    )
                    true
                }
            }
        }
    }

    fun playRange(surahNumber: Int, fromAyah: Int, toAyah: Int, reciterSubpath: String) {
        currentReciterSubpath = reciterSubpath
        _state.value = _state.value.copy(
            currentSurah = surahNumber,
            fromAyah = fromAyah,
            toAyah = toAyah,
            currentAyah = fromAyah,
            errorMessage = null
        )
        playAyahInternal(surahNumber, fromAyah)
    }

    fun playAyah(surahNumber: Int, ayahNumber: Int, reciterSubpath: String) {
        currentReciterSubpath = reciterSubpath
        _state.value = _state.value.copy(
            currentSurah = surahNumber,
            currentAyah = ayahNumber,
            errorMessage = null
        )
        playAyahInternal(surahNumber, ayahNumber)
    }

    private fun playAyahInternal(surahNumber: Int, ayahNumber: Int) {
        initPlayerIfNeeded()
        _state.value = _state.value.copy(
            isLoading = true,
            currentSurah = surahNumber,
            currentAyah = ayahNumber,
            positionMs = 0,
            durationMs = 0
        )
        stopProgressTracker()

        val s = String.format("%03d", surahNumber)
        val a = String.format("%03d", ayahNumber)
        val audioUrl = "https://everyayah.com/data/$currentReciterSubpath/$s$a.mp3"

        scope.launch(Dispatchers.IO) {
            try {
                mediaPlayer?.apply {
                    reset()
                    setDataSource(audioUrl)
                    setOnPreparedListener { mp ->
                        mp.start()
                        _state.value = _state.value.copy(
                            isPlaying = true,
                            isLoading = false,
                            durationMs = mp.duration,
                            positionMs = 0
                        )
                        startProgressTracker()
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e("QuranAudioPlayer", "Error loading audio: ${e.message}")
                _state.value = _state.value.copy(
                    isPlaying = false,
                    isLoading = false,
                    errorMessage = "تعذر تحميل التلاوة. يرجى التأكد من اتصال الإنترنت."
                )
            }
        }
    }

    private fun onAyahCompleted() {
        stopProgressTracker()
        val current = _state.value.currentAyah
        val to = _state.value.toAyah
        if (current < to) {
            val nextAyah = current + 1
            _state.value = _state.value.copy(currentAyah = nextAyah)
            playAyahInternal(_state.value.currentSurah, nextAyah)
        } else {
            // Reached end of range
            _state.value = _state.value.copy(
                isPlaying = false,
                isLoading = false,
                positionMs = _state.value.durationMs
            )
        }
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _state.value = _state.value.copy(isPlaying = false)
                stopProgressTracker()
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying && !_state.value.isLoading) {
                if (_state.value.positionMs >= _state.value.durationMs && _state.value.durationMs > 0) {
                    // Replay from beginning
                    playAyahInternal(_state.value.currentSurah, _state.value.fromAyah)
                } else {
                    it.start()
                    _state.value = _state.value.copy(isPlaying = true)
                    startProgressTracker()
                }
            }
        } ?: run {
            playAyahInternal(_state.value.currentSurah, _state.value.currentAyah)
        }
    }

    fun nextAyah() {
        val current = _state.value.currentAyah
        val to = _state.value.toAyah
        if (current < to) {
            playAyahInternal(_state.value.currentSurah, current + 1)
        }
    }

    fun previousAyah() {
        val current = _state.value.currentAyah
        val from = _state.value.fromAyah
        if (current > from) {
            playAyahInternal(_state.value.currentSurah, current - 1)
        }
    }

    fun seekTo(progressFraction: Float) {
        mediaPlayer?.let { mp ->
            val targetMs = (progressFraction * mp.duration).toInt()
            mp.seekTo(targetMs)
            _state.value = _state.value.copy(positionMs = targetMs)
        }
    }

    fun stop() {
        stopProgressTracker()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.reset()
        }
        _state.value = _state.value.copy(
            isPlaying = false,
            isLoading = false,
            positionMs = 0
        )
    }

    fun release() {
        stopProgressTracker()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch(Dispatchers.Main) {
            while (isActive && _state.value.isPlaying) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _state.value = _state.value.copy(
                            positionMs = mp.currentPosition,
                            durationMs = mp.duration
                        )
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }
}
