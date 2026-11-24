package com.saiful.dialme.core.model.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeSpecTest {

    @Test
    fun sanitized_clampsOutOfRangeValues() {
        val result = ThemeSpec(
            cornerRadiusDp = 999,
            spacingScale = 10f,
            fontScale = 0f,
            animationSpeed = 100f,
        ).sanitized()

        assertEquals(ThemeSpec.MAX_CORNER_RADIUS_DP, result.cornerRadiusDp)
        assertEquals(ThemeSpec.MAX_SCALE, result.spacingScale, 0.0001f)
        assertEquals(ThemeSpec.MIN_FONT_SCALE, result.fontScale, 0.0001f)
        assertEquals(ThemeSpec.MAX_ANIMATION_SPEED, result.animationSpeed, 0.0001f)
    }

    @Test
    fun sanitized_replacesNaNWithDefault() {
        val result = ThemeSpec(spacingScale = Float.NaN).sanitized()
        assertEquals(1.0f, result.spacingScale, 0.0001f)
    }

    @Test
    fun sanitized_forcesOpaqueSeedColor() {
        val result = ThemeSpec(seedColor = 0x00123456L).sanitized()
        assertEquals(0xFF123456L, result.seedColor)
    }

    @Test
    fun sanitized_blankNameBecomesDefault() {
        assertEquals(ThemeSpec.DEFAULT_NAME, ThemeSpec(name = "   ").sanitized().name)
    }

    @Test
    fun sanitized_truncatesLongName() {
        val result = ThemeSpec(name = "x".repeat(500)).sanitized()
        assertEquals(ThemeSpec.MAX_NAME_LENGTH, result.name.length)
    }
}
