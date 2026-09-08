package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SoundVaultColorScheme = darkColorScheme(
    primary = VaultPrimary,
    onPrimary = VaultOnPrimary,
    primaryContainer = VaultPrimaryContainer,
    onPrimaryContainer = VaultOnPrimaryContainer,
    secondary = VaultSecondary,
    onSecondary = VaultOnSecondary,
    secondaryContainer = VaultSecondaryContainer,
    onSecondaryContainer = VaultOnSecondaryContainer,
    tertiary = VaultTertiary,
    onTertiary = VaultOnTertiary,
    tertiaryContainer = VaultTertiaryContainer,
    background = VaultBackground,
    onBackground = VaultOnSurface,
    surface = VaultSurface,
    onSurface = VaultOnSurface,
    surfaceVariant = VaultSurfaceContainerHighest,
    onSurfaceVariant = VaultOnSurfaceVariant,
    surfaceContainerLowest = VaultSurfaceContainerLowest,
    surfaceContainerLow = VaultSurfaceContainerLow,
    surfaceContainer = VaultSurfaceContainer,
    surfaceContainerHigh = VaultSurfaceContainerHigh,
    surfaceContainerHighest = VaultSurfaceContainerHighest,
    outline = VaultOutline,
    outlineVariant = VaultOutlineVariant,
    error = VaultError,
    onError = VaultOnError,
    errorContainer = VaultErrorContainer
)

@Composable
fun SoundVaultTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SoundVaultColorScheme,
        typography = Typography,
        content = content
    )
}

// Kept for backward compatibility with template previews/tests
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    SoundVaultTheme(content = content)
}
