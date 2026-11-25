package com.saiful.dialme.core.telecom

import android.telecom.Call

object CallStateMapper {
    fun map(state: Int): CallStatus = when (state) {
        Call.STATE_NEW -> CallStatus.NEW
        Call.STATE_DIALING -> CallStatus.DIALING
        Call.STATE_CONNECTING -> CallStatus.CONNECTING
        Call.STATE_RINGING -> CallStatus.RINGING
        Call.STATE_ACTIVE -> CallStatus.ACTIVE
        Call.STATE_HOLDING -> CallStatus.HOLDING
        Call.STATE_DISCONNECTING -> CallStatus.DISCONNECTING
        Call.STATE_DISCONNECTED -> CallStatus.DISCONNECTED
        else -> CallStatus.UNKNOWN
    }
}
