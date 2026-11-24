package com.saiful.dialme.data

import com.saiful.dialme.core.model.theme.ThemeRepository
import com.saiful.dialme.core.model.theme.ThemeSpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Phase 1 theme storage: lives only while the app process is alive. */
@Singleton
class InMemoryThemeRepository @Inject constructor() : ThemeRepository {
    private val state = MutableStateFlow(ThemeSpec().sanitized())

    override val themeSpec: StateFlow<ThemeSpec> = state.asStateFlow()

    override fun update(transform: (ThemeSpec) -> ThemeSpec) {
        state.update { current -> transform(current).sanitized() }
    }

    override fun resetToDefault() {
        state.value = ThemeSpec().sanitized()
    }
}
