package com.saiful.dialme.feature.incall

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saiful.dialme.core.telecom.AudioInfo
import com.saiful.dialme.core.telecom.AudioRoute
import com.saiful.dialme.core.telecom.CallDirection
import com.saiful.dialme.core.telecom.CallInfo
import com.saiful.dialme.core.telecom.CallStatus
import kotlinx.coroutines.delay

@Composable
fun InCallScreen(
    calls: List<CallInfo>,
    audio: AudioInfo,
    onAnswer: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onDisconnect: (Int) -> Unit,
    onHoldChange: (Int, Boolean) -> Unit,
    onMuteChange: (Boolean) -> Unit,
    onRoute: (AudioRoute) -> Unit,
) {
    val primary = calls.firstOrNull { it.direction == CallDirection.INCOMING && it.status == CallStatus.RINGING }
        ?: calls.firstOrNull { it.status == CallStatus.ACTIVE }
        ?: calls.firstOrNull()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (primary == null) return@Surface

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            CallHeader(primary, otherCalls = calls.size - 1)

            if (primary.direction == CallDirection.INCOMING && primary.status == CallStatus.RINGING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = { onReject(primary.id) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                    ) { Text(stringResource(R.string.incall_decline)) }
                    Button(
                        onClick = { onAnswer(primary.id) },
                        modifier = Modifier.weight(1f),
                    ) { Text(stringResource(R.string.incall_answer)) }
                }
            } else {
                ActiveControls(
                    call = primary,
                    audio = audio,
                    onDisconnect = onDisconnect,
                    onHoldChange = onHoldChange,
                    onMuteChange = onMuteChange,
                    onRoute = onRoute,
                )
            }
        }
    }
}

@Composable
private fun CallHeader(call: CallInfo, otherCalls: Int) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(call.status) {
        while (call.status == CallStatus.ACTIVE) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = call.displayName ?: call.number ?: stringResource(R.string.incall_unknown_number),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = statusText(call.status),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (call.status == CallStatus.ACTIVE && call.connectTimeMillis > 0) {
            Text(
                text = DurationFormatter.format(now - call.connectTimeMillis),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        val simText = when {
            call.simSlot != null -> stringResource(R.string.incall_sim_slot, call.simSlot!!)
            !call.simLabel.isNullOrBlank() -> call.simLabel
            else -> null
        }
        if (simText != null) {
            Text(
                text = simText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (otherCalls > 0) {
            Text(
                text = stringResource(R.string.incall_other_calls, otherCalls),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ActiveControls(
    call: CallInfo,
    audio: AudioInfo,
    onDisconnect: (Int) -> Unit,
    onHoldChange: (Int, Boolean) -> Unit,
    onMuteChange: (Boolean) -> Unit,
    onRoute: (AudioRoute) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = audio.isMuted,
                onClick = { onMuteChange(!audio.isMuted) },
                label = { Text(stringResource(R.string.incall_mute)) },
            )
            if (call.canHold) {
                val holding = call.status == CallStatus.HOLDING
                FilterChip(
                    selected = holding,
                    onClick = { onHoldChange(call.id, !holding) },
                    label = { Text(stringResource(R.string.incall_hold)) },
                )
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (route in AudioRoute.entries) {
                if (route !in audio.availableRoutes) continue
                FilterChip(
                    selected = audio.route == route,
                    onClick = { onRoute(route) },
                    label = { Text(routeName(route)) },
                )
            }
        }
        Button(
            onClick = { onDisconnect(call.id) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) { Text(stringResource(R.string.incall_end)) }
    }
}

@Composable
private fun statusText(status: CallStatus): String = when (status) {
    CallStatus.DIALING -> stringResource(R.string.incall_status_dialing)
    CallStatus.CONNECTING, CallStatus.NEW -> stringResource(R.string.incall_status_connecting)
    CallStatus.RINGING -> stringResource(R.string.incall_status_ringing)
    CallStatus.ACTIVE -> stringResource(R.string.incall_status_active)
    CallStatus.HOLDING -> stringResource(R.string.incall_status_holding)
    CallStatus.DISCONNECTING, CallStatus.DISCONNECTED -> stringResource(R.string.incall_status_ending)
    CallStatus.UNKNOWN -> stringResource(R.string.incall_status_unknown)
}

@Composable
private fun routeName(route: AudioRoute): String = when (route) {
    AudioRoute.EARPIECE -> stringResource(R.string.incall_route_earpiece)
    AudioRoute.SPEAKER -> stringResource(R.string.incall_route_speaker)
    AudioRoute.WIRED_HEADSET -> stringResource(R.string.incall_route_wired)
    AudioRoute.BLUETOOTH -> stringResource(R.string.incall_route_bluetooth)
}
