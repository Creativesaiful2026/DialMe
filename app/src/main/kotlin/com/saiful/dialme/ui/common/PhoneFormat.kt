package com.saiful.dialme.ui.common

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/** Formats [number] for display using the rules of [countryIso]; returns it unchanged if it cannot. */
fun formatNumber(number: String, countryIso: String): String {
    if (number.isBlank()) return number
    return try {
        PhoneNumberUtils.formatNumber(number, countryIso) ?: number
    } catch (e: Exception) {
        number
    }
}

private fun detectCountryIso(context: Context): String {
    val sim = context.getSystemService(TelephonyManager::class.java)?.simCountryIso
    if (!sim.isNullOrBlank()) return sim.uppercase(Locale.ROOT)
    val locale = context.resources.configuration.locales[0].country
    return if (locale.isNotBlank()) locale else "BD"
}

/** A function that formats phone numbers for display, based on the SIM country. */
@Composable
fun rememberPhoneFormatter(): (String) -> String {
    val context = LocalContext.current
    val country = remember { detectCountryIso(context) }
    return remember(country) {
        val formatter: (String) -> String = { number -> formatNumber(number, country) }
        formatter
    }
}
