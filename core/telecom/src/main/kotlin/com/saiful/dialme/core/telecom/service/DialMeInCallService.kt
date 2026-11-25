package com.saiful.dialme.core.telecom.service

import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import com.saiful.dialme.core.telecom.CallManager

/** Entry point that Android binds to when DialMe is the default Phone app and a call exists. */
class DialMeInCallService : InCallService() {

    override fun onCreate() {
        super.onCreate()
        CallManager.onServiceCreated(this)
    }

    override fun onDestroy() {
        CallManager.onServiceDestroyed(this)
        super.onDestroy()
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CallManager.onCallAdded(call)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        CallManager.onCallRemoved(call)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        super.onCallAudioStateChanged(audioState)
        CallManager.onAudioStateChanged(audioState)
    }
}
