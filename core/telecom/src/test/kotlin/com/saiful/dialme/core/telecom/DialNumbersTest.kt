package com.saiful.dialme.core.telecom

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DialNumbersTest {

    @Test
    fun sanitize_removesSeparators() {
        assertEquals("01712345678", DialNumbers.sanitize("017 1234-5678"))
        assertEquals("0171234567", DialNumbers.sanitize("(017) 123.4567"))
    }

    @Test
    fun sanitize_keepsLeadingPlus() {
        assertEquals("+88017", DialNumbers.sanitize("+880 17"))
    }

    @Test
    fun sanitize_rejectsPlusInTheMiddle() {
        assertNull(DialNumbers.sanitize("1+2"))
    }

    @Test
    fun sanitize_convertsBengaliDigits() {
        assertEquals("0123", DialNumbers.sanitize("\u09E6\u09E7\u09E8\u09E9"))
    }

    @Test
    fun sanitize_keepsStarAndHash() {
        assertEquals("*#06#", DialNumbers.sanitize("*#06#"))
    }

    @Test
    fun sanitize_rejectsLettersEmptyAndPlusOnly() {
        assertNull(DialNumbers.sanitize("12a4"))
        assertNull(DialNumbers.sanitize(""))
        assertNull(DialNumbers.sanitize("   "))
        assertNull(DialNumbers.sanitize("+"))
    }

    @Test
    fun sanitize_rejectsTooLong() {
        assertNull(DialNumbers.sanitize("1".repeat(DialNumbers.MAX_LENGTH + 1)))
    }

    @Test
    fun mask_hidesAllButLastTwo() {
        assertEquals("*********78", DialNumbers.mask("01712345678"))
        assertEquals("**", DialNumbers.mask("12"))
        assertEquals("unknown", DialNumbers.mask(null))
        assertEquals("unknown", DialNumbers.mask("  "))
    }
}
