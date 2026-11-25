package com.saiful.dialme.core.telecom

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.TelecomManager
import com.saiful.dialme.core.common.logging.DialMeLog

object CallPlacer {
    private const val TAG = "CallPlacer"

    /**
     * Places an outgoing call through Telecom. Pass [sim] to choose the SIM, or null to let the
     * system use its default. Emergency numbers are never filtered or changed here.
     */
    fun placeCall(context: Context, rawNumber: String, sim: SimAccount?): PlaceCallResult {
        val number = DialNumbers.sanitize(rawNumber)
        if (number == null) {
            DialMeLog.w(TAG, "Rejected invalid number input")
            return PlaceCallResult.INVALID_NUMBER
        }
        if (!TelecomStatus.hasCallPhone(context)) {
            DialMeLog.w(TAG, "CALL_PHONE permission missing")
            return PlaceCallResult.NO_PERMISSION
        }
        val telecom = context.getSystemService(TelecomManager::class.java)
        if (telecom == null) {
            DialMeLog.e(TAG, "TelecomManager not available")
            return PlaceCallResult.ERROR
        }
        val extras = Bundle()
        if (sim != null) {
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, sim.handle)
        }
        return try {
            telecom.placeCall(Uri.fromParts(PhoneAccount.SCHEME_TEL, number, null), extras)
            val simText = when {
                sim == null -> "default SIM"
                sim.slotNumber != null -> "SIM ${sim.slotNumber}"
                else -> "SIM '${sim.label}'"
            }
            DialMeLog.i(TAG, "placeCall requested to ${DialNumbers.mask(number)} using $simText")
            PlaceCallResult.STARTED
        } catch (e: SecurityException) {
            DialMeLog.e(TAG, "placeCall denied", e)
            PlaceCallResult.NO_PERMISSION
        } catch (e: Exception) {
            DialMeLog.e(TAG, "placeCall failed", e)
            PlaceCallResult.ERROR
        }
    }
}
