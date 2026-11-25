package com.saiful.dialme.ui.screens

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.saiful.dialme.R
import com.saiful.dialme.core.common.logging.DialMeLog
import com.saiful.dialme.core.telecom.CallPlacer
import com.saiful.dialme.core.telecom.DialerRole
import com.saiful.dialme.core.telecom.PlaceCallResult
import com.saiful.dialme.core.telecom.SimAccount
import com.saiful.dialme.core.telecom.SimAccountProvider
import com.saiful.dialme.core.telecom.TelecomStatus

/**
 * Phase 1B test screen. It shows what the calling features need (default Phone app, permissions,
 * full-screen call notifications), lists the SIMs, and places a call with the chosen SIM.
 * It deliberately uses no phone numbers in logs and never dials on its own.
 */
@Composable
fun TelecomSpikeScreen(
    initialNumber: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var refreshKey by remember { mutableIntStateOf(0) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { refreshKey++ }

    val roleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { refreshKey++ }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { refreshKey++ }

    val isDefaultDialer = remember(refreshKey) { DialerRole.isHeld(context) }
    val defaultPackage = remember(refreshKey) { DialerRole.defaultDialerPackage(context) }
    val defaultLabel = remember(refreshKey) {
        defaultPackage?.let { DialerRole.appLabel(context, it) }
    }
    val hasCallPhone = remember(refreshKey) { TelecomStatus.hasCallPhone(context) }
    val hasPhoneState = remember(refreshKey) { TelecomStatus.hasReadPhoneState(context) }
    val hasNotifications = remember(refreshKey) { TelecomStatus.hasNotifications(context) }
    val canFullScreen = remember(refreshKey) { TelecomStatus.canUseFullScreenIntent(context) }
    val sims = remember(refreshKey) { SimAccountProvider.list(context) }

    LaunchedEffect(refreshKey) {
        DialMeLog.i(
            "TelecomSpike",
            "Status: defaultDialer=$isDefaultDialer (package=$defaultPackage), CALL_PHONE=$hasCallPhone, " +
                "READ_PHONE_STATE=$hasPhoneState, notifications=$hasNotifications, " +
                "fullScreenIntent=$canFullScreen, sims=${sims.map { it.slotNumber ?: -1 }}",
        )
    }

    var number by remember(initialNumber) { mutableStateOf(initialNumber.orEmpty()) }
    var message by remember { mutableStateOf<String?>(null) }

    val placeCall: (SimAccount?) -> Unit = { sim ->
        val result = CallPlacer.placeCall(context, number, sim)
        message = context.getString(
            when (result) {
                PlaceCallResult.STARTED -> R.string.telecom_result_started
                PlaceCallResult.INVALID_NUMBER -> R.string.telecom_result_invalid
                PlaceCallResult.NO_PERMISSION -> R.string.telecom_result_no_permission
                PlaceCallResult.ERROR -> R.string.telecom_result_error
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(onClick = onBack) { Text(stringResource(R.string.diagnostics_back)) }
        Text(
            text = stringResource(R.string.telecom_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.telecom_safety_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // ---- Status ----
        Text(
            text = stringResource(R.string.telecom_section_status),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp),
        )
        StatusRow(stringResource(R.string.telecom_default_dialer), isDefaultDialer)
        val currentApp = defaultLabel ?: defaultPackage ?: stringResource(R.string.telecom_unknown)
        Text(
            text = stringResource(R.string.telecom_current_default, currentApp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        StatusRow(stringResource(R.string.telecom_perm_call_phone), hasCallPhone)
        StatusRow(stringResource(R.string.telecom_perm_phone_state), hasPhoneState)
        StatusRow(stringResource(R.string.telecom_perm_notifications), hasNotifications)
        StatusRow(stringResource(R.string.telecom_fullscreen), canFullScreen)

        Button(
            onClick = {
                val intent = DialerRole.requestIntent(context)
                if (intent == null) {
                    message = context.getString(R.string.telecom_role_unavailable)
                } else {
                    roleLauncher.launch(intent)
                }
            },
            enabled = !isDefaultDialer,
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.telecom_request_role)) }
        Button(
            onClick = { permissionLauncher.launch(TelecomStatus.requiredRuntimePermissions()) },
            enabled = !(hasCallPhone && hasPhoneState && hasNotifications),
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.telecom_request_permissions)) }
        if (!canFullScreen) {
            OutlinedButton(
                onClick = {
                    val intent = TelecomStatus.fullScreenIntentSettingsIntent(context)
                    if (intent != null) {
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            DialMeLog.w("TelecomSpike", "Full-screen intent settings not available", e)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.telecom_open_fullscreen_settings)) }
        }
        Text(
            text = stringResource(R.string.telecom_role_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // ---- SIMs ----
        Text(
            text = stringResource(R.string.telecom_section_sims),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        if (sims.isEmpty()) {
            Text(
                text = stringResource(R.string.telecom_no_sims),
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            sims.forEach { sim ->
                val slot = sim.slotNumber
                Text(
                    text = if (slot != null) {
                        stringResource(R.string.telecom_sim_item, slot, sim.label)
                    } else {
                        stringResource(R.string.telecom_sim_item_unknown_slot, sim.label)
                    },
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // ---- Call ----
        Text(
            text = stringResource(R.string.telecom_section_call),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text(stringResource(R.string.telecom_number_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        sims.forEach { sim ->
            val slot = sim.slotNumber
            Button(
                onClick = { placeCall(sim) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (slot != null) {
                        stringResource(R.string.telecom_call_sim, slot)
                    } else {
                        stringResource(R.string.telecom_call_account, sim.label)
                    },
                )
            }
        }
        OutlinedButton(
            onClick = { placeCall(null) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.telecom_call_default)) }

        val resultText = message
        if (resultText != null) {
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun StatusRow(label: String, ok: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, modifier = Modifier.weight(1f))
        Text(
            text = if (ok) stringResource(R.string.telecom_yes) else stringResource(R.string.telecom_no),
            color = if (ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
    }
}
