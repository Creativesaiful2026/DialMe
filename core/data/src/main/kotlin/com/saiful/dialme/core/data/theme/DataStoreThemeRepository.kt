package com.saiful.dialme.core.data.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.saiful.dialme.core.common.logging.DialMeLog
import com.saiful.dialme.core.model.theme.ThemeCodec
import com.saiful.dialme.core.model.theme.ThemeDecodeResult
import com.saiful.dialme.core.model.theme.ThemeRepository
import com.saiful.dialme.core.model.theme.ThemeSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "dialme_theme")

/**
 * Keeps the active theme in memory and saves it to the device, so the chosen look survives an app
 * restart. The saved value is read once at start-up to avoid a flash of the default theme.
 */
class DataStoreThemeRepository(context: Context) : ThemeRepository {
    private val store: DataStore<Preferences> = context.applicationContext.themeDataStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val state = MutableStateFlow(loadInitial())

    override val themeSpec: StateFlow<ThemeSpec> = state.asStateFlow()

    init {
        scope.launch {
            // The first value is the one just loaded, so it is skipped. Rapid changes (a slider
            // being dragged) are merged: only the latest value is written after a short pause.
            state.drop(1).collectLatest { spec ->
                delay(SAVE_DELAY_MS)
                try {
                    store.edit { prefs -> prefs[KEY_THEME_JSON] = ThemeCodec.encode(spec) }
                } catch (e: Exception) {
                    DialMeLog.w(TAG, "Could not save the theme", e)
                }
            }
        }
    }

    override fun update(transform: (ThemeSpec) -> ThemeSpec) {
        state.update { current -> transform(current).sanitized() }
    }

    override fun resetToDefault() {
        state.value = ThemeSpec().sanitized()
    }

    private fun loadInitial(): ThemeSpec {
        val saved = try {
            runBlocking(Dispatchers.IO) { store.data.first()[KEY_THEME_JSON] }
        } catch (e: Exception) {
            DialMeLog.w(TAG, "Could not read the saved theme", e)
            null
        }
        if (saved == null) return ThemeSpec().sanitized()
        return when (val result = ThemeCodec.decode(saved)) {
            is ThemeDecodeResult.Success -> result.spec
            is ThemeDecodeResult.Failure -> {
                DialMeLog.w(TAG, "Saved theme was invalid (${result.error}); using the default")
                ThemeSpec().sanitized()
            }
        }
    }

    private companion object {
        const val TAG = "ThemeStore"
        const val SAVE_DELAY_MS = 300L
        val KEY_THEME_JSON = stringPreferencesKey("theme_spec_json")
    }
}
