package com.example.util

import java.util.Locale

object TimeFormatter {
    fun formatSeconds(seconds: Long): String {
        if (seconds < 0) return "00:00"
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    fun formatMillis(millis: Long): String {
        return formatSeconds(millis / 1000)
    }
}
