package com.saiful.dialme.core.model.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeCodecTest {

    @Test
    fun roundTrip_keepsValues() {
        val original = ThemeSpec(
            name = "Night",
            mode = ThemeMode.AMOLED,
            useDynamicColor = false,
            cornerRadiusDp = 8,
            buttonShape = ButtonShape.SQUARE,
        )

        val result = ThemeCodec.decode(ThemeCodec.encode(original))

        assertTrue(result is ThemeDecodeResult.Success)
        assertEquals(original.sanitized(), (result as ThemeDecodeResult.Success).spec)
    }

    @Test
    fun encode_writesSchemaVersion() {
        assertTrue(ThemeCodec.encode(ThemeSpec()).contains("schemaVersion"))
    }

    @Test
    fun decode_emptyObjectGivesDefaults() {
        val result = ThemeCodec.decode("{}")
        assertTrue(result is ThemeDecodeResult.Success)
        assertEquals(ThemeSpec().sanitized(), (result as ThemeDecodeResult.Success).spec)
    }

    @Test
    fun decode_ignoresUnknownKeys() {
        val result = ThemeCodec.decode("""{"name":"X","somethingNew":123}""")
        assertTrue(result is ThemeDecodeResult.Success)
        assertEquals("X", (result as ThemeDecodeResult.Success).spec.name)
    }

    @Test
    fun decode_clampsValuesFromFile() {
        val result = ThemeCodec.decode("""{"cornerRadiusDp":5000}""")
        assertTrue(result is ThemeDecodeResult.Success)
        assertEquals(
            ThemeSpec.MAX_CORNER_RADIUS_DP,
            (result as ThemeDecodeResult.Success).spec.cornerRadiusDp,
        )
    }

    @Test
    fun decode_rejectsGarbage() {
        val result = ThemeCodec.decode("this is not json")
        assertEquals(ThemeDecodeResult.Failure(ThemeDecodeError.INVALID_JSON), result)
    }

    @Test
    fun decode_rejectsFutureSchemaVersion() {
        val result = ThemeCodec.decode("""{"schemaVersion":99}""")
        assertEquals(ThemeDecodeResult.Failure(ThemeDecodeError.UNSUPPORTED_SCHEMA_VERSION), result)
    }

    @Test
    fun decode_rejectsTooLargeInput() {
        val result = ThemeCodec.decode(" ".repeat(ThemeCodec.MAX_IMPORT_CHARS + 1))
        assertEquals(ThemeDecodeResult.Failure(ThemeDecodeError.TOO_LARGE), result)
    }
}
