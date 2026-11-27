package com.saiful.dialme.core.data

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.saiful.dialme.core.common.logging.DialMeLog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** Emits a value every time the data behind [uri] changes. */
internal fun ContentResolver.changes(uri: Uri): Flow<Unit> = callbackFlow {
    val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            trySend(Unit)
        }
    }
    try {
        registerContentObserver(uri, true, observer)
    } catch (e: SecurityException) {
        DialMeLog.w("ContentChanges", "Cannot observe $uri", e)
    }
    awaitClose { unregisterContentObserver(observer) }
}
