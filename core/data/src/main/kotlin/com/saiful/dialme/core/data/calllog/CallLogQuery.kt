package com.saiful.dialme.core.data.calllog

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
import com.saiful.dialme.core.common.logging.DialMeLog

/** Reads the system call log, newest first (read only). */
internal object CallLogQuery {
    private const val TAG = "CallLogQuery"

    fun hasPermission(context: Context): Boolean =
        context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED

    fun typeOf(value: Int): CallType = when (value) {
        CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
        CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
        CallLog.Calls.MISSED_TYPE -> CallType.MISSED
        CallLog.Calls.REJECTED_TYPE -> CallType.REJECTED
        CallLog.Calls.BLOCKED_TYPE -> CallType.BLOCKED
        CallLog.Calls.VOICEMAIL_TYPE -> CallType.VOICEMAIL
        else -> CallType.OTHER
    }

    fun load(context: Context, limit: Int): List<CallLogEntry> {
        if (!hasPermission(context)) return emptyList()
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.PHONE_ACCOUNT_ID,
        )
        val args = Bundle().apply {
            putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
            putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(CallLog.Calls.DATE))
            putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
        }
        val result = ArrayList<CallLogEntry>()
        try {
            val cursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI, projection, args, null)
                ?: return emptyList()
            cursor.use { c ->
                val idCol = c.getColumnIndexOrThrow(CallLog.Calls._ID)
                val numberCol = c.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                val nameCol = c.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
                val typeCol = c.getColumnIndexOrThrow(CallLog.Calls.TYPE)
                val dateCol = c.getColumnIndexOrThrow(CallLog.Calls.DATE)
                val durationCol = c.getColumnIndexOrThrow(CallLog.Calls.DURATION)
                val accountCol = c.getColumnIndexOrThrow(CallLog.Calls.PHONE_ACCOUNT_ID)
                while (c.moveToNext()) {
                    result.add(
                        CallLogEntry(
                            id = c.getLong(idCol),
                            number = c.getString(numberCol).orEmpty(),
                            cachedName = c.getString(nameCol),
                            type = typeOf(c.getInt(typeCol)),
                            dateMillis = c.getLong(dateCol),
                            durationSeconds = c.getLong(durationCol),
                            phoneAccountId = c.getString(accountCol),
                        ),
                    )
                }
            }
        } catch (e: SecurityException) {
            DialMeLog.w(TAG, "No permission to read the call log", e)
            return emptyList()
        } catch (e: Exception) {
            DialMeLog.e(TAG, "Could not read the call log", e)
            return emptyList()
        }
        return result
    }
}
