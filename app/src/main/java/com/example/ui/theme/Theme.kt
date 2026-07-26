package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EnochianDarkColorScheme = darkColorScheme(
    primary = EnochianLavender,
    onPrimary = EnochianHeroContainer,
    primaryContainer = EnochianHeroContainer,
    onPrimaryContainer = EnochianLavender,
    secondary = EnochianGold,
    onSecondary = EnochianBackground,
    secondaryContainer = EnochianSurfaceVariant,
    onSecondaryContainer = OnEnochianSurface,
    tertiary = CelestialCyan,
    background = EnochianBackground,
    onBackground = OnEnochianSurface,
    surface = EnochianSurface,
    onSurface = OnEnochianSurface,
    surfaceVariant = EnochianSurfaceVariant,
    onSurfaceVariant = OnEnochianMuted,
    outline = EnochianBorder
)

@Composable
fun EnochianMagicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EnochianDarkColorScheme,
        typography = Typography,
        content = content
    )
}

