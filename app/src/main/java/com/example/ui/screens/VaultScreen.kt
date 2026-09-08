package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.TrackEntity
import com.example.ui.theme.*

@Composable
fun VaultScreen(
    tracks: List<TrackEntity>,
    currentTrack: TrackEntity?,
    isPlaying: Boolean,
    searchQuery: String,
    activeFilter: String,
    sortAsc: Boolean,
    onSearchChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onToggleSort: () -> Unit,
    onSelectTrack: (TrackEntity) -> Unit,
    onOpenContextMenu: (TrackEntity) -> Unit,
    onShuffleAll: () -> Unit,
    onGoToImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Filter & search tracks
    val filteredTracks = remember(tracks, searchQuery, activeFilter, sortAsc) {
        var list = tracks.filter { track ->
            if (searchQuery.isBlank()) true
            else {
                val q = searchQuery.lowercase()
                track.title.lowercase().contains(q) ||
                    track.artist.lowercase().contains(q) ||
                    track.album.lowercase().contains(q) ||
                    track.format.lowercase().contains(q)
            }
        }

        list = when (activeFilter) {
            "recent" -> list.sortedByDescending { it.dateAdded }
            "hires" -> list.filter { it.isLossless }
            "tags" -> list.filter { it.tags.isNotBlank() }
            else -> list
        }

        if (sortAsc) list.reversed() else list
    }

    val totalSizeMB = tracks.sumOf { it.fileSizeMB }
    val totalSizeFormatted = if (totalSizeMB > 1024) {
        String.format("%.2f GB", totalSizeMB / 1024.0)
    } else {
        "$totalSizeMB MB"
    }

    val flacCount = tracks.count { it.formatType == "FLAC" || it.formatType == "ALAC" }
    val wavCount = tracks.count { it.formatType == "WAV" }
    val mp3Count = tracks.count { it.formatType == "MP3" || it.formatType == "M4A" || it.formatType == "OPUS" || it.formatType == "OGG" }

    val infiniteTransition = rememberInfiniteTransition(label = "rowEqualizer")
    val b1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val b2 by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val b3 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b3"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VaultSurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = VaultSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = if (tracks.isNotEmpty()) "Search ${tracks.size} imported tracks..." else "Search offline vault...",
                            color = VaultOutline,
                            fontSize = 13.sp
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = VaultOnSurface,
                        unfocusedTextColor = VaultOnSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("vault_search_input")
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Clear search",
                            tint = VaultOutline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(18.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = VaultTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "OFFLINE",
                        color = VaultTertiary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            // Storage Overview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(VaultSurfaceContainerLow)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .testTag("storage_overview_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VaultSurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SdCard,
                                    contentDescription = null,
                                    tint = VaultPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Phone Local Storage",
                                    color = VaultOnSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ISOLATED ANDROID VAULT",
                                    color = VaultSecondary,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(VaultSurfaceContainerHighest)
                                .border(1.dp, VaultTertiary.copy(alpha = 0.25f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = VaultTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "PRIVATE LIBRARY",
                                color = VaultTertiary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Storage Numbers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = totalSizeFormatted,
                            color = VaultOnSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "${tracks.size} ${if (tracks.size == 1) "Imported Track" else "Imported Tracks"}",
                            color = VaultOutline,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Segmented Bar Indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(VaultSurfaceContainerHighest)
                    ) {
                        if (tracks.isNotEmpty()) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                if (flacCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(flacCount.toFloat())
                                            .fillMaxHeight()
                                            .background(VaultPrimary)
                                    )
                                }
                                if (wavCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(wavCount.toFloat())
                                            .fillMaxHeight()
                                            .background(VaultSecondary)
                                    )
                                }
                                if (mp3Count > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(mp3Count.toFloat())
                                            .fillMaxHeight()
                                            .background(VaultOutline)
                                    )
                                }
                            }
                        }
                    }

                    // Format Breakdown Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FormatPill("FLAC", flacCount, VaultPrimary)
                        FormatPill("WAV", wavCount, VaultSecondary)
                        FormatPill("Compressed", mp3Count, VaultOutline)

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onGoToImport() }
                                .padding(vertical = 2.dp)
                                .testTag("vault_card_import_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = VaultSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "IMPORT",
                                color = VaultSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (tracks.isNotEmpty()) {
            item {
                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        label = "All Tracks (${tracks.size})",
                        icon = Icons.Default.FolderOpen,
                        isSelected = activeFilter == "all",
                        testTag = "filter_all",
                        onClick = { onFilterChange("all") }
                    )
                    FilterChip(
                        label = "Recently Added",
                        isSelected = activeFilter == "recent",
                        testTag = "filter_recent",
                        onClick = { onFilterChange("recent") }
                    )
                    FilterChip(
                        label = "Hi-Res Lossless",
                        icon = Icons.Default.GraphicEq,
                        isSelected = activeFilter == "hires",
                        testTag = "filter_hires",
                        onClick = { onFilterChange("hires") }
                    )
                    FilterChip(
                        label = "Tagged",
                        icon = Icons.Default.Tag,
                        isSelected = activeFilter == "tags",
                        testTag = "filter_tags",
                        onClick = { onFilterChange("tags") }
                    )
                }
            }

            item {
                // Action row: Shuffle & Sort
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onShuffleAll,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultPrimaryContainer
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("shuffle_vault_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Shuffle Vault",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = onToggleSort,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(VaultSurfaceContainerHigh)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Toggle Sort Direction",
                                tint = VaultOnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VaultSurfaceContainerLow)
                            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                            .clickable { onToggleSort() }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = null,
                            tint = VaultOutline,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (sortAsc) "Oldest First" else "Date Added",
                            color = VaultOnSurfaceVariant,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Empty state or Track items
        if (tracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(VaultSurfaceContainerLow)
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                        .testTag("vault_empty_state"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(VaultPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = VaultPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "Your SoundVault is Empty",
                            color = VaultOnSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "No pre-loaded songs or sample tracks. SoundVault stores only the songs you import, keeping your music 100% offline and bit-perfect on your phone.",
                            color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Button(
                            onClick = onGoToImport,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultPrimary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("empty_state_import_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = null,
                                    tint = VaultOnPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Import Songs from Storage",
                                    color = VaultOnPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "Supports FLAC, WAV, M4A, MP3, OPUS + Custom Cover Art",
                            color = VaultOutline,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        } else {
            items(filteredTracks, key = { it.id }) { track ->
                val isCurrent = currentTrack?.id == track.id
                val isRowPlaying = isCurrent && isPlaying

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCurrent) VaultSurfaceContainer else VaultSurfaceContainerLow.copy(alpha = 0.6f))
                        .border(
                            width = 1.dp,
                            color = if (isCurrent) VaultPrimary.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.04f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectTrack(track) }
                        .padding(10.dp)
                        .testTag("track_row_${track.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Artwork with equalizer overlay if playing
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(VaultSurfaceContainerHigh)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (track.coverArt.isNotBlank()) {
                                AsyncImage(
                                    model = track.coverArt,
                                    contentDescription = track.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(48.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = VaultPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            if (isRowPlaying) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(VaultSurfaceContainerLowest.copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.Bottom,
                                        modifier = Modifier.height(16.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .fillMaxHeight(b1)
                                                .background(VaultSecondary)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .fillMaxHeight(b2)
                                                .background(VaultPrimary)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .fillMaxHeight(b3)
                                                .background(VaultTertiary)
                                        )
                                    }
                                }
                            }
                        }

                        // Track Details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                color = if (isCurrent) VaultPrimary else VaultOnSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = track.artist,
                                color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (track.isLossless) VaultSurfaceContainerHighest else VaultSurfaceContainerHigh)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = track.format,
                                        color = if (track.isLossless) VaultSecondary else VaultOutline,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${track.fileSizeMB} MB",
                                    color = VaultOutline,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Duration & More menu
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = track.durationFormatted,
                            color = VaultOutline,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        IconButton(
                            onClick = { onOpenContextMenu(track) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("track_menu_btn_${track.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Track options",
                                tint = VaultOutline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (filteredTracks.isEmpty() && searchQuery.isNotBlank()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = VaultOutline,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No tracks matching \"$searchQuery\"",
                                color = VaultOnSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            item {
                // End of collection footer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(VaultSecondary)
                    )
                    Text(
                        text = "${tracks.size} MASTER TRACKS OFFLINE • ZERO TELEMETRY",
                        color = VaultOutline,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun FormatPill(label: String, count: Int, dotColor: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(VaultSurfaceContainerHigh)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = "$label ($count)",
            color = VaultOnSurfaceVariant,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) VaultPrimary else VaultSurfaceContainerHigh)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) VaultOnPrimary else VaultSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = label,
            color = if (isSelected) VaultOnPrimary else VaultOnSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
