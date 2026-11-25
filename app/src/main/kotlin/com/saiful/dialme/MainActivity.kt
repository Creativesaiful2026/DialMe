package com.saiful.dialme

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saiful.dialme.core.common.logging.DialMeLog
import com.saiful.dialme.core.designsystem.theme.DialMeTheme
import com.saiful.dialme.core.designsystem.theme.isDark
import com.saiful.dialme.ui.DialMeApp
import com.saiful.dialme.ui.DialRequest
import com.saiful.dialme.ui.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    private var dialRequest by mutableStateOf<DialRequest?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            handleDialIntent(intent)
        }
        setContent {
            val spec by themeViewModel.themeSpec.collectAsStateWithLifecycle()
            val dark = spec.isDark()

            // Keep status/navigation bar icon colours in sync with the chosen theme mode.
            DisposableEffect(dark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                )
                onDispose { }
            }

            DialMeTheme(spec) {
                DialMeApp(
                    themeSpec = spec,
                    onThemeChange = themeViewModel::update,
                    onThemeReset = themeViewModel::reset,
                    dialRequest = dialRequest,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDialIntent(intent)
    }

    /** Handles tel: links and ACTION_DIAL from other apps. The number is only pre-filled, never dialed. */
    private fun handleDialIntent(intent: Intent?) {
        if (intent == null) return
        val isDial = intent.action == Intent.ACTION_DIAL || intent.action == Intent.ACTION_VIEW
        if (!isDial) return
        val data = intent.data
        if (data != null && data.scheme != "tel") return
        val number = data?.schemeSpecificPart.orEmpty()
        DialMeLog.i("Dial", "Dial intent received (${intent.action}), number length ${number.length}")
        dialRequest = DialRequest(number = number, token = System.nanoTime())
    }
}
