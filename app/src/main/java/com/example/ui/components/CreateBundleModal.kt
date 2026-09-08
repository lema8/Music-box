package com.example.ui.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.TrackEntity
import com.example.ui.theme.VaultOnPrimary
import com.example.ui.theme.VaultOnSurface
import com.example.ui.theme.VaultOnSurfaceVariant
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSecondary
import com.example.ui.theme.VaultSurfaceContainer
import com.example.ui.theme.VaultSurfaceContainerHigh
import com.example.ui.theme.VaultSurfaceContainerLow
import com.example.ui.theme.VaultSurfaceContainerLowest

@Composable
fun CreateBundleModal(
    isOpen: Boolean,
    tracks: List<TrackEntity>,
    onClose: () -> Unit,
    onCreateBundle: (title: String, selectedTrackIds: List<String>) -> Unit
) {
    if (!isOpen) return

    var bundleTitle by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf<String>() }

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(VaultSurfaceContainerHigh)
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("create_bundle_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
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
                                imageVector = Icons.Default.Archive,
                                contentDescription = null,
                                tint = VaultPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Bundle Packager",
                            color = VaultOnSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VaultSurfaceContainerLow)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = VaultOnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Title Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "BUNDLE NAME",
                        color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = bundleTitle,
                        onValueChange = { bundleTitle = it },
                        placeholder = { Text("e.g. Midnight Roadtrip Vault", fontSize = 12.sp, color = VaultOnSurfaceVariant.copy(alpha = 0.5f)) },
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
                        modifier = Modifier.fillMaxWidth().testTag("bundle_title_input")
                    )
                }

                // Track selection list
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SELECT TRACKS (${selectedIds.size} SELECTED)",
                        color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    if (tracks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VaultSurfaceContainerLow)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tracks in Vault to package yet",
                                color = VaultOnSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 180.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(tracks) { track ->
                                val isChecked = selectedIds.contains(track.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isChecked) VaultSurfaceContainer else VaultSurfaceContainerLow)
                                        .border(
                                            width = 1.dp,
                                            color = if (isChecked) VaultPrimary.copy(alpha = 0.4f) else Color.Transparent,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            if (isChecked) {
                                                selectedIds.remove(track.id)
                                            } else {
                                                selectedIds.add(track.id)
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                            contentDescription = null,
                                            tint = if (isChecked) VaultPrimary else VaultOnSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = track.title,
                                            color = VaultOnSurface,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = track.format,
                                        color = VaultSecondary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
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
                            if (bundleTitle.isNotBlank()) {
                                onCreateBundle(bundleTitle, selectedIds.toList())
                            }
                        },
                        enabled = bundleTitle.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VaultPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_create_bundle")
                    ) {
                        Text("Create Bundle", color = VaultOnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
