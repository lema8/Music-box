package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.audio.AudioPlaybackState
import com.example.data.TrackEntity
import com.example.ui.theme.*
import com.example.util.TimeFormatter

@Composable
fun PlayerScreen(
    currentTrack: TrackEntity?,
    playbackState: AudioPlaybackState,
    onTogglePlay: () -> Unit,
    onPrevTrack: () -> Unit,
    onNextTrack: () -> Unit,
    onSeek: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onEQChange: (String) -> Unit,
    onOutputRouteChange: (String) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenContextMenu: (TrackEntity) -> Unit,
    onOpenCoverArtPicker: (TrackEntity) -> Unit,
    onOpenAudioTags: () -> Unit,
    onMinimize: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentTrack == null) return

    val duration = playbackState.durationSec
    val position = playbackState.currentPositionSec
    val progress = if (duration > 0) (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
    val remainingSec = maxOf(0L, duration - position)

    val infiniteTransition = rememberInfiniteTransition(label = "playerAura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "auraScale"
    )

    // 60 Waveform procedural bar heights
    val barHeights = remember {
        listOf(
            35, 50, 70, 40, 85, 95, 60, 75, 45, 60, 100, 80, 65, 50, 70, 90, 45, 60, 75,
            95, 85, 40, 70, 80, 60, 75, 90, 100, 65, 55, 70, 85, 45, 60, 75, 90, 50, 35,
            65, 80, 95, 60, 70, 40, 55, 75, 90, 65, 45, 30, 55, 70, 85, 65, 45, 75, 90,
            60, 40, 30
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMinimize,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VaultSurfaceContainerHigh)
                    .testTag("minimize_player_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Minimize Player",
                    tint = VaultOnSurface,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "OFFLINE STORAGE HUB",
                    color = VaultSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Playing from Local Vault",
                    color = VaultOnSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = { onOpenContextMenu(currentTrack) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VaultSurfaceContainerHigh)
                    .testTag("player_more_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = VaultOnSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Centerpiece: Album Artwork with ambient glow and vinyl peek
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Neon aura backdrop
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .scale(auraScale)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                VaultPrimary.copy(alpha = 0.35f),
                                VaultSecondary.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
                    .blur(20.dp)
            )

            // Vinyl disc peek
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .offset(x = 18.dp)
                    .clip(CircleShape)
                    .background(VaultSurfaceContainerLowest)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(VaultSurfaceContainerHigh)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VaultSurfaceContainerLowest)
                    )
                }
            }

            // Main Album Frame
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(VaultSurfaceContainerHigh)
                    .border(1.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .testTag("player_cover_art_frame")
            ) {
                if (currentTrack.coverArt.isNotBlank()) {
                    AsyncImage(
                        model = currentTrack.coverArt,
                        contentDescription = currentTrack.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(VaultSurfaceContainerHigh, VaultSurfaceContainerLowest)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = VaultPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // Quick Change Cover Art Overlay Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(VaultSurfaceContainerLowest.copy(alpha = 0.85f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                        .clickable { onOpenCoverArtPicker(currentTrack) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("player_change_cover_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = VaultPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Cover Art",
                            color = VaultPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bottom Badges Scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, VaultSurfaceContainerLowest.copy(alpha = 0.85f))
                            )
                        )
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(VaultSurfaceContainerLowest.copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "STUDIO MASTER",
                                color = VaultSecondary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(VaultSurfaceContainerLowest.copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "100% BIT-PERFECT",
                                color = VaultTertiary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Track Meta & Favorite Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentTrack.title,
                    color = VaultOnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${currentTrack.artist} • ${currentTrack.album}",
                    color = VaultOnSurfaceVariant.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { onToggleFavorite(currentTrack.id) },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(VaultSurfaceContainer)
                    .testTag("player_favorite_btn")
            ) {
                Icon(
                    imageVector = if (currentTrack.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (currentTrack.isFavorite) VaultPrimary else VaultOnSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Lossless Spec Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VaultSurfaceContainerHigh)
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VaultSurfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = VaultTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "${currentTrack.format} • ${currentTrack.sampleRate}",
                            color = VaultTertiary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Local Storage Direct • Offline Validated",
                            color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VaultSurfaceContainerHighest)
                        .clickable { onOpenAudioTags() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("player_tags_btn")
                ) {
                    Text(
                        text = "TAGS",
                        color = VaultSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Interactive Audio Waveform Scrubber
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VaultSurfaceContainer)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .pointerInput(duration) {
                        detectTapGestures { offset ->
                            val ratio = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                            onSeek((ratio * duration).toLong())
                        }
                    }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Background bars
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    barHeights.forEachIndexed { index, heightPercent ->
                        val barFraction = index.toFloat() / barHeights.size.toFloat()
                        val isPlayed = barFraction <= progress
                        val barColor = if (isPlayed) {
                            if (index % 3 == 0) VaultSecondary else VaultPrimary
                        } else {
                            VaultSurfaceContainerHighest
                        }

                        Box(
                            modifier = Modifier
                                .width(3.5.dp)
                                .fillMaxHeight(fraction = heightPercent / 100f)
                                .clip(RoundedCornerShape(2.dp))
                                .background(barColor)
                        )
                    }
                }
            }

            // Time Markers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = TimeFormatter.formatSeconds(position),
                    color = VaultSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "-${TimeFormatter.formatSeconds(remainingSec)}",
                    color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Primary Playback Transport Hub
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shuffle
            IconButton(
                onClick = onToggleShuffle,
                modifier = Modifier.testTag("player_shuffle_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (playbackState.isShuffle) VaultSecondary else VaultOnSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Previous
            IconButton(
                onClick = onPrevTrack,
                modifier = Modifier.size(48.dp).testTag("player_prev_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous Track",
                    tint = VaultOnSurface,
                    modifier = Modifier.size(34.dp)
                )
            }

            // Master Neon Play/Pause Knob
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(VaultPrimary, VaultPrimaryContainer, VaultSecondary)
                        )
                    )
                    .clickable { onTogglePlay() }
                    .testTag("player_master_play_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                    tint = VaultSurfaceContainerLowest,
                    modifier = Modifier.size(42.dp)
                )
            }

            // Next
            IconButton(
                onClick = onNextTrack,
                modifier = Modifier.size(48.dp).testTag("player_next_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next Track",
                    tint = VaultOnSurface,
                    modifier = Modifier.size(34.dp)
                )
            }

            // Repeat Loop
            IconButton(
                onClick = onCycleRepeat,
                modifier = Modifier.testTag("player_repeat_btn")
            ) {
                Icon(
                    imageVector = if (playbackState.repeatMode == "one") Icons.Default.RepeatOne else Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (playbackState.repeatMode != "off") VaultPrimary else VaultOnSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Studio Sound Tuning: EQ & Output Route
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 10-Band EQ Cycler
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VaultSurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                    .clickable {
                        val presets = listOf("Studio Warmth", "Bass Boost", "Hi-Res Clarity", "Direct Neutral", "Vocal Focus")
                        val nextIdx = (presets.indexOf(playbackState.eqPreset) + 1) % presets.size
                        onEQChange(presets[nextIdx])
                    }
                    .padding(10.dp)
                    .testTag("player_eq_toggle")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "10-BAND EQ",
                            color = VaultOnSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(VaultSecondary)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = null,
                            tint = VaultSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = playbackState.eqPreset,
                                color = VaultOnSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = when (playbackState.eqPreset) {
                                    "Studio Warmth" -> "+2.5dB Bass • Linear"
                                    "Bass Boost" -> "+5.0dB Sub • Punch"
                                    else -> "+1.5dB Treble • Clarity"
                                },
                                color = VaultSecondary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Output Route Cycler
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VaultSurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                    .clickable {
                        val routes = listOf("USB DAC / ASIO", "Internal 32-bit DAC", "Bluetooth LDAC 990k")
                        val nextIdx = (routes.indexOf(playbackState.outputRoute) + 1) % routes.size
                        onOutputRouteChange(routes[nextIdx])
                    }
                    .padding(10.dp)
                    .testTag("player_output_route_toggle")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OUTPUT ROUTE",
                            color = VaultOnSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(VaultTertiary)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = VaultTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = playbackState.outputRoute,
                                color = VaultOnSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "Direct Passthrough",
                                color = VaultTertiary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Hardware Master Volume Fader
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VaultSurfaceContainerLow)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { onVolumeChange(if (playbackState.volume == 0f) 0.8f else 0f) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (playbackState.volume == 0f) Icons.Default.VolumeOff else Icons.Default.VolumeDown,
                    contentDescription = "Mute",
                    tint = VaultOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Slider(
                value = playbackState.volume,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = VaultPrimary,
                    inactiveTrackColor = VaultSurfaceContainerHighest
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("volume_slider")
            )

            IconButton(
                onClick = { onVolumeChange(1f) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Max Volume",
                    tint = VaultOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
