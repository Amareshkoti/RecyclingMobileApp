package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val PlayfulDarkColorScheme = darkColorScheme(
    primary = EcoGreenPrimary,
    secondary = SkyBlueSecondary,
    tertiary = SolarYellowTertiary,
    background = CharcoalText,
    surface = CharcoalText,
    onPrimary = KidsWhite,
    onSecondary = CharcoalText,
    onTertiary = CharcoalText,
    onBackground = KidsWhite,
    onSurface = KidsWhite
)

private val PlayfulLightColorScheme = lightColorScheme(
    primary = EcoGreenDark,
    secondary = SkyBlueSecondary,
    tertiary = SolarYellowTertiary,
    background = KidsBackground,
    surface = KidsWhite,
    onPrimary = KidsWhite,
    onSecondary = KidsWhite,
    onTertiary = CharcoalText,
    onBackground = CharcoalText,
    onSurface = CharcoalText,
    outline = SoftGrayOutline,
    surfaceVariant = EcoGreenLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep dynamicColor false to enforce our customized beautiful, leaf-green branding across all devices
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        PlayfulDarkColorScheme
    } else {
        PlayfulLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
