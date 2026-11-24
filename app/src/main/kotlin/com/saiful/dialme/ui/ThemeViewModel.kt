package com.saiful.dialme.ui

import androidx.lifecycle.ViewModel
import com.saiful.dialme.core.model.theme.ThemeRepository
import com.saiful.dialme.core.model.theme.ThemeSpec
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val repository: ThemeRepository,
) : ViewModel() {

    val themeSpec: StateFlow<ThemeSpec> = repository.themeSpec

    fun update(transform: (ThemeSpec) -> ThemeSpec) = repository.update(transform)

    fun reset() = repository.resetToDefault()
}
