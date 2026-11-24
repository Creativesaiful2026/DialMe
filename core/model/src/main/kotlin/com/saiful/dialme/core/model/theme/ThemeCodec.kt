package com.saiful.dialme.core.model.theme

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/** Why a theme file could not be imported. */
enum class ThemeDecodeError { TOO_LARGE, INVALID_JSON, UNSUPPORTED_SCHEMA_VERSION }

/** Result of importing a theme. Never throws. */
sealed interface ThemeDecodeResult {
    data class Success(val spec: ThemeSpec) : ThemeDecodeResult
    data class Failure(val error: ThemeDecodeError) : ThemeDecodeResult
}

/** Reads and writes [ThemeSpec] as JSON, validating everything that comes in. */
object ThemeCodec {
    const val MAX_IMPORT_CHARS = 100_000

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    fun encode(spec: ThemeSpec): String =
        json.encodeToString(ThemeSpec.serializer(), spec.sanitized())

    fun decode(text: String): ThemeDecodeResult {
        if (text.length > MAX_IMPORT_CHARS) {
            return ThemeDecodeResult.Failure(ThemeDecodeError.TOO_LARGE)
        }
        val spec = try {
            json.decodeFromString(ThemeSpec.serializer(), text)
        } catch (e: SerializationException) {
            return ThemeDecodeResult.Failure(ThemeDecodeError.INVALID_JSON)
        } catch (e: IllegalArgumentException) {
            return ThemeDecodeResult.Failure(ThemeDecodeError.INVALID_JSON)
        }
        if (spec.schemaVersion < 1 || spec.schemaVersion > ThemeSpec.CURRENT_SCHEMA_VERSION) {
            return ThemeDecodeResult.Failure(ThemeDecodeError.UNSUPPORTED_SCHEMA_VERSION)
        }
        return ThemeDecodeResult.Success(spec.sanitized())
    }
}
