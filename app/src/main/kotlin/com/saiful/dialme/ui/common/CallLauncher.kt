package com.saiful.dialme.ui.common

import android.Manifest
import android.content.Context
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.saiful.dialme.R
import com.saiful.dialme.core.telecom.CallPlacer
import com.saiful.dialme.core.telecom.PlaceCallResult
import com.saiful.dialme.core.telecom.SimAccount
import com.saiful.dialme.core.telecom.SimAccountProvider
import com.saiful.dialme.core.telecom.TelecomStatus

/** Starts calls from any screen. Obtain it from [CallLauncherHost]. */
class CallLauncher internal constructor(private val onCall: (String) -> Unit) {
    fun call(number: String) = onCall(number)
}

private fun systemDefaultSimOrNull(context: Context): PhoneAccountHandle? = try {
    context.getSystemService(TelecomManager::class.java)
        ?.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL)
} catch (e: SecurityException) {
    null
}

/**
 * Wraps a screen and gives it a [CallLauncher]. It asks for the call permissions when they are
 * missing and, on a phone with two SIMs and no system default SIM, asks which SIM to use.
 */
@Composable
fun CallLauncherHost(content: @Composable (CallLauncher) -> Unit) {
    val context = LocalContext.current
    var pendingNumber by remember { mutableStateOf<String?>(null) }
    var chooserNumber by remember { mutableStateOf<String?>(null) }
    var chooserSims by remember { mutableStateOf<List<SimAccount>>(emptyList()) }

    fun place(number: String, sim: SimAccount?) {
        val messageRes = when (CallPlacer.placeCall(context, number, sim)) {
            PlaceCallResult.STARTED -> null
            PlaceCallResult.INVALID_NUMBER -> R.string.call_error_invalid
            PlaceCallResult.NO_PERMISSION -> R.string.call_error_permission
            PlaceCallResult.ERROR -> R.string.call_error_failed
        }
        if (messageRes != null) Toast.makeText(context, messageRes, Toast.LENGTH_LONG).show()
    }

    fun proceed(number: String) {
        val sims = SimAccountProvider.list(context)
        if (sims.size >= 2 && systemDefaultSimOrNull(context) == null) {
            chooserSims = sims
            chooserNumber = number
        } else {
            place(number, null)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        val number = pendingNumber
        pendingNumber = null
        if (number != null) {
            if (TelecomStatus.hasCallPhone(context)) {
                proceed(number)
            } else {
                Toast.makeText(context, R.string.call_error_permission, Toast.LENGTH_LONG).show()
            }
        }
    }

    val launcher = remember {
        CallLauncher { number ->
            if (TelecomStatus.hasCallPhone(context)) {
                proceed(number)
            } else {
                pendingNumber = number
                permissionLauncher.launch(
                    arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE),
                )
            }
        }
    }

    content(launcher)

    val number = chooserNumber
    if (number != null) {
        AlertDialog(
            onDismissRequest = { chooserNumber = null },
            title = { Text(stringResource(R.string.call_choose_sim)) },
            text = {
                Column {
                    chooserSims.forEach { sim ->
                        val slot = sim.slotNumber
                        val label = if (slot != null) {
                            stringResource(R.string.sim_item, slot, sim.label)
                        } else {
                            sim.label
                        }
                        TextButton(
                            onClick = {
                                chooserNumber = null
                                place(number, sim)
                            },
                        ) { Text(label) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { chooserNumber = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }
}
