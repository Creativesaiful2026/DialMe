package com.saiful.dialme.core.data.contacts

/** One phone number of a contact. [normalized] holds ASCII digits only and is used for matching. */
data class ContactNumber(
    val number: String,
    val normalized: String,
    val typeLabel: String?,
)

/** A contact as read from the system Contacts Provider. DialMe never changes this data. */
data class Contact(
    val id: Long,
    val lookupKey: String?,
    val displayName: String,
    val photoThumbnailUri: String?,
    val isStarred: Boolean,
    val numbers: List<ContactNumber>,
)
