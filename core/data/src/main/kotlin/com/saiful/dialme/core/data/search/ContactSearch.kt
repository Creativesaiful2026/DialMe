package com.saiful.dialme.core.data.search

import com.saiful.dialme.core.data.contacts.Contact
import com.saiful.dialme.core.data.contacts.ContactNumber

/** A contact together with the number that matched what the user typed. */
data class Suggestion(val contact: Contact, val number: ContactNumber)

object ContactSearch {

    /**
     * Text search for the Contacts screen. A query with letters matches names only; a query made of
     * digits and number punctuation matches names and numbers.
     */
    fun filter(contacts: List<Contact>, query: String): List<Contact> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return contacts
        val digits = T9.digitsOnly(trimmed)
        val searchNumbers = digits.isNotEmpty() && trimmed.none { it.isLetter() }
        return contacts.filter { contact ->
            contact.displayName.contains(trimmed, ignoreCase = true) ||
                (searchNumbers && contact.numbers.any { it.normalized.contains(digits) })
        }
    }

    /**
     * Suggestions while typing on the dialpad. Names whose word starts with the typed T9 digits come
     * first, then names that contain them, then contacts whose number contains them.
     */
    fun dialpadSuggestions(contacts: List<Contact>, input: String, limit: Int = 4): List<Suggestion> {
        val query = T9.digitsOnly(input)
        if (query.isEmpty() || limit <= 0) return emptyList()
        val ranked = ArrayList<Pair<Int, Suggestion>>()
        for (contact in contacts) {
            if (contact.numbers.isEmpty()) continue
            val nameRank = T9.nameRank(contact.displayName, query)
            val numberMatch = contact.numbers.firstOrNull { it.normalized.contains(query) }
            if (nameRank != null) {
                ranked.add(nameRank to Suggestion(contact, numberMatch ?: contact.numbers.first()))
            } else if (numberMatch != null) {
                ranked.add(2 to Suggestion(contact, numberMatch))
            }
        }
        return ranked
            .sortedWith(
                compareBy<Pair<Int, Suggestion>>({ it.first }, { it.second.contact.displayName.lowercase() }),
            )
            .take(limit)
            .map { it.second }
    }
}
