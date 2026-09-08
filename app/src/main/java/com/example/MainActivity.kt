package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.SoundBundleEntity
import com.example.data.TrackEntity
import com.example.ui.ActiveTab
import com.example.ui.SoundVaultViewModel
import com.example.ui.components.AudioTagsModal
import com.example.ui.components.CoverArtPickerModal
import com.example.ui.components.CreateBundleModal
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.ProfileStatusModal
import com.example.ui.components.QrSyncModal
import com.example.ui.components.SoundVaultBottomNav
import com.example.ui.components.SoundVaultHeader
import com.example.ui.components.TrackContextMenuSheet
import com.example.ui.screens.BundlesScreen
import com.example.ui.screens.ImportScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.SoundVaultTheme
import com.example.ui.theme.VaultBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoundVaultTheme {
                SoundVaultApp()
            }
        }
    }
}

@Composable
fun SoundVaultApp(
    viewModel: SoundVaultViewModel = viewModel()
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val bundles by viewModel.bundles.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val vaultFilter by viewModel.vaultFilter.collectAsStateWithLifecycle()
    val sortAsc by viewModel.sortAsc.collectAsStateWithLifecycle()

    val currentTrack = playbackState.currentTrack

    // Modals state
    var selectedContextTrack by remember { mutableStateOf<TrackEntity?>(null) }
    var isContextMenuOpen by remember { mutableStateOf(false) }

    var selectedCoverTrack by remember { mutableStateOf<TrackEntity?>(null) }
    var isCoverPickerOpen by remember { mutableStateOf(false) }

    var isTagsModalOpen by remember { mutableStateOf(false) }
    var isProfileModalOpen by remember { mutableStateOf(false) }

    var qrSyncBundle by remember { mutableStateOf<SoundBundleEntity?>(null) }
    var isQrModalOpen by remember { mutableStateOf(false) }

    var isCreateBundleOpen by remember { mutableStateOf(false) }

    val totalStorageMB = tracks.sumOf { it.fileSizeMB }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(VaultBackground)
            .testTag("soundvault_scaffold"),
        containerColor = VaultBackground,
        topBar = {
            if (activeTab != ActiveTab.PLAYER) {
                SoundVaultHeader(
                    onProfileClick = { isProfileModalOpen = true }
                )
            }
        },
        bottomBar = {
            Column {
                // Mini Player when not on player screen
                AnimatedVisibility(
                    visible = activeTab != ActiveTab.PLAYER && currentTrack != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    MiniPlayerBar(
                        currentTrack = currentTrack,
                        isPlaying = playbackState.isPlaying,
                        currentPositionSec = playbackState.currentPositionSec,
                        durationSec = playbackState.durationSec,
                        onTogglePlay = { viewModel.togglePlay() },
                        onNextTrack = { viewModel.playNextTrack() },
                        onOpenPlayer = { viewModel.selectTab(ActiveTab.PLAYER) }
                    )
                }

                // Bottom Navigation
                SoundVaultBottomNav(
                    activeTab = activeTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(VaultBackground)
        ) {
            when (activeTab) {
                ActiveTab.VAULT -> VaultScreen(
                    tracks = tracks,
                    currentTrack = currentTrack,
                    isPlaying = playbackState.isPlaying,
                    searchQuery = searchQuery,
                    activeFilter = vaultFilter,
                    sortAsc = sortAsc,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onFilterChange = { viewModel.setVaultFilter(it) },
                    onToggleSort = { viewModel.toggleSort() },
                    onSelectTrack = { track ->
                        viewModel.playTrack(track)
                    },
                    onOpenContextMenu = { track ->
                        selectedContextTrack = track
                        isContextMenuOpen = true
                    },
                    onShuffleAll = { viewModel.shuffleAll() },
                    onGoToImport = { viewModel.selectTab(ActiveTab.IMPORT) }
                )

                ActiveTab.IMPORT -> ImportScreen(
                    recentTracks = tracks,
                    onAddTrackToVault = { title, artist, album, format, formatType, fileSizeMB, durationSec, coverArt, rawName, uri ->
                        viewModel.addTrackToVault(
                            title = title,
                            artist = artist,
                            album = album,
                            format = format,
                            formatType = formatType,
                            fileSizeMB = fileSizeMB,
                            durationSec = durationSec,
                            coverArt = coverArt,
                            rawName = rawName,
                            uri = uri
                        )
                    }
                )

                ActiveTab.PLAYER -> PlayerScreen(
                    currentTrack = currentTrack ?: tracks.firstOrNull(),
                    playbackState = playbackState,
                    onTogglePlay = { viewModel.togglePlay() },
                    onPrevTrack = { viewModel.playPrevTrack() },
                    onNextTrack = { viewModel.playNextTrack() },
                    onSeek = { posSec -> viewModel.seekTo(posSec) },
                    onVolumeChange = { vol -> viewModel.setVolume(vol) },
                    onEQChange = { eq -> viewModel.setEQ(eq) },
                    onOutputRouteChange = { route -> viewModel.setOutputRoute(route) },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onCycleRepeat = { viewModel.cycleRepeatMode() },
                    onToggleFavorite = { trackId -> viewModel.toggleFavorite(trackId) },
                    onOpenContextMenu = { track ->
                        selectedContextTrack = track
                        isContextMenuOpen = true
                    },
                    onOpenCoverArtPicker = { track ->
                        selectedCoverTrack = track
                        isCoverPickerOpen = true
                    },
                    onOpenAudioTags = { isTagsModalOpen = true },
                    onMinimize = { viewModel.selectTab(ActiveTab.VAULT) }
                )

                ActiveTab.BUNDLES -> BundlesScreen(
                    bundles = bundles,
                    vaultTracks = tracks,
                    onOpenQrSync = { bundle ->
                        qrSyncBundle = bundle
                        isQrModalOpen = true
                    },
                    onOpenCreateBundle = { isCreateBundleOpen = true }
                )

                ActiveTab.SETTINGS -> SettingsScreen(
                    trackCount = tracks.size,
                    totalStorageMB = totalStorageMB
                )
            }
        }
    }

    // Context Menu Sheet
    TrackContextMenuSheet(
        track = selectedContextTrack,
        isOpen = isContextMenuOpen,
        onDismiss = { isContextMenuOpen = false },
        onDeleteTrack = { trackId -> viewModel.deleteTrack(trackId) },
        onUpdateTrack = { updatedTrack ->
            viewModel.updateTrackMetadata(
                trackId = updatedTrack.id,
                title = updatedTrack.title,
                artist = updatedTrack.artist,
                album = updatedTrack.album,
                coverArt = updatedTrack.coverArt
            )
        },
        onOpenCoverArtPicker = { track ->
            selectedCoverTrack = track
            isCoverPickerOpen = true
        },
        onAddToBundle = { isCreateBundleOpen = true }
    )

    // Cover Art Picker Modal
    CoverArtPickerModal(
        isOpen = isCoverPickerOpen,
        currentArtwork = selectedCoverTrack?.coverArt ?: "",
        songTitle = selectedCoverTrack?.title ?: "Select Track",
        onClose = { isCoverPickerOpen = false },
        onSelectArtwork = { newCover ->
            selectedCoverTrack?.let { track ->
                viewModel.updateTrackArtwork(track.id, newCover)
            }
        }
    )

    // Audio Tags Modal
    AudioTagsModal(
        track = currentTrack ?: tracks.firstOrNull(),
        isOpen = isTagsModalOpen,
        onClose = { isTagsModalOpen = false }
    )

    // Profile Modal
    ProfileStatusModal(
        isOpen = isProfileModalOpen,
        trackCount = tracks.size,
        onClose = { isProfileModalOpen = false }
    )

    // QR Sync Modal
    QrSyncModal(
        bundle = qrSyncBundle,
        onClose = { isQrModalOpen = false }
    )

    // Create Bundle Modal
    CreateBundleModal(
        isOpen = isCreateBundleOpen,
        tracks = tracks,
        onClose = { isCreateBundleOpen = false },
        onCreateBundle = { title, selectedIds ->
            viewModel.createBundle(title, selectedIds)
            isCreateBundleOpen = false
        }
    )
}
