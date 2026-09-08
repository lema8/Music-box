package com.example.data

data class AppSettings(
    val smartSanitizer: Boolean = true,
    val defaultBitrateLossless: Boolean = true,
    val autoEmbedID3: Boolean = true,
    val bitPerfectPlayback: Boolean = true,
    val gaplessCrossfadeSec: Float = 3.5f,
    val replayGain: Boolean = false,
    val wifiOnlyIngest: Boolean = true,
    val localP2PSharing: Boolean = true
)
