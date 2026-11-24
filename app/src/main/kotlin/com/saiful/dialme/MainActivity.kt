package com.saiful.dialme

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saiful.dialme.core.designsystem.theme.DialMeTheme
import com.saiful.dialme.core.designsystem.theme.isDark
import com.saiful.dialme.ui.DialMeApp
import com.saiful.dialme.ui.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                )
            }
        }
    }
}
