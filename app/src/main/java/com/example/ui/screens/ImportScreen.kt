package com.example.ui.screens

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.TrackEntity
import com.example.ui.components.CoverArtPickerModal
import com.example.ui.theme.*
import com.example.util.CoverArtPresets
import com.example.util.FilenameSanitizer

@Composable
fun ImportScreen(
    recentTracks: List<TrackEntity>,
    onAddTrackToVault: (
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
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var streamUrl by remember { mutableStateOf("") }
    var isFetchingStream by remember { mutableStateOf(false) }

    // Staged track state
    var hasFileStaged by remember { mutableStateOf(false) }
    var stagedUri by remember { mutableStateOf<Uri?>(null) }
    var rawFilename by remember { mutableStateOf("") }
    var scrubEnabled by remember { mutableStateOf(true) }
    var stagedTitle by remember { mutableStateOf("") }
    var stagedArtist by remember { mutableStateOf("") }
    var stagedAlbum by remember { mutableStateOf("Offline Vault Master") }
    var targetFormat by remember { mutableStateOf("FLAC") }
    var targetBitrate by remember { mutableStateOf("BIT-EXACT 192k") }
    var stagedCoverArt by remember { mutableStateOf(CoverArtPresets.defaultCover) }
    var stagedDurationSec by remember { mutableLongStateOf(240L) }
    var stagedFileSizeMB by remember { mutableIntStateOf(25) }
    var isCoverModalOpen by remember { mutableStateOf(false) }
    var saveBtnSuccess by remember { mutableStateOf(false) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { pickedUri ->
            stagedUri = pickedUri
            var name = "selected_audio.flac"
            var size = 25

            try {
                context.contentResolver.query(pickedUri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) name = cursor.getString(nameIndex) ?: name
                        if (sizeIndex != -1) {
                            val bytes = cursor.getLong(sizeIndex)
                            if (bytes > 0) size = maxOf(1, (bytes / (1024 * 1024)).toInt())
                        }
                    }
                }
            } catch (ignored: Exception) {}

            rawFilename = name
            hasFileStaged = true
            stagedFileSizeMB = size

            // Read duration if possible
            try {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(context, pickedUri)
                val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                if (durationStr != null) {
                    val ms = durationStr.toLongOrNull() ?: 240000L
                    stagedDurationSec = ms / 1000
                }
                val extTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val extArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val extAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)

                if (extTitle != null && extTitle.isNotBlank()) stagedTitle = extTitle
                if (extArtist != null && extArtist.isNotBlank()) stagedArtist = extArtist
                if (extAlbum != null && extAlbum.isNotBlank()) stagedAlbum = extAlbum
                mmr.release()
            } catch (ignored: Exception) {}

            // Determine format
            val ext = name.substringAfterLast('.', "FLAC").uppercase()
            targetFormat = when (ext) {
                "WAV" -> "WAV"
                "MP3" -> "MP3"
                "ALAC", "M4A" -> "ALAC"
                else -> "FLAC"
            }

            if (scrubEnabled && stagedTitle.isBlank()) {
                val cleaned = FilenameSanitizer.sanitize(name)
                stagedTitle = cleaned.title
                stagedArtist = cleaned.artist
                stagedAlbum = cleaned.album
            } else if (stagedTitle.isBlank()) {
                stagedTitle = name.substringBeforeLast('.')
                stagedArtist = "Local Artist"
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(VaultSecondary)
                        )
                        Text(
                            text = "AUDIO INGESTION & COVER STAGING",
                            color = VaultSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(VaultSurfaceContainerHigh)
                            .border(1.dp, VaultTertiary.copy(alpha = 0.2f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "100% OFFLINE",
                            color = VaultTertiary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "Import & Rename Audio",
                    color = VaultOnSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Import local audio songs, clean raw file names, and attach custom song images from your phone.",
                    color = VaultOnSurfaceVariant.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        item {
            // Main File Picker Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(VaultSurfaceContainerLow)
                    .border(1.5.dp, VaultPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .clickable { audioPickerLauncher.launch("audio/*") }
                    .padding(22.dp)
                    .testTag("tap_to_select_audio_btn"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(VaultSurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AudioFile,
                            contentDescription = null,
                            tint = VaultPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = "Tap to Select Audio from Phone",
                        color = VaultOnSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose from Android Downloads, Internal Storage, or SD Card",
                        color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        FormatTag("FLAC", VaultSecondary)
                        FormatTag("WAV 32b", VaultTertiary)
                        FormatTag("M4A", VaultPrimary)
                        FormatTag("MP3", VaultOutline)
                        FormatTag("OPUS", VaultOutline)
                    }
                }
            }
        }

        item {
            // Or Paste Audio URL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VaultSurfaceContainer)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = VaultSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "OR FETCH DIRECT AUDIO LINK / STREAM",
                            color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(VaultSurfaceContainerLowest)
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = streamUrl,
                            onValueChange = { streamUrl = it },
                            placeholder = { Text("Paste audio file link or online stream URL...", fontSize = 11.sp, color = VaultOutline) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = VaultOnSurface,
                                unfocusedTextColor = VaultOnSurface
                            ),
                            modifier = Modifier.weight(1f).testTag("stream_url_input")
                        )

                        Button(
                            onClick = {
                                if (streamUrl.isNotBlank()) {
                                    val filename = streamUrl.substringAfterLast('/').substringBefore('?')
                                    rawFilename = filename.ifBlank { "stream_master_audio.flac" }
                                    hasFileStaged = true
                                    val cleaned = FilenameSanitizer.sanitize(rawFilename)
                                    stagedTitle = cleaned.title
                                    stagedArtist = cleaned.artist
                                    stagedAlbum = cleaned.album
                                    stagedFileSizeMB = 18
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultSecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).testTag("parse_url_button")
                        ) {
                            Text(
                                text = "Parse",
                                color = VaultSurfaceContainerLowest,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Staged Audio Inspector
        if (hasFileStaged) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(VaultSurfaceContainerHigh)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                        .padding(16.dp)
                        .testTag("staged_audio_inspector")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Inspector Top Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(VaultTertiary)
                                )
                                Text(
                                    text = "STAGED AUDIO INSPECTOR",
                                    color = VaultOnSurface,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(VaultSurfaceContainerLowest)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$stagedFileSizeMB MB • $targetFormat",
                                    color = VaultSecondary,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Original Raw File Name Banner
                        if (rawFilename.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(VaultSurfaceContainerLowest)
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = VaultError,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "ORIGINAL RAW FILE NAME",
                                            color = VaultError,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = rawFilename,
                                        color = VaultOnSurfaceVariant,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Auto-Scrub Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(VaultSurfaceContainer)
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoFixHigh,
                                    contentDescription = null,
                                    tint = VaultPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        text = "Auto-Scrub Messy Tags & Rips",
                                        color = VaultOnSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Removes .m4a, bitrate tags, yt_dl strings & bracket noise",
                                        color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Switch(
                                checked = scrubEnabled,
                                onCheckedChange = { checked ->
                                    scrubEnabled = checked
                                    if (checked && rawFilename.isNotBlank()) {
                                        val cleaned = FilenameSanitizer.sanitize(rawFilename)
                                        stagedTitle = cleaned.title
                                        stagedArtist = cleaned.artist
                                    } else if (!checked && rawFilename.isNotBlank()) {
                                        stagedTitle = rawFilename
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = VaultSurfaceContainerLowest,
                                    checkedTrackColor = VaultPrimaryContainer,
                                    uncheckedThumbColor = VaultOutline,
                                    uncheckedTrackColor = VaultSurfaceContainerHighest
                                ),
                                modifier = Modifier.testTag("auto_scrub_switch")
                            )
                        }

                        // Custom Song Image Section
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(VaultSurfaceContainer)
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(VaultSurfaceContainerLowest)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = stagedCoverArt,
                                    contentDescription = "Cover preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(54.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Custom Song Artwork",
                                    color = VaultOnSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Button(
                                    onClick = { isCoverModalOpen = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VaultSurfaceContainerHigh
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .border(1.dp, VaultPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                        .testTag("change_image_btn")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = VaultPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Change / Upload Image",
                                            color = VaultPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Manual Title, Artist, Album text fields
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = stagedTitle,
                                onValueChange = { stagedTitle = it },
                                label = { Text("Song Title (Rename)", fontSize = 11.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VaultPrimary,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedContainerColor = VaultSurfaceContainerLowest,
                                    unfocusedContainerColor = VaultSurfaceContainerLowest,
                                    focusedTextColor = VaultOnSurface,
                                    unfocusedTextColor = VaultOnSurface
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("staged_title_input")
                            )

                            OutlinedTextField(
                                value = stagedArtist,
                                onValueChange = { stagedArtist = it },
                                label = { Text("Artist Name", fontSize = 11.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VaultPrimary,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedContainerColor = VaultSurfaceContainerLowest,
                                    unfocusedContainerColor = VaultSurfaceContainerLowest,
                                    focusedTextColor = VaultOnSurface,
                                    unfocusedTextColor = VaultOnSurface
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("staged_artist_input")
                            )

                            OutlinedTextField(
                                value = stagedAlbum,
                                onValueChange = { stagedAlbum = it },
                                label = { Text("Album / Collection", fontSize = 11.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VaultPrimary,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedContainerColor = VaultSurfaceContainerLowest,
                                    unfocusedContainerColor = VaultSurfaceContainerLowest,
                                    focusedTextColor = VaultOnSurface,
                                    unfocusedTextColor = VaultOnSurface
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("staged_album_input")
                            )
                        }

                        // Target Format and Bitrate cyclers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(VaultSurfaceContainerLowest)
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        val formats = listOf("FLAC", "WAV", "ALAC", "MP3")
                                        val nextIdx = (formats.indexOf(targetFormat) + 1) % formats.size
                                        targetFormat = formats[nextIdx]
                                    }
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "TARGET FORMAT (TAP)",
                                        color = VaultOutline,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = targetFormat,
                                        color = VaultSecondary,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(VaultSurfaceContainerLowest)
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        targetBitrate = if (targetBitrate == "BIT-EXACT 192k") "VBR HIGH RES" else "BIT-EXACT 192k"
                                    }
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "BITRATE MODE (TAP)",
                                        color = VaultOutline,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = targetBitrate,
                                        color = VaultTertiary,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Commit Button
                        val formatStr = when (targetFormat) {
                            "ALAC" -> "LOSSLESS ALAC 24b"
                            "FLAC" -> "FLAC 24/192"
                            "WAV" -> "WAV 32-bit"
                            else -> "320kbps MP3"
                        }

                        Button(
                            onClick = {
                                onAddTrackToVault(
                                    stagedTitle.ifBlank { "Imported Audio" },
                                    stagedArtist.ifBlank { "SoundVault Artist" },
                                    stagedAlbum.ifBlank { "Offline Vault" },
                                    formatStr,
                                    targetFormat,
                                    stagedFileSizeMB,
                                    stagedDurationSec,
                                    stagedCoverArt,
                                    rawFilename,
                                    stagedUri
                                )
                                saveBtnSuccess = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (saveBtnSuccess) VaultTertiary else VaultPrimary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("save_to_soundvault_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (saveBtnSuccess) Icons.Default.CheckCircle else Icons.Default.Save,
                                    contentDescription = null,
                                    tint = if (saveBtnSuccess) VaultSurfaceContainerLowest else VaultOnPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (saveBtnSuccess) "Saved to Offline Vault!" else "Save Song to SoundVault",
                                    color = if (saveBtnSuccess) VaultSurfaceContainerLowest else VaultOnPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Ingests
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recently Stored in Vault",
                    color = VaultOnSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${recentTracks.size} Total",
                    color = VaultPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (recentTracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(VaultSurfaceContainerLow)
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No songs imported yet. Choose an audio file above to add your first track.",
                        color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(recentTracks.take(5)) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(VaultSurfaceContainerLow)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VaultSurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            if (track.coverArt.isNotBlank()) {
                                AsyncImage(
                                    model = track.coverArt,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = VaultPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                color = VaultOnSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${track.artist} • ${track.format} • ${track.fileSizeMB} MB",
                                color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = VaultTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }

    // Cover Art Picker Modal for Staged Track
    CoverArtPickerModal(
        isOpen = isCoverModalOpen,
        currentArtwork = stagedCoverArt,
        songTitle = stagedTitle.ifBlank { "New Import" },
        onClose = { isCoverModalOpen = false },
        onSelectArtwork = { newArtwork ->
            stagedCoverArt = newArtwork
        }
    )
}

@Composable
private fun FormatTag(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(VaultSurfaceContainerHighest)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
