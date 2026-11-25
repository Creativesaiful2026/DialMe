package com.saiful.dialme.core.telecom

import android.content.Context
import android.os.Build
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import com.saiful.dialme.core.common.logging.DialMeLog

/** Finds the SIMs that can place calls and describes the SIM used by a call. */
object SimAccountProvider {
    private const val TAG = "SimAccounts"

    data class SimDescription(val label: String?, val slotNumber: Int?, val subscriptionId: Int?)

    /** Needs READ_PHONE_STATE. Returns an empty list when the permission is missing. */
    fun list(context: Context): List<SimAccount> {
        if (!TelecomStatus.hasReadPhoneState(context)) return emptyList()
        val telecom = context.getSystemService(TelecomManager::class.java) ?: return emptyList()
        val handles: List<PhoneAccountHandle> = try {
            telecom.callCapablePhoneAccounts
        } catch (e: SecurityException) {
            DialMeLog.w(TAG, "No permission to list phone accounts", e)
            return emptyList()
        }

        val result = mutableListOf<SimAccount>()
        for (handle in handles) {
            val account = try {
                telecom.getPhoneAccount(handle)
            } catch (e: Exception) {
                null
            } ?: continue
            if (!account.hasCapabilities(PhoneAccount.CAPABILITY_SIM_SUBSCRIPTION)) continue
            val description = describe(context, handle)
            result.add(
                SimAccount(
                    handle = handle,
                    label = account.label?.toString().orEmpty(),
                    slotNumber = description.slotNumber,
                    subscriptionId = description.subscriptionId,
                ),
            )
        }
        return result.sortedWith(compareBy({ it.slotNumber ?: Int.MAX_VALUE }, { it.label }))
    }

    /** Describes the SIM behind [handle]. Every field may be null if the system does not tell us. */
    fun describe(context: Context, handle: PhoneAccountHandle?): SimDescription {
        if (handle == null) return SimDescription(null, null, null)
        val telecom = context.getSystemService(TelecomManager::class.java)
        val label = try {
            telecom?.getPhoneAccount(handle)?.label?.toString()
        } catch (e: Exception) {
            null
        }
        var slot: Int? = null
        var subId: Int? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val tm = context.getSystemService(TelephonyManager::class.java)
                    ?.createForPhoneAccountHandle(handle)
                val id = tm?.subscriptionId
                if (id != null && id != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                    subId = id
                    val info = context.getSystemService(SubscriptionManager::class.java)
                        ?.getActiveSubscriptionInfo(id)
                    val index = info?.simSlotIndex
                    if (index != null && index >= 0) slot = index + 1
                }
            } catch (e: Exception) {
                DialMeLog.w(TAG, "Could not resolve SIM slot", e)
            }
        }
        return SimDescription(label, slot, subId)
    }
}
