package com.saiful.dialme.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saiful.dialme.core.model.theme.ThemeSpec

@Immutable
data class DialMeSpacing(
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
)

internal fun spacingFor(scale: Float): DialMeSpacing = DialMeSpacing(
    xs = 4.dp * scale,
    sm = 8.dp * scale,
    md = 16.dp * scale,
    lg = 24.dp * scale,
    xl = 32.dp * scale,
)

val LocalDialMeSpacing = staticCompositionLocalOf { spacingFor(1.0f) }

/** The active [ThemeSpec], available to any composable under [DialMeTheme]. */
val LocalThemeSpec = staticCompositionLocalOf { ThemeSpec() }
