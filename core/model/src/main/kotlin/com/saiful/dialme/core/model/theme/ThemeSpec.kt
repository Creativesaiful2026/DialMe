package com.saiful.dialme.core.model.theme

import kotlinx.serialization.Serializable

/** Light/dark behaviour of the app. */
@Serializable
enum class ThemeMode { SYSTEM, LIGHT, DARK, AMOLED }

/** Shape family used by buttons (dialpad keys, call buttons, ...). */
@Serializable
enum class ButtonShape { ROUND, ROUNDED_SQUARE, SQUARE }

/**
 * Serializable description of the whole app look. This is the single source of truth for
 * customization. Presets and user themes are stored and shared as JSON of this class.
 *
 * Every numeric value is clamped by [sanitized], so an imported file can never break the UI.
 */
@Serializable
data class ThemeSpec(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val name: String = DEFAULT_NAME,
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = true,
    /** Opaque ARGB colour, used when dynamic colour is off or unavailable. */
    val seedColor: Long = DEFAULT_SEED_COLOR,
    val cornerRadiusDp: Int = DEFAULT_CORNER_RADIUS_DP,
    val spacingScale: Float = 1.0f,
    val fontScale: Float = 1.0f,
    val animationSpeed: Float = 1.0f,
    val buttonShape: ButtonShape = ButtonShape.ROUND,
) {
    /** Returns a copy with every value forced into its allowed range. */
    fun sanitized(): ThemeSpec = copy(
        name = name.trim().take(MAX_NAME_LENGTH).ifEmpty { DEFAULT_NAME },
        seedColor = (seedColor and 0xFFFFFFFFL) or 0xFF000000L,
        cornerRadiusDp = cornerRadiusDp.coerceIn(MIN_CORNER_RADIUS_DP, MAX_CORNER_RADIUS_DP),
        spacingScale = spacingScale.clampOr(MIN_SCALE, MAX_SCALE, 1.0f),
        fontScale = fontScale.clampOr(MIN_FONT_SCALE, MAX_FONT_SCALE, 1.0f),
        animationSpeed = animationSpeed.clampOr(MIN_ANIMATION_SPEED, MAX_ANIMATION_SPEED, 1.0f),
    )

    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
        const val DEFAULT_NAME = "Default"
        const val MAX_NAME_LENGTH = 40
        const val DEFAULT_SEED_COLOR = 0xFF1B6B4FL
        const val DEFAULT_CORNER_RADIUS_DP = 16
        const val MIN_CORNER_RADIUS_DP = 0
        const val MAX_CORNER_RADIUS_DP = 32
        const val MIN_SCALE = 0.5f
        const val MAX_SCALE = 2.0f
        const val MIN_FONT_SCALE = 0.8f
        const val MAX_FONT_SCALE = 1.5f
        const val MIN_ANIMATION_SPEED = 0.25f
        const val MAX_ANIMATION_SPEED = 3.0f
    }
}

private fun Float.clampOr(min: Float, max: Float, fallback: Float): Float =
    if (isNaN() || isInfinite()) fallback else coerceIn(min, max)
