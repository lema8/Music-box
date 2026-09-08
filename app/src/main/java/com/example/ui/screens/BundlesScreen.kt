package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.SoundBundleEntity
import com.example.data.TrackEntity
import com.example.ui.components.CreateBundleModal
import com.example.ui.components.QrSyncModal
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultOnPrimary
import com.example.ui.theme.VaultOnSurface
import com.example.ui.theme.VaultOnSurfaceVariant
import com.example.ui.theme.VaultOutline
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSecondary
import com.example.ui.theme.VaultSurfaceContainer
import com.example.ui.theme.VaultSurfaceContainerHigh
import com.example.ui.theme.VaultSurfaceContainerLow
import com.example.ui.theme.VaultSurfaceContainerLowest
import com.example.ui.theme.VaultTertiary

@Composable
fun BundlesScreen(
    bundles: List<SoundBundleEntity>,
    vaultTracks: List<TrackEntity>,
    onOpenQrSync: (SoundBundleEntity) -> Unit,
    onOpenCreateBundle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf("p2p") } // "p2p" or "zip"

    val activeBundle = bundles.firstOrNull() ?: SoundBundleEntity(
        id = "bundle-synth-8893",
        title = "Late Night Synthwave Vault",
        trackCount = 12,
        totalSizeMB = 680,
        lossless = true,
        coverArt = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?q=80&w=800&auto=format&fit=crop",
        tags = "Full Dynamic Masters, 3000px Covers & Cues, P2P Verified Hash",
        trackIds = "",
        status = "SYNCED",
        formatLabel = "680 MB Lossless Audio",
        dateCreated = "2026-09-02"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "radarPulse")
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "radarAlpha"
    )

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
                        imageVector = Icons.Default.Archive,
                        contentDescription = null,
                        tint = VaultPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "SoundBundles",
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
                        .border(1.dp, VaultSecondary.copy(alpha = 0.25f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "v2.4 P2P SYNC",
                        color = VaultSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = "Export & share lossless offline packages with zero quality loss.",
                color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        item {
            // Hero Action: Create New Bundle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(VaultPrimary, VaultSecondary, VaultTertiary)
                        )
                    )
                    .padding(1.5.dp)
                    .clickable { onOpenCreateBundle() }
                    .testTag("create_bundle_hero_cta")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.5.dp))
                        .background(VaultSurfaceContainerHigh)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(VaultPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = VaultOnPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "+ Create New Bundle",
                                color = VaultOnSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pack tracks, metadata & hires art into .soundvault",
                                color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        tint = VaultPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        item {
            // Active Bundle Showcase Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(VaultSurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
                    .padding(16.dp)
                    .testTag("active_bundle_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Title & Art
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(VaultSurfaceContainerLowest)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = activeBundle.coverArt,
                                    contentDescription = activeBundle.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE BUNDLE",
                                        color = VaultTertiary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(VaultTertiary)
                                    )
                                }
                                Text(
                                    text = activeBundle.title,
                                    color = VaultOnSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${activeBundle.trackCount} Tracks • ${activeBundle.totalSizeMB} MB Lossless Audio",
                                    color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { onOpenQrSync(activeBundle) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = VaultOnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Metadata Micro Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BundleMicroChip(Icons.Default.Album, "Full Dynamic", VaultSecondary)
                        BundleMicroChip(Icons.Default.Image, "3000px Art", VaultOnSurfaceVariant)
                        BundleMicroChip(Icons.Default.Lock, "P2P Hash", VaultTertiary)
                    }

                    // Sharing Topology & Format Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "SHARING TOPOLOGY & FORMAT",
                            color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TopologyCard(
                                title = "P2P AirDrop / Wi-Fi",
                                subtitle = "Direct local network stream",
                                icon = Icons.Default.Sensors,
                                isSelected = selectedFormat == "p2p",
                                tint = VaultSecondary,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedFormat = "p2p" }
                            )

                            TopologyCard(
                                title = ".soundvault File",
                                subtitle = "Encrypted single archive",
                                icon = Icons.Default.Inventory2,
                                isSelected = selectedFormat == "zip",
                                tint = VaultPrimary,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedFormat = "zip" }
                            )
                        }
                    }

                    // Card Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onOpenQrSync(activeBundle) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("qr_sync_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    tint = VaultOnPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("QR Sync / Link", color = VaultOnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "SoundVault Archive: ${activeBundle.title}\nFormat: Lossless BIT-PERFECT\nSize: ${activeBundle.totalSizeMB} MB")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export ${activeBundle.title}"))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultSurfaceContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .testTag("export_file_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DownloadForOffline,
                                    contentDescription = null,
                                    tint = VaultSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("Export File", color = VaultOnSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        item {
            // Peer Discovery Active Radar Widget
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VaultSurfaceContainerLow)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(VaultSurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = null,
                            tint = VaultTertiary.copy(alpha = radarAlpha),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "3 Local Peers Nearby",
                            color = VaultOnSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ready to receive bundles via Zero-Loss LAN",
                            color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        Toast.makeText(context, "Broadcasting presence to 5 GHz Wi-Fi Direct mesh...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VaultSurfaceContainerHigh
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.border(1.dp, VaultTertiary.copy(alpha = 0.3f), RoundedCornerShape(50))
                ) {
                    Text(
                        text = "BROADCAST",
                        color = VaultTertiary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            // Saved Bundles Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Saved Bundles",
                    color = VaultOnSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${bundles.size} Bundles",
                    color = VaultPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(bundles, key = { it.id }) { bundle ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VaultSurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            ) {
                                AsyncImage(
                                    model = bundle.coverArt,
                                    contentDescription = bundle.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bundle.title,
                                    color = VaultOnSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${bundle.trackCount} songs • ${bundle.totalSizeMB} MB • ${if (bundle.lossless) "FLAC" else "Mastered"}",
                                    color = VaultOnSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { onOpenQrSync(bundle) },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VaultSurfaceContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = VaultSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Self-Contained Library File",
                            color = VaultOnSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (bundle.status == "SYNCED") VaultTertiary.copy(alpha = 0.2f) else VaultSurfaceContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = bundle.status,
                                color = if (bundle.status == "SYNCED") VaultTertiary else VaultOnSurfaceVariant,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun BundleMicroChip(icon: ImageVector, text: String, tint: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(VaultSurfaceContainerLowest)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(12.dp))
        Text(text = text, color = VaultOnSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun TopologyCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) VaultSurfaceContainerLowest else VaultSurfaceContainerLow)
            .border(
                width = 1.dp,
                color = if (isSelected) tint.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            if (isSelected) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(tint))
            }
        }
        Text(text = title, color = VaultOnSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = VaultOnSurfaceVariant.copy(alpha = 0.7f), fontSize = 10.sp, lineHeight = 13.sp)
    }
}
