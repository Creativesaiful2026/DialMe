package com.saiful.dialme.core.common.logging

import android.content.Context
import android.util.Log
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * App-wide logger. Writes to Logcat and to a rotating file inside the app's private storage so
 * that logs can be viewed and shared from the app itself (no PC or adb needed).
 */
object DialMeLog {
    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
            .withZone(ZoneId.systemDefault())

    @Volatile
    private var logFile: RotatingLogFile? = null

    fun init(context: Context) {
        if (logFile == null) {
            logFile = RotatingLogFile(File(context.applicationContext.filesDir, "logs/dialme.log"))
        }
    }

    fun d(tag: String, message: String) = write('D', tag, message, null)
    fun i(tag: String, message: String) = write('I', tag, message, null)
    fun w(tag: String, message: String, throwable: Throwable? = null) = write('W', tag, message, throwable)
    fun e(tag: String, message: String, throwable: Throwable? = null) = write('E', tag, message, throwable)

    fun readLog(maxChars: Int = DEFAULT_READ_CHARS): String = logFile?.read(maxChars).orEmpty()

    fun clear() {
        logFile?.clear()
    }

    private fun write(level: Char, tag: String, message: String, throwable: Throwable?) {
        try {
            when (level) {
                'D' -> Log.d(tag, message)
                'I' -> Log.i(tag, message)
                'W' -> if (throwable != null) Log.w(tag, message, throwable) else Log.w(tag, message)
                else -> if (throwable != null) Log.e(tag, message, throwable) else Log.e(tag, message)
            }
            val stack = if (throwable != null) "\n" + Log.getStackTraceString(throwable) else ""
            logFile?.append("${formatter.format(Instant.now())} $level/$tag: $message$stack")
        } catch (e: Exception) {
            // Logging must never crash the app.
        }
    }

    const val DEFAULT_READ_CHARS = 50_000
}
