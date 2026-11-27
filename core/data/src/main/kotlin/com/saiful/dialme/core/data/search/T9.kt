package com.saiful.dialme.core.data.search

/**
 * Pure helpers for T9 style search on the dialpad.
 *
 * Only Latin letters have a T9 key. Letters of other scripts (for example Bengali) are treated as
 * word separators, so such names can still be found by their phone number or by the text search
 * on the Contacts screen.
 */
object T9 {
    private val keyLetters: Map<Char, String> = mapOf(
        '2' to "abc",
        '3' to "def",
        '4' to "ghi",
        '5' to "jkl",
        '6' to "mno",
        '7' to "pqrs",
        '8' to "tuv",
        '9' to "wxyz",
    )

    private val letterToDigit: Map<Char, Char> = buildMap {
        for ((digit, letters) in keyLetters) {
            for (letter in letters) put(letter, digit)
        }
    }

    /** Letters printed under a key, for example "ABC" for '2'. Empty for keys without letters. */
    fun lettersFor(key: Char): String = keyLetters[key]?.uppercase().orEmpty()

    /** The ASCII digit a character stands for on a T9 keypad, or null if it has none. */
    fun digitFor(ch: Char): Char? {
        val mapped = letterToDigit[ch.lowercaseChar()]
        if (mapped != null) return mapped
        val digit = Character.digit(ch, 10)
        return if (digit >= 0) '0' + digit else null
    }

    /** Keeps only digits and converts Bengali and other Unicode digits to ASCII digits. */
    fun digitsOnly(text: String): String = buildString {
        for (ch in text) {
            val digit = Character.digit(ch, 10)
            if (digit >= 0) append('0' + digit)
        }
    }

    /** The T9 digit string of every word in [name]. */
    fun wordCodes(name: String): List<String> {
        val words = mutableListOf<String>()
        val current = StringBuilder()
        for (ch in name) {
            val digit = digitFor(ch)
            if (digit != null) {
                current.append(digit)
            } else if (current.isNotEmpty()) {
                words.add(current.toString())
                current.setLength(0)
            }
        }
        if (current.isNotEmpty()) words.add(current.toString())
        return words
    }

    /**
     * How well [name] matches the typed [query] (digits only): 0 when a word of the name starts
     * with the query, 1 when the query appears anywhere in the name, null when it does not match.
     */
    fun nameRank(name: String, query: String): Int? {
        if (query.isEmpty() || query.any { it !in '0'..'9' }) return null
        val words = wordCodes(name)
        if (words.any { it.startsWith(query) }) return 0
        if (words.joinToString("").contains(query)) return 1
        return null
    }
}
