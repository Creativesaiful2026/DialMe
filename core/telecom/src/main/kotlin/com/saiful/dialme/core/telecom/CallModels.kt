package com.saiful.dialme.core.telecom

import android.telecom.PhoneAccountHandle

enum class CallDirection { INCOMING, OUTGOING, UNKNOWN }

enum class CallStatus {
    NEW, DIALING, CONNECTING, RINGING, ACTIVE, HOLDING, DISCONNECTING, DISCONNECTED, UNKNOWN
}

enum class AudioRoute { EARPIECE, SPEAKER, WIRED_HEADSET, BLUETOOTH }

/** Read-only snapshot of one call, safe to hand to UI code. */
data class CallInfo(
    val id: Int,
    val number: String?,
    val displayName: String?,
    val direction: CallDirection,
    val status: CallStatus,
    val simLabel: String?,
    val simSlot: Int?,
    val connectTimeMillis: Long,
    val canHold: Boolean,
    val canMute: Boolean,
)

data class AudioInfo(
    val isMuted: Boolean = false,
    val route: AudioRoute? = null,
    val availableRoutes: Set<AudioRoute> = emptySet(),
)

/** A SIM that can place calls. [slotNumber] is 1-based (SIM 1, SIM 2) when it could be resolved. */
data class SimAccount(
    val handle: PhoneAccountHandle,
    val label: String,
    val slotNumber: Int?,
    val subscriptionId: Int?,
)

enum class PlaceCallResult { STARTED, INVALID_NUMBER, NO_PERMISSION, ERROR }
