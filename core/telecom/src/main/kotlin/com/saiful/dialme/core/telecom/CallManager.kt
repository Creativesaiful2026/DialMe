package com.saiful.dialme.core.telecom

import android.content.Context
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.telecom.PhoneAccountHandle
import android.telecom.VideoProfile
import com.saiful.dialme.core.common.logging.DialMeLog
import com.saiful.dialme.core.telecom.notification.CallNotifier
import java.util.IdentityHashMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-wide hub between Android's Telecom framework and DialMe's UI.
 *
 * The system creates [com.saiful.dialme.core.telecom.service.DialMeInCallService]; the service
 * forwards everything here. UI code only reads [calls] and [audio] and calls the action functions,
 * and never touches android.telecom.Call directly.
 */
object CallManager {
    private const val TAG = "CallManager"

    private val lock = Any()
    private val idByCall = IdentityHashMap<Call, Int>()
    private val callById = LinkedHashMap<Int, Call>()
    private val callbackByCall = IdentityHashMap<Call, Call.Callback>()
    private val lastStatus = HashMap<Int, CallStatus>()
    private val simCache = HashMap<PhoneAccountHandle, SimAccountProvider.SimDescription>()
    private var nextId = 1

    private val _calls = MutableStateFlow<List<CallInfo>>(emptyList())
    val calls: StateFlow<List<CallInfo>> = _calls.asStateFlow()

    private val _audio = MutableStateFlow(AudioInfo())
    val audio: StateFlow<AudioInfo> = _audio.asStateFlow()

    @Volatile
    private var service: InCallService? = null

    @Volatile
    private var appContext: Context? = null

    // ---- Called by the InCallService -------------------------------------------------------

    fun onServiceCreated(service: InCallService) {
        this.service = service
        this.appContext = service.applicationContext
        DialMeLog.i(TAG, "InCallService created")
    }

    fun onServiceDestroyed(service: InCallService) {
        if (this.service === service) this.service = null
        DialMeLog.i(TAG, "InCallService destroyed")
    }

    @Suppress("DEPRECATION")
    fun onCallAdded(call: Call) {
        val id: Int
        synchronized(lock) {
            id = nextId++
            idByCall[call] = id
            callById[id] = call
        }
        val callback = object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) {
                publish("state changed")
            }

