package com.saiful.dialme.core.common.logging

import java.io.File

/**
 * Small append-only text log with a size limit. When the file grows past [maxBytes] it is moved
 * to "<name>.old" (replacing any older copy) and a fresh file is started.
 *
 * Logging must never crash the app, so every I/O problem is swallowed.
 */
class RotatingLogFile(
    private val file: File,
    private val maxBytes: Long = DEFAULT_MAX_BYTES,
) {
    private val lock = Any()

    fun append(line: String) {
        synchronized(lock) {
            try {
                file.parentFile?.mkdirs()
                if (file.exists() && file.length() > maxBytes) {
                    rotate()
                }
                file.appendText(line + "\n")
            } catch (e: Exception) {
                // Intentionally ignored.
            }
        }
    }

    /** Returns at most [maxChars] characters from the end of the log. */
    fun read(maxChars: Int): String {
        synchronized(lock) {
            return try {
                if (!file.exists()) {
                    ""
                } else {
                    val text = file.readText()
                    if (text.length <= maxChars) text else text.takeLast(maxChars)
                }
            } catch (e: Exception) {
                ""
            }
        }
    }

    fun clear() {
        synchronized(lock) {
            try {
                file.delete()
                File(file.path + ".old").delete()
            } catch (e: Exception) {
                // Intentionally ignored.
            }
        }
    }

    private fun rotate() {
        val old = File(file.path + ".old")
        if (old.exists()) old.delete()
        file.renameTo(old)
    }

    companion object {
        const val DEFAULT_MAX_BYTES = 512 * 1024L
    }
}
