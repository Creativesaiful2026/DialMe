package com.saiful.dialme.core.common.logging

/** Records uncaught exceptions in [DialMeLog] before handing them to the system's default handler. */
object CrashHandler {
    fun install() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                DialMeLog.e("CRASH", "Uncaught exception on thread ${thread.name}", throwable)
            } catch (e: Throwable) {
                // Never let logging hide the original crash.
            }
            previous?.uncaughtException(thread, throwable)
        }
    }
}
