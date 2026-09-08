package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sound_bundles")
data class SoundBundleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val trackCount: Int = 0,
    val totalSizeMB: Int = 0,
    val lossless: Boolean = true,
    val coverArt: String = "",
    val tags: String = "Local Offline Package, Zero Loss",
    val trackIds: String = "", // comma-separated track IDs
    val status: String = "READY", // SYNCED, READY, BROADCASTING
    val formatLabel: String = "Lossless Audio Archive",
    val dateCreated: String = ""
)
