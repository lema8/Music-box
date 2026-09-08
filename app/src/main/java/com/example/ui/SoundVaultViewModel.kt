package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.audio.AudioPlaybackState
import com.example.data.AppSettings
import com.example.data.SoundBundleEntity
import com.example.data.SoundVaultDatabase
import com.example.data.SoundVaultRepository
import com.example.data.TrackEntity
import com.example.util.CoverArtPresets
import com.example.util.FilenameSanitizer
import com.example.util.TimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ActiveTab {
    VAULT,
    IMPORT,
    PLAYER,
    BUNDLES,
    SETTINGS
}

class SoundVaultViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SoundVaultDatabase.getDatabase(application)
    private val repository = SoundVaultRepository(database.trackDao(), database.bundleDao())
    val audioEngine = AudioEngine(application)

    val tracks: StateFlow<List<TrackEntity>> = repository.allTracks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bundles: StateFlow<List<SoundBundleEntity>> = repository.allBundles.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val playbackState: StateFlow<AudioPlaybackState> = audioEngine.playbackState

    private val _activeTab = MutableStateFlow(ActiveTab.VAULT)
    val activeTab: StateFlow<ActiveTab> = _activeTab.asStateFlow()

    private val _previousTab = MutableStateFlow(ActiveTab.VAULT)
    val previousTab: StateFlow<ActiveTab> = _previousTab.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _vaultFilter = MutableStateFlow("all")
    val vaultFilter: StateFlow<String> = _vaultFilter.asStateFlow()

    private val _sortAsc = MutableStateFlow(false)
    val sortAsc: StateFlow<Boolean> = _sortAsc.asStateFlow()

    // Modals & Context Menus
    private val _selectedContextTrack = MutableStateFlow<TrackEntity?>(null)
    val selectedContextTrack: StateFlow<TrackEntity?> = _selectedContextTrack.asStateFlow()

    private val _showProfileModal = MutableStateFlow(false)
    val showProfileModal: StateFlow<Boolean> = _showProfileModal.asStateFlow()

    private val _showAudioTagsModal = MutableStateFlow(false)
    val showAudioTagsModal: StateFlow<Boolean> = _showAudioTagsModal.asStateFlow()

    private val _showCoverArtPickerModal = MutableStateFlow(false)
    val showCoverArtPickerModal: StateFlow<Boolean> = _showCoverArtPickerModal.asStateFlow()

    private val _coverArtTargetTrack = MutableStateFlow<TrackEntity?>(null)
    val coverArtTargetTrack: StateFlow<TrackEntity?> = _coverArtTargetTrack.asStateFlow()

    private val _qrSyncBundle = MutableStateFlow<SoundBundleEntity?>(null)
    val qrSyncBundle: StateFlow<SoundBundleEntity?> = _qrSyncBundle.asStateFlow()

    private val _showCreateBundleModal = MutableStateFlow(false)
    val showCreateBundleModal: StateFlow<Boolean> = _showCreateBundleModal.asStateFlow()

    init {
        audioEngine.onTrackCompletion = {
            playNextTrack()
        }

        // Seed initial sample bundle if none exist
        viewModelScope.launch {
            repository.allBundles.collect { list ->
                if (list.isEmpty()) {
                    val initialBundle = SoundBundleEntity(
                        id = "bundle-synth-8893",
                        title = "Late Night Synthwave Vault",
                        trackCount = 12,
                        totalSizeMB = 680,
                        lossless = true,
                        coverArt = CoverArtPresets.presets[0].url,
                        tags = "Full Dynamic Masters, 3000px Covers & Cues, P2P Verified Hash",
                        trackIds = "",
                        status = "SYNCED",
                        formatLabel = "680 MB Lossless Audio",
                        dateCreated = "2026-09-02"
                    )
                    repository.insertBundle(initialBundle)
                }
            }
        }
    }

    fun selectTab(tab: ActiveTab) {
        if (_activeTab.value != ActiveTab.PLAYER) {
            _previousTab.value = _activeTab.value
        }
        _activeTab.value = tab
    }

    fun playTrack(track: TrackEntity, autoPlay: Boolean = true) {
        audioEngine.playTrack(track, autoPlay)
    }

    fun togglePlay() {
        audioEngine.togglePlay()
    }

    fun playNextTrack() {
        val currentTracks = tracks.value
        if (currentTracks.isEmpty()) return
        val currentId = playbackState.value.currentTrack?.id
        val currentIndex = currentTracks.indexOfFirst { it.id == currentId }
        val nextIndex = if (currentIndex >= 0) (currentIndex + 1) % currentTracks.size else 0
        playTrack(currentTracks[nextIndex], true)
    }

    fun playPrevTrack() {
        val currentTracks = tracks.value
        if (currentTracks.isEmpty()) return
        val currentId = playbackState.value.currentTrack?.id
        val currentIndex = currentTracks.indexOfFirst { it.id == currentId }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else currentTracks.size - 1
        playTrack(currentTracks[prevIndex], true)
    }

    fun shuffleAll() {
        val currentTracks = tracks.value
        if (currentTracks.isEmpty()) return
        val randomTrack = currentTracks.random()
        playTrack(randomTrack, true)
    }

    fun seekTo(seconds: Long) {
        audioEngine.seekTo(seconds)
    }

    fun setVolume(vol: Float) {
        audioEngine.setVolume(vol)
    }

    fun setEQ(preset: String) {
        audioEngine.setEQPreset(preset)
    }

    fun setOutputRoute(route: String) {
        audioEngine.setOutputRoute(route)
    }

    fun toggleShuffle() {
        audioEngine.toggleShuffle()
    }

    fun cycleRepeatMode() {
        audioEngine.cycleRepeatMode()
    }

    fun toggleFavorite(trackId: String) {
        viewModelScope.launch {
            val track = tracks.value.find { it.id == trackId } ?: return@launch
            val updated = track.copy(isFavorite = !track.isFavorite)
            repository.updateTrack(updated)
            if (playbackState.value.currentTrack?.id == trackId) {
                audioEngine.playTrack(updated, playbackState.value.isPlaying)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setVaultFilter(filter: String) {
        _vaultFilter.value = filter
    }

    fun toggleSort() {
        _sortAsc.value = !_sortAsc.value
    }

    fun openContextMenu(track: TrackEntity) {
        _selectedContextTrack.value = track
    }

    fun closeContextMenu() {
        _selectedContextTrack.value = null
    }

    fun openCoverArtPicker(track: TrackEntity?) {
        _coverArtTargetTrack.value = track
        _showCoverArtPickerModal.value = true
    }

    fun closeCoverArtPicker() {
        _coverArtTargetTrack.value = null
        _showCoverArtPickerModal.value = false
    }

    fun updateTrackArtwork(trackId: String, newArtwork: String) {
        viewModelScope.launch {
            val track = tracks.value.find { it.id == trackId } ?: return@launch
            val updated = track.copy(coverArt = newArtwork)
            repository.updateTrack(updated)
            if (playbackState.value.currentTrack?.id == trackId) {
                audioEngine.playTrack(updated, playbackState.value.isPlaying)
            }
        }
    }

    fun updateTrackMetadata(trackId: String, title: String, artist: String, album: String, coverArt: String) {
        viewModelScope.launch {
            val track = tracks.value.find { it.id == trackId } ?: return@launch
            val updated = track.copy(
                title = title.ifBlank { track.title },
                artist = artist.ifBlank { track.artist },
                album = album.ifBlank { track.album },
                coverArt = coverArt.ifBlank { track.coverArt }
            )
            repository.updateTrack(updated)
            if (playbackState.value.currentTrack?.id == trackId) {
                audioEngine.playTrack(updated, playbackState.value.isPlaying)
            }
        }
    }

    fun deleteTrack(trackId: String) {
        viewModelScope.launch {
            repository.deleteTrackById(trackId)
            if (playbackState.value.currentTrack?.id == trackId) {
                val remaining = tracks.value.filter { it.id != trackId }
                if (remaining.isNotEmpty()) {
                    playTrack(remaining[0], playbackState.value.isPlaying)
                } else {
                    audioEngine.pause()
                }
            }
        }
    }

    fun addTrackToVault(
        title: String,
        artist: String,
        album: String,
        format: String,
        formatType: String,
        fileSizeMB: Int,
        durationSec: Long,
        coverArt: String,
        rawName: String,
        uri: Uri?
    ) {
        viewModelScope.launch {
            val newTrack = TrackEntity(
                id = "track-${System.currentTimeMillis()}",
                title = title.ifBlank { "Imported Audio" },
                artist = artist.ifBlank { "SoundVault Artist" },
                album = album.ifBlank { "Offline Vault" },
                duration = if (durationSec > 0) durationSec else 240L,
                durationFormatted = TimeFormatter.formatSeconds(if (durationSec > 0) durationSec else 240L),
                format = format,
                formatType = formatType,
                fileSizeMB = if (fileSizeMB > 0) fileSizeMB else 25,
                dynamicRange = "DR14",
                bitrate = "BIT-EXACT 192k",
                sampleRate = "96.0 kHz / 24-bit",
                coverArt = coverArt.ifBlank { CoverArtPresets.defaultCover },
                originalRawName = rawName,
                audioUri = uri?.toString(),
                dateAdded = System.currentTimeMillis(),
                isFavorite = false,
                isLossless = formatType.uppercase() != "MP3",
                tags = "Local Ingest, Custom Cover"
            )
            repository.insertTrack(newTrack)
            playTrack(newTrack, true)
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
    }

    fun toggleSetting(key: String) {
        val current = _settings.value
        _settings.value = when (key) {
            "smartSanitizer" -> current.copy(smartSanitizer = !current.smartSanitizer)
            "defaultBitrateLossless" -> current.copy(defaultBitrateLossless = !current.defaultBitrateLossless)
            "autoEmbedID3" -> current.copy(autoEmbedID3 = !current.autoEmbedID3)
            "bitPerfectPlayback" -> current.copy(bitPerfectPlayback = !current.bitPerfectPlayback)
            "replayGain" -> current.copy(replayGain = !current.replayGain)
            "wifiOnlyIngest" -> current.copy(wifiOnlyIngest = !current.wifiOnlyIngest)
            "localP2PSharing" -> current.copy(localP2PSharing = !current.localP2PSharing)
            else -> current
        }
    }

    fun setCrossfadeSec(seconds: Float) {
        _settings.value = _settings.value.copy(gaplessCrossfadeSec = seconds)
    }

    fun purgeCache() {
        // Purge transient cache
    }

    fun openProfileModal() {
        _showProfileModal.value = true
    }

    fun closeProfileModal() {
        _showProfileModal.value = false
    }

    fun openAudioTagsModal() {
        _showAudioTagsModal.value = true
    }

    fun closeAudioTagsModal() {
        _showAudioTagsModal.value = false
    }

    fun openQrSync(bundle: SoundBundleEntity) {
        _qrSyncBundle.value = bundle
    }

    fun closeQrSync() {
        _qrSyncBundle.value = null
    }

    fun openCreateBundle() {
        _showCreateBundleModal.value = true
    }

    fun closeCreateBundle() {
        _showCreateBundleModal.value = false
    }

    fun createBundle(title: String, selectedTrackIds: List<String>) {
        viewModelScope.launch {
            val chosenTracks = tracks.value.filter { selectedTrackIds.contains(it.id) }
            val totalSize = chosenTracks.sumOf { it.fileSizeMB }.takeIf { it > 0 } ?: 120
            val newBundle = SoundBundleEntity(
                id = "bundle-${System.currentTimeMillis()}",
                title = title.ifBlank { "Offline Vault Package" },
                trackCount = maxOf(1, chosenTracks.size),
                totalSizeMB = totalSize,
                lossless = chosenTracks.any { it.isLossless },
                coverArt = chosenTracks.firstOrNull()?.coverArt ?: CoverArtPresets.defaultCover,
                tags = "Local Offline Package, Zero Loss",
                trackIds = selectedTrackIds.joinToString(","),
                status = "READY",
                formatLabel = "$totalSize MB Master Archive",
                dateCreated = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            )
            repository.insertBundle(newBundle)
            closeCreateBundle()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
