package com.saiful.dialme.core.data.calllog

enum class CallType { INCOMING, OUTGOING, MISSED, REJECTED, BLOCKED, VOICEMAIL, OTHER }

/** One row of the system call log. [number] is empty for private or unknown callers. */
data class CallLogEntry(
    val id: Long,
    val number: String,
    val cachedName: String?,
    val type: CallType,
    val dateMillis: Long,
    val durationSeconds: Long,
    val phoneAccountId: String?,
)

/** Consecutive calls with the same number and type, newest first. */
data class CallLogGroup(
    val number: String,
    val displayName: String?,
    val type: CallType,
    val entries: List<CallLogEntry>,
) {
    val latest: CallLogEntry get() = entries.first()
    val count: Int get() = entries.size
}
