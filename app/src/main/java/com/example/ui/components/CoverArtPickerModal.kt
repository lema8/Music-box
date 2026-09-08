package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.util.CoverArtPresets
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultOnPrimary
import com.example.ui.theme.VaultOnSurface
import com.example.ui.theme.VaultOnSurfaceVariant
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSecondary
import com.example.ui.theme.VaultSurfaceContainerHigh
import com.example.ui.theme.VaultSurfaceContainerLow
import com.example.ui.theme.VaultSurfaceContainerLowest
import com.example.ui.theme.VaultTertiary

@Composable
fun CoverArtPickerModal(
    isOpen: Boolean,
    currentArtwork: String,
    songTitle: String,
    onClose: () -> Unit,
    onSelectArtwork: (String) -> Unit
) {
    if (!isOpen) return

    var selectedUrl by remember(currentArtwork) {
        mutableStateOf(currentArtwork.ifEmpty { CoverArtPresets.defaultCover })
    }
    var urlInput by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUrl = it.toString()
        }
    }

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(VaultSurfaceContainerHigh)
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                .padding(18.dp)
                .testTag("cover_art_picker_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VaultPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = VaultPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Set Custom Cover Art",
                                color = VaultOnSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = songTitle,
                                color = VaultSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
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

                // Live Preview
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(VaultSurfaceContainerLowest)
                            .border(2.dp, VaultPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedUrl.isNotBlank()) {
                            AsyncImage(
                                model = selectedUrl,
                                contentDescription = "Cover Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(160.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = VaultOnSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1:1 SQUARE ASPECT RATIO • BIT-EXACT HI-RES",
                        color = VaultTertiary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Method 1: Choose from device gallery
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "METHOD 1: UPLOAD FROM PHONE (GALLERY / DOWNLOADS)",
                        color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VaultSurfaceContainerLow
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .testTag("pick_cover_from_gallery")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = VaultTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Choose Image File from Phone",
                                color = VaultOnSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Method 2: Paste URL
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "METHOD 2: PASTE DOWNLOADED / ONLINE IMAGE URL",
                        color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            placeholder = {
                                Text(
                                    "https://.../cover.jpg",
                                    fontSize = 11.sp,
                                    color = VaultOnSurfaceVariant.copy(alpha = 0.5f)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VaultPrimary,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedContainerColor = VaultSurfaceContainerLow,
                                unfocusedContainerColor = VaultSurfaceContainerLow,
                                focusedTextColor = VaultOnSurface,
                                unfocusedTextColor = VaultOnSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("cover_url_input")
                        )
                        Button(
                            onClick = {
                                if (urlInput.isNotBlank()) {
                                    selectedUrl = urlInput.trim()
                                    urlInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultSurfaceContainerLow
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.border(1.dp, VaultSecondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        ) {
                            Text("Apply", color = VaultSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Method 3: Instant presets
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "METHOD 3: CHOOSE INSTANT ART PRESET",
                        color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CoverArtPresets.presets.take(3).forEach { preset ->
                            val isSel = selectedUrl == preset.url
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VaultSurfaceContainerLow)
                                    .border(
                                        width = if (isSel) 2.dp else 1.dp,
                                        color = if (isSel) VaultPrimary else Color.White.copy(alpha = 0.06f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedUrl = preset.url }
                                    .padding(4.dp)
                            ) {
                                AsyncImage(
                                    model = preset.url,
                                    contentDescription = preset.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.name,
                                    color = if (isSel) VaultPrimary else VaultOnSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onClose,
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
                            onSelectArtwork(selectedUrl)
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VaultPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_cover_art_button")
                    ) {
                        Text("Save Art", color = VaultOnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
