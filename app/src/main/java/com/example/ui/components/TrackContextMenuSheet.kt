package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultError
import com.example.ui.theme.VaultOnError
import com.example.ui.theme.VaultOnPrimary
import com.example.ui.theme.VaultOnSurface
import com.example.ui.theme.VaultOnSurfaceVariant
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSecondary
import com.example.ui.theme.VaultSurfaceContainer
import com.example.ui.theme.VaultSurfaceContainerHigh
import com.example.ui.theme.VaultSurfaceContainerLow
import com.example.ui.theme.VaultSurfaceContainerLowest
import com.example.ui.theme.VaultTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackContextMenuSheet(
    track: TrackEntity?,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onDeleteTrack: (String) -> Unit,
    onUpdateTrack: (TrackEntity) -> Unit,
    onOpenCoverArtPicker: (TrackEntity) -> Unit,
    onAddToBundle: (TrackEntity) -> Unit
) {
    if (!isOpen || track == null) return

    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }
    var editTitle by remember(track) { mutableStateOf(track.title) }
    var editArtist by remember(track) { mutableStateOf(track.artist) }
    var editAlbum by remember(track) { mutableStateOf(track.album) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VaultSurfaceContainerHigh,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(VaultSurfaceContainerLowest)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (track.coverArt.isNotBlank()) {
                            AsyncImage(
                                model = track.coverArt,
                                contentDescription = track.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(44.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = VaultPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            color = VaultOnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${track.format} • ${track.fileSizeMB} MB",
                            color = VaultSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VaultSurfaceContainerLow)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = VaultOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (isEditing) {
                // Edit Metadata Form
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Track Title", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VaultPrimary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = VaultSurfaceContainerLowest,
                            unfocusedContainerColor = VaultSurfaceContainerLowest,
                            focusedTextColor = VaultOnSurface,
                            unfocusedTextColor = VaultOnSurface
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("edit_title_input")
                    )

                    OutlinedTextField(
                        value = editArtist,
                        onValueChange = { editArtist = it },
                        label = { Text("Artist / Producer", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VaultPrimary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = VaultSurfaceContainerLowest,
                            unfocusedContainerColor = VaultSurfaceContainerLowest,
                            focusedTextColor = VaultOnSurface,
                            unfocusedTextColor = VaultOnSurface
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("edit_artist_input")
                    )

                    OutlinedTextField(
                        value = editAlbum,
                        onValueChange = { editAlbum = it },
                        label = { Text("Album Master", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VaultPrimary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = VaultSurfaceContainerLowest,
                            unfocusedContainerColor = VaultSurfaceContainerLowest,
                            focusedTextColor = VaultOnSurface,
                            unfocusedTextColor = VaultOnSurface
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("edit_album_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { isEditing = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultSurfaceContainerLow
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = VaultOnSurface, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onUpdateTrack(
                                    track.copy(
                                        title = editTitle.ifBlank { track.title },
                                        artist = editArtist.ifBlank { track.artist },
                                        album = editAlbum.ifBlank { track.album }
                                    )
                                )
                                isEditing = false
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("save_edit_button")
                        ) {
                            Text("Save Changes", color = VaultOnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Action options
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ContextMenuItem(
                        icon = Icons.Default.EditNote,
                        iconTint = VaultPrimary,
                        title = "Rename Song & Details",
                        subtitle = "Update Song Title, Artist, Album metadata",
                        testTag = "action_rename_song",
                        onClick = { isEditing = true }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.AddPhotoAlternate,
                        iconTint = VaultTertiary,
                        title = "Set Custom Song Image",
                        subtitle = "Upload image from phone or paste URL",
                        testTag = "action_custom_image",
                        onClick = {
                            onOpenCoverArtPicker(track)
                            onDismiss()
                        }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.Inventory2,
                        iconTint = VaultSecondary,
                        title = "Add to SoundBundle",
                        subtitle = "Group into portable archive for offline backup",
                        testTag = "action_add_bundle",
                        onClick = {
                            onAddToBundle(track)
                            onDismiss()
                        }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.IosShare,
                        iconTint = VaultPrimary,
                        title = "Export Audio File",
                        subtitle = "Share or backup audio file to device storage",
                        testTag = "action_export_audio",
                        onClick = {
                            if (!track.audioUri.isNullOrEmpty()) {
                                try {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_STREAM, Uri.parse(track.audioUri))
                                        type = "audio/*"
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Export ${track.title}"))
                                } catch (ignored: Exception) {}
                            }
                            onDismiss()
                        }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.DeleteSweep,
                        iconTint = VaultError,
                        title = "Delete from SoundVault",
                        subtitle = "Removes track and frees ${track.fileSizeMB} MB storage",
                        testTag = "action_delete_track",
                        textColor = VaultError,
                        onClick = {
                            onDeleteTrack(track.id)
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    testTag: String,
    textColor: Color = VaultOnSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VaultSurfaceContainer)
            .clickable { onClick() }
            .padding(12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
