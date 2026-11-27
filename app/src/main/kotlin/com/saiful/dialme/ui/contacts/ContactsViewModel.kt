package com.saiful.dialme.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiful.dialme.core.data.contacts.Contact
import com.saiful.dialme.core.data.contacts.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ContactsUiState(
    val hasPermission: Boolean,
    val contacts: List<Contact>,
    val loading: Boolean,
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repository: ContactsRepository,
) : ViewModel() {

    private val permission = MutableStateFlow(repository.hasPermission())

    val state: StateFlow<ContactsUiState> = combine(permission, repository.observe()) { granted, list ->
        ContactsUiState(hasPermission = granted, contacts = list, loading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = ContactsUiState(
            hasPermission = repository.hasPermission(),
            contacts = emptyList(),
            loading = true,
        ),
    )

    /** Call after the permission dialog closed or the screen came back to the foreground. */
    fun refreshPermission() {
        permission.value = repository.hasPermission()
        repository.reload()
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
