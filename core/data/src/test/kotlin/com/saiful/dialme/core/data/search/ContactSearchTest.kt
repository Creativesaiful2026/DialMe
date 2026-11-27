package com.saiful.dialme.core.data.search

import com.saiful.dialme.core.data.contacts.Contact
import com.saiful.dialme.core.data.contacts.ContactNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactSearchTest {

    private fun contact(id: Long, name: String, vararg numbers: String): Contact = Contact(
        id = id,
        lookupKey = null,
        displayName = name,
        photoThumbnailUri = null,
        isStarred = false,
        numbers = numbers.map { ContactNumber(it, T9.digitsOnly(it), null) },
    )

    private val ali = contact(1, "Ali Khan", "01711111111")
    private val bob = contact(2, "Bob", "01822222222", "01933333333")
    private val rina = contact(3, "Rina Akter", "01544444444")

    @Test
    fun emptyQueryReturnsEverything() {
        assertEquals(3, ContactSearch.filter(listOf(ali, bob, rina), "  ").size)
    }

    @Test
    fun letterQueryMatchesNamesOnly() {
        val result = ContactSearch.filter(listOf(ali, bob, rina), "akt")
        assertEquals(listOf(rina), result)
    }

    @Test
    fun digitQueryMatchesNumbers() {
        val result = ContactSearch.filter(listOf(ali, bob, rina), "0193")
        assertEquals(listOf(bob), result)
    }

    @Test
    fun dialpadSuggestionsRankNameBeforeNumber() {
        // "254" is the T9 code of "Ali"; "2" also appears inside Bob's number.
        val result = ContactSearch.dialpadSuggestions(listOf(bob, ali, rina), "254")
        assertEquals(1, result.size)
        assertEquals("Ali Khan", result[0].contact.displayName)
    }

    @Test
    fun dialpadSuggestionsReturnTheMatchingNumber() {
        val result = ContactSearch.dialpadSuggestions(listOf(bob), "0193")
        assertEquals(1, result.size)
        assertEquals("01933333333", result[0].number.number)
    }

    @Test
    fun dialpadSuggestionsAreLimitedAndEmptyForNoInput() {
        assertTrue(ContactSearch.dialpadSuggestions(listOf(ali, bob, rina), "").isEmpty())
        assertEquals(2, ContactSearch.dialpadSuggestions(listOf(ali, bob, rina), "0", limit = 2).size)
    }
}
