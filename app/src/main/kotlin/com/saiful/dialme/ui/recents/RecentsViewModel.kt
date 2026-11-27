package com.saiful.dialme.ui.recents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiful.dialme.core.data.calllog.CallLogGrouping
import com.saiful.dialme.core.data.calllog.CallLogGroup
import com.saiful.dialme.core.data.calllog.CallLogRepository
import com.saiful.dialme.core.data.contacts.Contact
import com.saiful.dialme.core.data.contacts.ContactsRepository
import com.saiful.dialme.core.data.search.PhoneKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

data class RecentsUiState(
    val hasPermission: Boolean,
    val groups: List<CallLogGroup>,
    val loading: Boolean,
)

@HiltViewModel
class RecentsViewModel @Inject constructor(
    private val callLog: CallLogRepository,
    private val contacts: ContactsRepository,
) : ViewModel() {

    private val permission = MutableStateFlow(callLog.hasPermission())

    val state: StateFlow<RecentsUiState> =
        combine(permission, callLog.observe(), contacts.observe()) { granted, entries, contactList ->
            val byKey = HashMap<String, Contact>()
            for (contact in contactList) {
                for (item in contact.numbers) {
                    byKey.putIfAbsent(PhoneKeys.matchKey(item.number), contact)
                }
            }
            val groups = CallLogGrouping.group(entries) { entry ->
                val saved = if (entry.number.isEmpty()) null else byKey[PhoneKeys.matchKey(entry.number)]?.displayName
                saved ?: entry.cachedName?.takeIf { it.isNotBlank() }
            }
            RecentsUiState(hasPermission = granted, groups = groups, loading = false)
        }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = RecentsUiState(
                    hasPermission = callLog.hasPermission(),
                    groups = emptyList(),
                    loading = true,
                ),
            )

    /** Call after the permission dialog closed or the screen came back to the foreground. */
    fun refreshPermission() {
        permission.value = callLog.hasPermission()
        callLog.reload()
        contacts.reload()
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
