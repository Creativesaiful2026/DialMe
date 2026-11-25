package com.saiful.dialme.feature.incall

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saiful.dialme.core.common.logging.DialMeLog
import com.saiful.dialme.core.designsystem.theme.DialMeTheme
import com.saiful.dialme.core.model.theme.ThemeSpec
import com.saiful.dialme.core.telecom.CallManager
import com.saiful.dialme.core.telecom.InCallLauncher

/**
 * Plain in-call screen for the Phase 1B Telecom spike. It shows the current call(s) and the basic
 * controls. It uses the default theme because the app-wide theme store is not shared with this
 * module yet.
 */
class InCallActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            handleIntent(intent)
        }

        setContent {
            val calls by CallManager.calls.collectAsStateWithLifecycle()
            val audio by CallManager.audio.collectAsStateWithLifecycle()
            val spec = remember { ThemeSpec() }

            LaunchedEffect(calls.isEmpty()) {
                if (calls.isEmpty()) {
                    DialMeLog.i("InCall", "No calls left, closing in-call screen")
                    finish()
                }
            }

            DialMeTheme(spec) {
                InCallScreen(
                    calls = calls,
                    audio = audio,
                    onAnswer = CallManager::answer,
                    onReject = CallManager::reject,
                    onDisconnect = CallManager::disconnect,
                    onHoldChange = { id, hold -> if (hold) CallManager.hold(id) else CallManager.unhold(id) },
                    onMuteChange = CallManager::setMuted,
                    onRoute = CallManager::setAudioRoute,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val id = intent?.getIntExtra(InCallLauncher.EXTRA_AUTO_ANSWER_CALL_ID, -1) ?: -1
        if (id >= 0) {
            DialMeLog.i("InCall", "Answer requested from notification (call $id)")
            CallManager.answer(id)
            intent?.removeExtra(InCallLauncher.EXTRA_AUTO_ANSWER_CALL_ID)
        }
    }
}
