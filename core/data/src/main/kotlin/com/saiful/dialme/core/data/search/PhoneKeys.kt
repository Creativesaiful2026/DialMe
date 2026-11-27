package com.saiful.dialme.core.data.search

/** Helpers to decide whether two phone number strings mean the same number. */
object PhoneKeys {
    private const val KEY_LENGTH = 10

    /**
     * A comparison key for [number]: its digits, shortened to the last ten so that "01712345678"
     * and "+8801712345678" give the same key.
     */
    fun matchKey(number: String): String {
        val digits = T9.digitsOnly(number)
        return if (digits.length > KEY_LENGTH) digits.takeLast(KEY_LENGTH) else digits
    }
}
