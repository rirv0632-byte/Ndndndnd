package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.QuranAudioPlayer
import com.example.data.local.AppDatabase
import com.example.data.local.ReelProjectEntity
import com.example.data.model.Ayah
import com.example.data.model.ReelConfig
import com.example.data.model.Surah
import com.example.data.quran.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.reelProjectDao()

    val audioPlayer = QuranAudioPlayer(viewModelScope)
    val audioState = audioPlayer.state

    private val _reelConfig = MutableStateFlow(ReelConfig())
    val reelConfig: StateFlow<ReelConfig> = _reelConfig.asStateFlow()

    private val _versesList = MutableStateFlow<List<Ayah>>(emptyList())
    val versesList: StateFlow<List<Ayah>> = _versesList.asStateFlow()

    private val _isLoadingVerses = MutableStateFlow(false)
    val isLoadingVerses: StateFlow<Boolean> = _isLoadingVerses.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredSurahs: StateFlow<List<Surah>> = _searchQuery.combine(MutableStateFlow(QuranRepository.allSurahs)) { query, all ->
        if (query.isBlank()) {
            all
        } else {
            val q = query.trim().lowercase()
            all.filter {
                it.nameArabic.contains(q) ||
                it.nameEnglish.lowercase().contains(q) ||
                it.number.toString() == q
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuranRepository.allSurahs)

    val savedProjects: StateFlow<List<ReelProjectEntity>> = dao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAyah: StateFlow<Ayah?> = combine(versesList, audioState) { verses, aState ->
        if (verses.isEmpty()) return@combine null
        verses.firstOrNull { it.numberInSurah == aState.currentAyah } ?: verses.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadVersesForCurrentConfig()
    }

    fun selectSurah(surahNumber: Int) {
        val surah = QuranRepository.allSurahs.firstOrNull { it.number == surahNumber } ?: return
        val maxAyah = surah.versesCount
        val newTo = minOf(3, maxAyah)
        _reelConfig.value = _reelConfig.value.copy(
            surahNumber = surahNumber,
            surahName = surah.nameArabic,
            fromAyah = 1,
            toAyah = newTo
        )
        audioPlayer.stop()
        loadVersesForCurrentConfig()
    }

    fun setAyahRange(from: Int, to: Int) {
        val surah = QuranRepository.allSurahs.firstOrNull { it.number == _reelConfig.value.surahNumber }
        val maxVerses = surah?.versesCount ?: 100
        val safeFrom = from.coerceIn(1, maxVerses)
        val safeTo = to.coerceIn(safeFrom, maxVerses)

        _reelConfig.value = _reelConfig.value.copy(
            fromAyah = safeFrom,
            toAyah = safeTo
        )
        audioPlayer.stop()
        loadVersesForCurrentConfig()
    }

    fun selectReciter(reciterId: String) {
        _reelConfig.value = _reelConfig.value.copy(reciterId = reciterId)
        if (audioState.value.isPlaying) {
            playCurrentReel()
        }
    }

    fun selectBackground(backgroundId: String) {
        _reelConfig.value = _reelConfig.value.copy(backgroundId = backgroundId)
    }

    fun setAspectRatio(ratio: String) {
        _reelConfig.value = _reelConfig.value.copy(aspectRatio = ratio)
    }

    fun setFontSize(size: Float) {
        _reelConfig.value = _reelConfig.value.copy(fontSize = size)
    }

    fun setFontFamilyType(type: String) {
        _reelConfig.value = _reelConfig.value.copy(fontFamilyType = type)
    }

    fun setTextColorHex(hex: String) {
        _reelConfig.value = _reelConfig.value.copy(textColorHex = hex)
    }

    fun toggleTranslation() {
        _reelConfig.value = _reelConfig.value.copy(showTranslation = !_reelConfig.value.showTranslation)
    }

    fun toggleSurahBadge() {
        _reelConfig.value = _reelConfig.value.copy(showSurahBadge = !_reelConfig.value.showSurahBadge)
    }

    fun toggleReciterBadge() {
        _reelConfig.value = _reelConfig.value.copy(showReciterBadge = !_reelConfig.value.showReciterBadge)
    }

    fun toggleFrame() {
        _reelConfig.value = _reelConfig.value.copy(showFrame = !_reelConfig.value.showFrame)
    }

    fun setMotionEffect(effect: String) {
        _reelConfig.value = _reelConfig.value.copy(motionEffect = effect)
    }

    fun setDarkOverlayAlpha(alpha: Float) {
        _reelConfig.value = _reelConfig.value.copy(darkOverlayAlpha = alpha.coerceIn(0f, 0.9f))
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playCurrentReel() {
        val cfg = _reelConfig.value
        val reciter = QuranRepository.allReciters.firstOrNull { it.id == cfg.reciterId }
            ?: QuranRepository.allReciters.first()
        audioPlayer.playRange(cfg.surahNumber, cfg.fromAyah, cfg.toAyah, reciter.subpath)
    }

    fun togglePlayPause() {
        if (audioState.value.isPlaying) {
            audioPlayer.pause()
        } else {
            if (audioState.value.durationMs > 0 && audioState.value.currentAyah in _reelConfig.value.fromAyah.._reelConfig.value.toAyah) {
                audioPlayer.resume()
            } else {
                playCurrentReel()
            }
        }
    }

    fun nextAyah() {
        audioPlayer.nextAyah()
    }

    fun previousAyah() {
        audioPlayer.previousAyah()
    }

    fun seekAudio(progress: Float) {
        audioPlayer.seekTo(progress)
    }

    fun saveCurrentProject(customTitle: String? = null, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            val cfg = _reelConfig.value
            val title = if (!customTitle.isNullOrBlank()) {
                customTitle
            } else {
                "${cfg.surahName} (${cfg.fromAyah}-${cfg.toAyah})"
            }
            val entity = ReelProjectEntity.fromConfig(title, cfg)
            dao.insertProject(entity)
            onSaved()
        }
    }

    fun loadProject(project: ReelProjectEntity) {
        _reelConfig.value = project.toReelConfig()
        audioPlayer.stop()
        loadVersesForCurrentConfig()
    }

    fun deleteProject(project: ReelProjectEntity) {
        viewModelScope.launch {
            dao.deleteProject(project)
        }
    }

    private fun loadVersesForCurrentConfig() {
        val cfg = _reelConfig.value
        _isLoadingVerses.value = true
        viewModelScope.launch {
            val ayahs = QuranRepository.getVersesRange(cfg.surahNumber, cfg.fromAyah, cfg.toAyah)
            _versesList.value = ayahs
            _isLoadingVerses.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
