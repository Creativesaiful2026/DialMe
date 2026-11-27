package com.saiful.dialme.ui.dialpad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiful.dialme.core.data.contacts.Contact
import com.saiful.dialme.core.data.contacts.ContactsRepository
import com.saiful.dialme.core.data.search.ContactSearch
import com.saiful.dialme.core.data.search.Suggestion
import com.saiful.dialme.core.telecom.DialNumbers
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class DialpadViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
) : ViewModel() {

    private val _number = MutableStateFlow("")
    val number: StateFlow<String> = _number.asStateFlow()

    private val contacts: StateFlow<List<Contact>> = contactsRepository.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val suggestions: StateFlow<List<Suggestion>> = combine(contacts, _number) { list, input ->
        ContactSearch.dialpadSuggestions(list, input)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    /** Adds one key press. A "+" is only accepted as the first character. */
    fun append(ch: Char) {
        _number.update { current ->
            when {
                current.length >= DialNumbers.MAX_LENGTH -> current
                ch == '+' && current.isNotEmpty() -> current
                else -> current + ch
            }
        }
    }

    fun backspace() {
        _number.update { it.dropLast(1) }
    }

    fun clear() {
        _number.value = ""
    }

    /** Replaces the number with [raw] (clipboard, tel: link, suggestion). Returns false if invalid. */
    fun setNumber(raw: String): Boolean {
        val cleaned = DialNumbers.sanitize(raw) ?: return false
        _number.value = cleaned
        return true
    }

    fun onContactsPermissionChanged() {
        contactsRepository.reload()
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
