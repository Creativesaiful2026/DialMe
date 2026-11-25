package com.saiful.dialme.core.telecom

import android.content.Context
import android.content.Intent

/**
 * Builds intents for the in-call screen. The activity lives in another module, so it is
 * addressed by class name (all modules share the app's package at runtime).
 */
object InCallLauncher {
    const val ACTIVITY_CLASS_NAME = "com.saiful.dialme.feature.incall.InCallActivity"
    const val EXTRA_AUTO_ANSWER_CALL_ID = "com.saiful.dialme.extra.AUTO_ANSWER_CALL_ID"

    fun intent(context: Context, autoAnswerCallId: Int? = null): Intent {
        val intent = Intent()
            .setClassName(context.packageName, ACTIVITY_CLASS_NAME)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        if (autoAnswerCallId != null) {
            intent.putExtra(EXTRA_AUTO_ANSWER_CALL_ID, autoAnswerCallId)
        }
        return intent
    }
}
