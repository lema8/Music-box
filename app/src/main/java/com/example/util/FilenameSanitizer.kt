package com.example.util

import java.util.Locale

data class SanitizedAudioInfo(
    val title: String,
    val artist: String,
    val album: String = "Offline Vault Master"
)

object FilenameSanitizer {
    /**
     * Intelligent Smart Filename Cleaner
     * Removes .m4a, bitrate tags, ripped IDs, track counters, brackets, yt_dl strings
     */
    fun sanitize(filename: String): SanitizedAudioInfo {
        // Strip file extension
        var clean = filename.replace(Regex("\\.(mp3|wav|flac|alac|m4a|aac|ogg|opus|aiff)$", RegexOption.IGNORE_CASE), "")

        // Strip bracket noise, yt_dl tags, rip IDs, bitrate suffixes
        clean = clean.replace(Regex("\\[.*?\\]|\\(.*?\\)"), " ")
        clean = clean.replace(Regex("yt_dl_audio_stream[a-zA-Z0-9_-]*", RegexOption.IGNORE_CASE), "")
        clean = clean.replace(Regex("(_1080p_rip|_720p|_320kbps|_hq|_master|_final|_v\\d+)", RegexOption.IGNORE_CASE), "")
        clean = clean.replace(Regex("official video|music video|audio rip|lyric video|remastered|explicit", RegexOption.IGNORE_CASE), "")

        // Replace underscores with space
        clean = clean.replace("_", " ")

        var artist = "SoundVault Artist"
        var title = clean
        val album = "Local Vault Master"

        if (clean.contains(" - ")) {
            val parts = clean.split(" - ")
            if (parts.size >= 2) {
                artist = parts[0].trim()
                title = parts.drop(1).joinToString(" - ").trim()
            }
        } else if (clean.contains(" by ", ignoreCase = true)) {
            val parts = clean.split(Regex(" by ", RegexOption.IGNORE_CASE))
            if (parts.size >= 2) {
                title = parts[0].trim()
                artist = parts[1].trim()
            }
        }

        // Clean any extraneous repeated dashes or whitespace in artist / title
        artist = artist.replace(Regex("\\s+"), " ").trim()
        title = title.replace(Regex("\\s+"), " ").trim()

        fun toTitleCase(str: String): String {
            return str.split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                }
        }

        val cleanTitle = toTitleCase(title).ifEmpty { "Imported Audio" }
        val cleanArtist = toTitleCase(artist).ifEmpty { "SoundVault Artist" }

        return SanitizedAudioInfo(
            title = cleanTitle,
            artist = cleanArtist,
            album = album
        )
    }
}
