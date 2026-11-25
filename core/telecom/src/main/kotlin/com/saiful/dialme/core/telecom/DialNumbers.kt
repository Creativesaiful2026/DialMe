package com.saiful.dialme.core.telecom

/** Pure helpers for phone number input and for keeping numbers out of logs. */
object DialNumbers {
    const val MAX_LENGTH = 32

    /**
     * Cleans user input into something Telecom can dial, or returns null if it is not a valid
     * dial string. Spaces, dashes, dots and brackets are removed, Bengali and other Unicode digits
     * become ASCII digits, "+" is allowed only at the start, "*" and "#" are kept.
     */
    fun sanitize(input: String): String? {
        val out = StringBuilder()
        for (ch in input) {
            when {
                ch.isWhitespace() || ch == '-' || ch == '.' || ch == '(' || ch == ')' -> Unit
                ch == '+' -> {
                    if (out.isNotEmpty()) return null
                    out.append(ch)
                }
                ch == '*' || ch == '#' -> out.append(ch)
                else -> {
                    val digit = Character.digit(ch, 10)
                    if (digit < 0) return null
                    out.append('0' + digit)
                }
            }
            if (out.length > MAX_LENGTH) return null
        }
        val result = out.toString()
        val hasDialableChar = result.any { it in '0'..'9' || it == '*' || it == '#' }
        return if (hasDialableChar) result else null
    }

    /** Replaces all but the last two characters with "*" so logs never contain full numbers. */
    fun mask(number: String?): String {
        if (number.isNullOrBlank()) return "unknown"
        return if (number.length <= 2) "**" else "*".repeat(number.length - 2) + number.takeLast(2)
    }
}
