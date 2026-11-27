package com.saiful.dialme.core.data.contacts

import android.content.Context
import android.provider.ContactsContract
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

/** Live, read-only view of the system contacts. Without the permission it emits an empty list. */
class ContactsRepository(context: Context) {
    private val appContext = context.applicationContext

    private val reloadSignal = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun hasPermission(): Boolean = ContactsQuery.hasPermission(appContext)

    /** Asks active collectors to read again, for example right after the permission was granted. */
    fun reload() {
        reloadSignal.tryEmit(Unit)
    }

    fun observe(): Flow<List<Contact>> =
        merge(appContext.contentResolver.changes(ContactsContract.Contacts.CONTENT_URI), reloadSignal)
            .onStart { emit(Unit) }
            .conflate()
            .map { ContactsQuery.load(appContext) }
            .flowOn(Dispatchers.IO)
}
