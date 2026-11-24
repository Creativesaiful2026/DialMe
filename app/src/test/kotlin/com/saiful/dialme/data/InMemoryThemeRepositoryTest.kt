package com.saiful.dialme.data

import com.saiful.dialme.core.model.theme.ThemeMode
import com.saiful.dialme.core.model.theme.ThemeSpec
import org.junit.Assert.assertEquals
import org.junit.Test

class InMemoryThemeRepositoryTest {

    @Test
    fun startsWithDefaultTheme() {
        val repo = InMemoryThemeRepository()
        assertEquals(ThemeSpec().sanitized(), repo.themeSpec.value)
    }

    @Test
    fun update_appliesChange() {
        val repo = InMemoryThemeRepository()
        repo.update { it.copy(mode = ThemeMode.DARK) }
        assertEquals(ThemeMode.DARK, repo.themeSpec.value.mode)
    }

    @Test
    fun update_clampsInvalidValues() {
        val repo = InMemoryThemeRepository()
        repo.update { it.copy(cornerRadiusDp = 999) }
        assertEquals(ThemeSpec.MAX_CORNER_RADIUS_DP, repo.themeSpec.value.cornerRadiusDp)
    }

    @Test
    fun resetToDefault_restoresDefaults() {
        val repo = InMemoryThemeRepository()
        repo.update { it.copy(mode = ThemeMode.AMOLED, useDynamicColor = false) }
        repo.resetToDefault()
        assertEquals(ThemeSpec().sanitized(), repo.themeSpec.value)
    }
}
