package com.saiful.dialme

import android.app.Application
import android.os.Build
import com.saiful.dialme.core.common.logging.CrashHandler
import com.saiful.dialme.core.common.logging.DialMeLog
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DialMeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DialMeLog.init(this)
        CrashHandler.install()
        DialMeLog.i(
            "App",
            "DialMe ${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE}) started on " +
                "${Build.MANUFACTURER} ${Build.MODEL}, Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
        )
    }
}
