package com.saiful.dialme.feature.incall

import org.junit.Assert.assertEquals
import org.junit.Test

class DurationFormatterTest {

    @Test
    fun format_underAnHour() {
        assertEquals("00:00", DurationFormatter.format(0))
        assertEquals("00:09", DurationFormatter.format(9_999))
        assertEquals("01:05", DurationFormatter.format(65_000))
        assertEquals("59:59", DurationFormatter.format(3_599_000))
    }

    @Test
    fun format_overAnHour() {
        assertEquals("1:02:05", DurationFormatter.format(3_725_000))
    }

    @Test
    fun format_negativeIsZero() {
        assertEquals("00:00", DurationFormatter.format(-5_000))
    }
}
