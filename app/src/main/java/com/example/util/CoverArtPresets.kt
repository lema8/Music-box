package com.example.util

data class CoverPreset(
    val id: String,
    val name: String,
    val url: String
)

object CoverArtPresets {
    val presets = listOf(
        CoverPreset(
            id = "preset-cyber",
            name = "Cyber Synth",
            url = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?q=80&w=800&auto=format&fit=crop"
        ),
        CoverPreset(
            id = "preset-vinyl",
            name = "Vintage Vinyl",
            url = "https://images.unsplash.com/photo-1539185441755-769473a23570?q=80&w=800&auto=format&fit=crop"
        ),
        CoverPreset(
            id = "preset-cosmic",
            name = "Deep Nebula",
            url = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=800&auto=format&fit=crop"
        ),
        CoverPreset(
            id = "preset-acoustic",
            name = "Acoustic Warmth",
            url = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=800&auto=format&fit=crop"
        ),
        CoverPreset(
            id = "preset-ambient",
            name = "Solar Drift",
            url = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?q=80&w=800&auto=format&fit=crop"
        ),
        CoverPreset(
            id = "preset-tape",
            name = "Analog Master",
            url = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=800&auto=format&fit=crop"
        )
    )

    val defaultCover: String
        get() = presets[0].url
}
