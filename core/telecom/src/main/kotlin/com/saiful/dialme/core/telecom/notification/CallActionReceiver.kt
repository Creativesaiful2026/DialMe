package com.saiful.dialme.core.telecom.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.saiful.dialme.core.telecom.CallManager

/** Handles the Decline and Hang up buttons of call notifications. */
class CallActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val callId = intent.getIntExtra(EXTRA_CALL_ID, -1)
        when (intent.action) {
            ACTION_DECLINE -> CallManager.reject(callId)
            ACTION_HANGUP -> CallManager.disconnect(callId)
        }
    }

    companion object {
        const val ACTION_DECLINE = "com.saiful.dialme.action.DECLINE_CALL"
        const val ACTION_HANGUP = "com.saiful.dialme.action.HANGUP_CALL"
        const val EXTRA_CALL_ID = "com.saiful.dialme.extra.CALL_ID"
    }
}
