package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "Local Vault Master",
    val duration: Long = 240, // in seconds
    val durationFormatted: String = "04:00",
    val format: String = "FLAC 24/192",
    val formatType: String = "FLAC", // FLAC, WAV, MP3, ALAC, M4A, OPUS, OGG
    val fileSizeMB: Int = 25,
    val dynamicRange: String = "DR14",
    val bitrate: String = "BIT-EXACT 192k",
    val sampleRate: String = "96.0 kHz / 24-bit",
    val coverArt: String = "",
    val originalRawName: String = "",
    val audioUri: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isLossless: Boolean = true,
    val tags: String = "Local Ingest, Lossless"
)