            override fun onDetailsChanged(call: Call, details: Call.Details) {
                publish("details changed")
            }
        }
        synchronized(lock) { callbackByCall[call] = callback }
        call.registerCallback(callback)

        try {
            onAudioStateChanged(service?.callAudioState)
        } catch (e: Exception) {
            DialMeLog.w(TAG, "Could not read initial audio state", e)
        }

        publish("call added")

        val info = _calls.value.firstOrNull { it.id == id }
        if (info != null && info.direction == CallDirection.OUTGOING) {
            openInCallScreen()
        }
    }

    fun onCallRemoved(call: Call) {
        val id: Int?
        val callback: Call.Callback?
        synchronized(lock) {
            id = idByCall.remove(call)
            if (id != null) {
                callById.remove(id)
                lastStatus.remove(id)
            }
            callback = callbackByCall.remove(call)
        }
        if (callback != null) {
            try {
                call.unregisterCallback(callback)
            } catch (e: Exception) {
                DialMeLog.w(TAG, "unregisterCallback failed", e)
            }
        }
        val cause = try {
            call.details?.disconnectCause
        } catch (e: Exception) {
            null
        }
        DialMeLog.i(
            TAG,
            "Call $id removed (disconnect code=${cause?.code}, reason=${cause?.reason})",
        )
        publish("call removed")
    }

    @Suppress("DEPRECATION")
    fun onAudioStateChanged(state: CallAudioState?) {
        if (state == null) return
        val mask = state.supportedRouteMask
        val available = buildSet {
            if ((mask and CallAudioState.ROUTE_EARPIECE) != 0) add(AudioRoute.EARPIECE)
            if ((mask and CallAudioState.ROUTE_SPEAKER) != 0) add(AudioRoute.SPEAKER)
            if ((mask and CallAudioState.ROUTE_WIRED_HEADSET) != 0) add(AudioRoute.WIRED_HEADSET)
            if ((mask and CallAudioState.ROUTE_BLUETOOTH) != 0) add(AudioRoute.BLUETOOTH)
        }
        val route = when (state.route) {
            CallAudioState.ROUTE_EARPIECE -> AudioRoute.EARPIECE
            CallAudioState.ROUTE_SPEAKER -> AudioRoute.SPEAKER
            CallAudioState.ROUTE_WIRED_HEADSET -> AudioRoute.WIRED_HEADSET
            CallAudioState.ROUTE_BLUETOOTH -> AudioRoute.BLUETOOTH
            else -> null
        }
        val info = AudioInfo(isMuted = state.isMuted, route = route, availableRoutes = available)
        if (info != _audio.value) {
            DialMeLog.i(TAG, "Audio: muted=${info.isMuted}, route=${info.route}, available=${info.availableRoutes}")
        }
        _audio.value = info
    }

    // ---- Actions used by the UI and notifications -------------------------------------------

    fun answer(id: Int) = withCall(id, "answer") { it.answer(VideoProfile.STATE_AUDIO_ONLY) }

    fun reject(id: Int) = withCall(id, "reject") { it.reject(false, null) }

    fun disconnect(id: Int) = withCall(id, "disconnect") { it.disconnect() }

    fun hold(id: Int) = withCall(id, "hold") { it.hold() }

    fun unhold(id: Int) = withCall(id, "unhold") { it.unhold() }

    fun setMuted(muted: Boolean) {
        val s = service
        if (s == null) {
            DialMeLog.w(TAG, "setMuted($muted) ignored: no InCallService")
            return
        }
        DialMeLog.i(TAG, "Action: setMuted($muted)")
        s.setMuted(muted)
    }

    @Suppress("DEPRECATION")
    fun setAudioRoute(route: AudioRoute) {
        val s = service
        if (s == null) {
            DialMeLog.w(TAG, "setAudioRoute($route) ignored: no InCallService")
            return
        }
        DialMeLog.i(TAG, "Action: setAudioRoute($route)")
        s.setAudioRoute(
            when (route) {
                AudioRoute.EARPIECE -> CallAudioState.ROUTE_EARPIECE
                AudioRoute.SPEAKER -> CallAudioState.ROUTE_SPEAKER
                AudioRoute.WIRED_HEADSET -> CallAudioState.ROUTE_WIRED_HEADSET
                AudioRoute.BLUETOOTH -> CallAudioState.ROUTE_BLUETOOTH
            },
        )
    }

    // ---- Internals ---------------------------------------------------------------------------

    private fun withCall(id: Int, name: String, block: (Call) -> Unit) {
        val call = synchronized(lock) { callById[id] }
        if (call == null) {
            DialMeLog.w(TAG, "Action $name ignored: unknown call id $id")
            return
        }
        DialMeLog.i(TAG, "Action: $name (call $id)")
        try {
            block(call)
        } catch (e: Exception) {
            DialMeLog.e(TAG, "Action $name failed for call $id", e)
        }
    }

    private fun publish(reason: String) {
        val entries = synchronized(lock) { callById.entries.map { it.key to it.value } }
        val infos = entries.map { (id, call) -> snapshot(id, call) }
        for (info in infos) {
            val previous = synchronized(lock) { lastStatus.put(info.id, info.status) }
            if (previous != info.status) {
                DialMeLog.i(
                    TAG,
                    "Call ${info.id}: ${previous ?: "-"} -> ${info.status} " +
                        "(${info.direction}, SIM ${info.simSlot ?: "?"}, ${DialNumbers.mask(info.number)}) [$reason]",
                )
            }
        }
        _calls.value = infos
        appContext?.let { CallNotifier.sync(it, infos) }
    }

    @Suppress("DEPRECATION")
    private fun snapshot(id: Int, call: Call): CallInfo {
        val details = call.details
        val rawStatus = CallStateMapper.map(call.state)

        var direction = when (details?.callDirection) {
            Call.Details.DIRECTION_INCOMING -> CallDirection.INCOMING
            Call.Details.DIRECTION_OUTGOING -> CallDirection.OUTGOING
            else -> CallDirection.UNKNOWN
        }
        if (direction == CallDirection.UNKNOWN) {
            direction = when (rawStatus) {
                CallStatus.RINGING -> CallDirection.INCOMING
                CallStatus.DIALING, CallStatus.CONNECTING -> CallDirection.OUTGOING
                else -> CallDirection.UNKNOWN
            }
        }

        val sim = describeSim(details?.accountHandle)
        return CallInfo(
            id = id,
            number = details?.handle?.schemeSpecificPart?.takeIf { it.isNotBlank() },
            displayName = details?.callerDisplayName?.takeIf { it.isNotBlank() },
            direction = direction,
            status = rawStatus,
            simLabel = sim?.label,
            simSlot = sim?.slotNumber,
            connectTimeMillis = details?.connectTimeMillis ?: 0L,
            canHold = details?.can(Call.Details.CAPABILITY_HOLD) == true,
            canMute = details?.can(Call.Details.CAPABILITY_MUTE) == true,
        )
    }

    private fun describeSim(handle: PhoneAccountHandle?): SimAccountProvider.SimDescription? {
        if (handle == null) return null
        val context = appContext ?: return null
        synchronized(lock) { simCache[handle] }?.let { return it }
        val description = SimAccountProvider.describe(context, handle)
        // Only cache complete answers so a missing permission does not stick.
        if (description.slotNumber != null) {
            synchronized(lock) { simCache[handle] = description }
        }
        return description
    }

    private fun openInCallScreen() {
        val context = appContext ?: return
        try {
            context.startActivity(InCallLauncher.intent(context))
            DialMeLog.i(TAG, "In-call screen started for outgoing call")
        } catch (e: Exception) {
            DialMeLog.w(TAG, "Could not open the in-call screen directly (use the notification)", e)
        }
    }
}
