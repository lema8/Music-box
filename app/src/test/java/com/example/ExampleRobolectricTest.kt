package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.FilenameSanitizer
import com.example.util.TimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SoundVault", appName)
    }

    @Test
    fun `filename sanitizer cleans messy audio filenames`() {
        val rawName = "Tycho - Awake (Official Audio) [FLAC 24-96] [yt_dl].flac"
        val result = FilenameSanitizer.sanitize(rawName)
        assertEquals("Tycho", result.artist)
        assertEquals("Awake", result.title)
    }

    @Test
    fun `time formatter formats seconds properly`() {
        assertEquals("00:00", TimeFormatter.formatSeconds(0))
        assertEquals("03:45", TimeFormatter.formatSeconds(225))
        assertEquals("60:00", TimeFormatter.formatSeconds(3600))
    }
}
