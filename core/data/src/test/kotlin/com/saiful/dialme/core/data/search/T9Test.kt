package com.saiful.dialme.core.data.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class T9Test {

    @Test
    fun lettersAreMappedToKeys() {
        assertEquals('2', T9.digitFor('a'))
        assertEquals('2', T9.digitFor('C'))
        assertEquals('7', T9.digitFor('s'))
        assertEquals('9', T9.digitFor('z'))
        assertEquals('5', T9.digitFor('5'))
        assertNull(T9.digitFor('-'))
    }

    @Test
    fun bengaliDigitsBecomeAsciiDigits() {
        assertEquals("017", T9.digitsOnly("\u09E6\u09E7\u09ED"))
        assertEquals("8801712", T9.digitsOnly("+880 17-12"))
    }

    @Test
    fun wordCodesSplitOnNonKeyCharacters() {
        assertEquals(listOf("254", "76484"), T9.wordCodes("Ali Smith"))
    }

    @Test
    fun nameRankPrefersWordStart() {
        assertEquals(0, T9.nameRank("Ali Khan", "254"))
        assertEquals(0, T9.nameRank("Ali Khan", "5426"))
        assertEquals(1, T9.nameRank("Ali Khan", "42"))
        assertNull(T9.nameRank("Ali Khan", "999"))
    }

    @Test
    fun invalidQueriesNeverMatch() {
        assertNull(T9.nameRank("Ali", ""))
        assertNull(T9.nameRank("Ali", "2a"))
    }

    @Test
    fun bengaliNamesHaveNoT9Code() {
        assertEquals(emptyList<String>(), T9.wordCodes("\u0986\u09B2\u09C0"))
        assertNull(T9.nameRank("\u0986\u09B2\u09C0", "254"))
    }

    @Test
    fun phoneKeyIgnoresCountryCode() {
        assertEquals(PhoneKeys.matchKey("01712345678"), PhoneKeys.matchKey("+8801712345678"))
        assertEquals("123", PhoneKeys.matchKey("1-2 3"))
    }
}
