package com.saiful.dialme.core.data.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.saiful.dialme.core.common.logging.DialMeLog
import com.saiful.dialme.core.data.search.T9

/** Reads contacts that have a phone number from the system Contacts Provider (read only). */
internal object ContactsQuery {
    private const val TAG = "ContactsQuery"

    fun hasPermission(context: Context): Boolean =
        context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

    private class Builder(
        val id: Long,
        val lookupKey: String?,
        val displayName: String,
        val photoThumbnailUri: String?,
        val isStarred: Boolean,
    ) {
        val numbers = ArrayList<ContactNumber>()

        fun build() = Contact(
            id = id,
            lookupKey = lookupKey,
            displayName = displayName,
            photoThumbnailUri = photoThumbnailUri,
            isStarred = isStarred,
            numbers = numbers,
        )
    }

    fun load(context: Context): List<Contact> {
        if (!hasPermission(context)) return emptyList()
        val projection = arrayOf(
            Phone.CONTACT_ID,
            Phone.LOOKUP_KEY,
            Phone.DISPLAY_NAME,
            Phone.NUMBER,
            Phone.TYPE,
            Phone.LABEL,
            Phone.PHOTO_THUMBNAIL_URI,
            Phone.STARRED,
        )
        val builders = LinkedHashMap<Long, Builder>()
        try {
            val cursor = context.contentResolver.query(
                Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${Phone.DISPLAY_NAME} COLLATE LOCALIZED ASC",
            ) ?: return emptyList()
            cursor.use { c ->
                val idCol = c.getColumnIndexOrThrow(Phone.CONTACT_ID)
                val lookupCol = c.getColumnIndexOrThrow(Phone.LOOKUP_KEY)
                val nameCol = c.getColumnIndexOrThrow(Phone.DISPLAY_NAME)
                val numberCol = c.getColumnIndexOrThrow(Phone.NUMBER)
                val typeCol = c.getColumnIndexOrThrow(Phone.TYPE)
                val labelCol = c.getColumnIndexOrThrow(Phone.LABEL)
                val photoCol = c.getColumnIndexOrThrow(Phone.PHOTO_THUMBNAIL_URI)
                val starCol = c.getColumnIndexOrThrow(Phone.STARRED)
                while (c.moveToNext()) {
                    val number = c.getString(numberCol)?.trim().orEmpty()
                    if (number.isEmpty()) continue
                    val id = c.getLong(idCol)
                    val builder = builders.getOrPut(id) {
                        Builder(
                            id = id,
                            lookupKey = c.getString(lookupCol),
                            displayName = c.getString(nameCol)?.takeIf { it.isNotBlank() } ?: number,
                            photoThumbnailUri = c.getString(photoCol),
                            isStarred = c.getInt(starCol) == 1,
                        )
                    }
                    val normalized = T9.digitsOnly(number)
                    if (normalized.isNotEmpty() && builder.numbers.any { it.normalized == normalized }) continue
                    val typeLabel = Phone.getTypeLabel(context.resources, c.getInt(typeCol), c.getString(labelCol))
                        ?.toString()
                    builder.numbers.add(ContactNumber(number, normalized, typeLabel))
                }
            }
        } catch (e: SecurityException) {
            DialMeLog.w(TAG, "No permission to read contacts", e)
            return emptyList()
        } catch (e: Exception) {
            DialMeLog.e(TAG, "Could not read contacts", e)
            return emptyList()
        }
        return builders.values.map { it.build() }
    }
}
