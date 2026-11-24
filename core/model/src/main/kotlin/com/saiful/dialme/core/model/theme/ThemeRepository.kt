package com.saiful.dialme.core.model.theme

import kotlinx.coroutines.flow.StateFlow

/** Holds the active theme. Phase 1 keeps it in memory; persistent storage arrives with the data layer. */
interface ThemeRepository {
    val themeSpec: StateFlow<ThemeSpec>
    fun update(transform: (ThemeSpec) -> ThemeSpec)
    fun resetToDefault()
}
