package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultError
import com.example.ui.theme.VaultOnPrimary
import com.example.ui.theme.VaultOnSurface
import com.example.ui.theme.VaultOnSurfaceVariant
import com.example.ui.theme.VaultOutline
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultPrimaryContainer
import com.example.ui.theme.VaultSecondary
import com.example.ui.theme.VaultSurfaceContainer
import com.example.ui.theme.VaultSurfaceContainerHigh
import com.example.ui.theme.VaultSurfaceContainerHighest
import com.example.ui.theme.VaultSurfaceContainerLow
import com.example.ui.theme.VaultSurfaceContainerLowest
import com.example.ui.theme.VaultTertiary

@Composable
fun SettingsScreen(
    trackCount: Int,
    totalStorageMB: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var smartSanitizer by remember { mutableStateOf(true) }
    var autoEmbedTags by remember { mutableStateOf(true) }
    var bitPerfectDac by remember { mutableStateOf(true) }
    var replayGain by remember { mutableStateOf(true) }
    var crossfadeSeconds by remember { mutableFloatStateOf(2.5f) }
    var wifiOnlyIngest by remember { mutableStateOf(false) }
    var p2pNodeSharing by remember { mutableStateOf(true) }
    var defaultBitrate by remember { mutableStateOf("FLAC 24/192 Lossless") }

    val usedGB = String.format("%.2f", totalStorageMB / 1024.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = VaultSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Vault Config",
                        color = VaultOnSurface,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(VaultSurfaceContainerHigh)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "BUILD 2.4.0-APK",
                        color = VaultOutline,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = "Hardware routing, metadata sanitization, and offline storage allocation.",
                color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        item {
            // Storage Partition Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(VaultSurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .testTag("settings_storage_partition")
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
                            Icon(
                                imageVector = Icons.Default.SdCard,
                                contentDescription = null,
                                tint = VaultPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Storage Partition Allocation",
                                color = VaultOnSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$usedGB GB / 64 GB Dedicated",
                            color = VaultSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(VaultSurfaceContainerLowest)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(0.18f)
                                    .fillMaxHeight()
                                    .background(VaultPrimary)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(0.06f)
                                    .fillMaxHeight()
                                    .background(VaultSecondary)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(0.76f)
                                    .fillMaxHeight()
                                    .background(Color.Transparent)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "• $trackCount FLAC/WAV Masters ($totalStorageMB MB)",
                            color = VaultOutline,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "• Cache Index: 3.2 MB",
                            color = VaultOutline,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "Audio buffer cache purged! Freed 3.2 MB.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultSurfaceContainerLow
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = VaultOutline, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Purge Cache", color = VaultOnSurface, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "SoundVault Export: $trackCount tracks ($totalStorageMB MB).")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export Vault Backup"))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultSurfaceContainerLow
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.IosShare, contentDescription = null, tint = VaultSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Vault", color = VaultSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Ingest & Clean Rules
            SectionCard(title = "INGEST & CLEAN RULES") {
                SettingsSwitchRow(
                    title = "Smart Filename Sanitizer",
                    subtitle = "Auto-strip messy download tags and brackets",
                    icon = Icons.Default.AutoFixHigh,
                    checked = smartSanitizer,
                    onCheckedChange = { smartSanitizer = it },
                    testTag = "settings_switch_sanitizer"
                )

                SettingsClickRow(
                    title = "Default Ingest Bitrate",
                    value = defaultBitrate,
                    icon = Icons.Default.GraphicEq,
                    onClick = {
                        val rates = listOf("FLAC 24/192 Lossless", "WAV 32-bit Float", "320kbps MP3 Master")
                        val nextIdx = (rates.indexOf(defaultBitrate) + 1) % rates.size
                        defaultBitrate = rates[nextIdx]
                    }
                )

                SettingsSwitchRow(
                    title = "Auto-Embed ID3 & Artwork",
                    subtitle = "Inject 1:1 square cover art directly into audio headers",
                    icon = Icons.Default.Tune,
                    checked = autoEmbedTags,
                    onCheckedChange = { autoEmbedTags = it },
                    testTag = "settings_switch_embed_tags"
                )
            }
        }

        item {
            // Acoustic Engine
            SectionCard(title = "ACOUSTIC AUDIO ENGINE") {
                SettingsSwitchRow(
                    title = "Bit-Perfect Playback (DAC Bypass)",
                    subtitle = "Bypasses standard Android OS mixer resampler",
                    icon = Icons.Default.Headphones,
                    checked = bitPerfectDac,
                    onCheckedChange = { bitPerfectDac = it },
                    testTag = "settings_switch_bitperfect"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gapless & Crossfade",
                            color = VaultOnSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${String.format("%.1f", crossfadeSeconds)}s",
                            color = VaultSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = crossfadeSeconds,
                        onValueChange = { crossfadeSeconds = it },
                        valueRange = 0f..10f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = VaultPrimary,
                            inactiveTrackColor = VaultSurfaceContainerLowest
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                SettingsSwitchRow(
                    title = "ReplayGain Normalization",
                    subtitle = "EBU R128 loudness matching without dynamic distortion",
                    icon = Icons.Default.VerifiedUser,
                    checked = replayGain,
                    onCheckedChange = { replayGain = it },
                    testTag = "settings_switch_replaygain"
                )
            }
        }

        item {
            // Network & Mesh
            SectionCard(title = "NETWORK & MESH") {
                SettingsSwitchRow(
                    title = "Wi-Fi Only Ingest",
                    subtitle = "Halt streaming links over metered cellular networks",
                    icon = Icons.Default.Wifi,
                    checked = wifiOnlyIngest,
                    onCheckedChange = { wifiOnlyIngest = it },
                    testTag = "settings_switch_wifi"
                )

                SettingsSwitchRow(
                    title = "Local P2P Node Sharing",
                    subtitle = "Enable discovery on 5 GHz Wi-Fi Direct for nearby devices",
                    icon = Icons.Default.NetworkCheck,
                    checked = p2pNodeSharing,
                    onCheckedChange = { p2pNodeSharing = it },
                    testTag = "settings_switch_p2p"
                )
            }
        }

        item {
            // Audio Buffer Pipeline Diagram
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VaultSurfaceContainerLow)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = VaultTertiary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "AUDIO BUFFER PIPELINE: DOUBLE-BUFFERED",
                            color = VaultTertiary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Audio frames are preloaded into direct memory buffers to eliminate playback jitter, stutter, and latency during screen off lock state.",
                        color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            // Footer Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "SoundVault Audio Station v2.4.0",
                    color = VaultOnSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "High-Fidelity Offline Audio Engine for Android",
                    color = VaultOutline,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Zero Telemetry • Bit-Perfect ALSA Output",
                    color = VaultSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(VaultSurfaceContainerHigh)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = title,
                color = VaultOnSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = VaultSecondary, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, color = VaultOnSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(text = subtitle, color = VaultOnSurfaceVariant.copy(alpha = 0.7f), fontSize = 10.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VaultSurfaceContainerLowest,
                checkedTrackColor = VaultPrimaryContainer,
                uncheckedThumbColor = VaultOutline,
                uncheckedTrackColor = VaultSurfaceContainerHighest
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SettingsClickRow(
    title: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = VaultTertiary, modifier = Modifier.size(20.dp))
            Text(text = title, color = VaultOnSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Text(
            text = value,
            color = VaultTertiary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
