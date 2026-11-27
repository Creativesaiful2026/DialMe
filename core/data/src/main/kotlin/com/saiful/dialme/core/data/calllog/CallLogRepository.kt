package com.saiful.dialme.core.data.calllog

import android.content.Context
import android.provider.CallLog
import com.saiful.dialme.core.data.changes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart

/** Live, read-only view of the system call log. Without the permission it emits an empty list. */
class CallLogRepository(context: Context) {
    private val appContext = context.applicationContext

    private val reloadSignal = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun hasPermission(): Boolean = CallLogQuery.hasPermission(appContext)

    /** Asks active collectors to read again, for example right after the permission was granted. */
    fun reload() {
        reloadSignal.tryEmit(Unit)
    }

    fun observe(limit: Int = DEFAULT_LIMIT): Flow<List<CallLogEntry>> =
        merge(appContext.contentResolver.changes(CallLog.Calls.CONTENT_URI), reloadSignal)
            .onStart { emit(Unit) }
            .conflate()
            .map { CallLogQuery.load(appContext, limit) }
            .flowOn(Dispatchers.IO)

    companion object {
        const val DEFAULT_LIMIT = 500
    }
}
