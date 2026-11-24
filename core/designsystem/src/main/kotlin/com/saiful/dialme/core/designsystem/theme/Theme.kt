package com.saiful.dialme.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.saiful.dialme.core.model.theme.ThemeMode
import com.saiful.dialme.core.model.theme.ThemeSpec

/** True when [ThemeSpec.mode] resolves to a dark appearance right now. */
@Composable
fun ThemeSpec.isDark(): Boolean {
    val systemDark = isSystemInDarkTheme()
    return when (mode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }
}

/** Applies [spec] to everything inside [content]. */
@Composable
fun DialMeTheme(
    spec: ThemeSpec,
    content: @Composable () -> Unit,
) {
    val dark = spec.isDark()
    val context = LocalContext.current

    val baseScheme = if (spec.useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        seedColorScheme(Color(spec.seedColor), dark)
    }
    val colorScheme = if (spec.mode == ThemeMode.AMOLED) baseScheme.toAmoled() else baseScheme

    CompositionLocalProvider(
        LocalThemeSpec provides spec,
        LocalDialMeSpacing provides spacingFor(spec.spacingScale),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography(spec.fontScale),
            shapes = shapesFor(spec.cornerRadiusDp),
            content = content,
        )
    }
}
