package com.saiful.dialme.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

private fun onColorFor(background: Color): Color =
    if (background.luminance() > 0.5f) Color.Black else Color.White

/**
 * Simple colour scheme derived from one seed colour. It is used when dynamic colour is off or
 * unavailable (Android 11 and older). The full tonal-palette engine arrives with the
 * customization phase.
 */
internal fun seedColorScheme(seed: Color, dark: Boolean): ColorScheme {
    return if (dark) {
        val primary = lerp(seed, Color.White, 0.35f)
        val container = lerp(seed, Color.Black, 0.45f)
        val secondary = lerp(primary, Color(0xFF9E9E9E), 0.5f)
        val secondaryContainer = lerp(secondary, Color.Black, 0.6f)
        val surface = lerp(seed, Color.Black, 0.92f)
        darkColorScheme(
            primary = primary,
            onPrimary = onColorFor(primary),
            primaryContainer = container,
            onPrimaryContainer = onColorFor(container),
            secondary = secondary,
            onSecondary = onColorFor(secondary),
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = onColorFor(secondaryContainer),
            tertiary = secondary,
            onTertiary = onColorFor(secondary),
            background = surface,
            onBackground = Color(0xFFE6E1E5),
            surface = surface,
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = lerp(surface, Color.White, 0.12f),
            onSurfaceVariant = Color(0xFFCAC4D0),
            outline = Color(0xFF938F99),
        )
    } else {
        val primary = seed
        val container = lerp(seed, Color.White, 0.80f)
        val secondary = lerp(primary, Color(0xFF757575), 0.5f)
        val secondaryContainer = lerp(secondary, Color.White, 0.8f)
        val surface = lerp(seed, Color.White, 0.97f)
        lightColorScheme(
            primary = primary,
            onPrimary = onColorFor(primary),
            primaryContainer = container,
            onPrimaryContainer = onColorFor(container),
            secondary = secondary,
            onSecondary = onColorFor(secondary),
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = onColorFor(secondaryContainer),
            tertiary = secondary,
            onTertiary = onColorFor(secondary),
            background = surface,
            onBackground = Color(0xFF1C1B1F),
            surface = surface,
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = lerp(surface, Color.Black, 0.08f),
            onSurfaceVariant = Color(0xFF49454F),
            outline = Color(0xFF79747E),
        )
    }
}

/** Pure black backgrounds for AMOLED screens. */
internal fun ColorScheme.toAmoled(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
)
