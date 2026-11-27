package com.saiful.dialme.core.data.calllog

import com.saiful.dialme.core.data.search.PhoneKeys

object CallLogGrouping {

    /**
     * Groups consecutive entries that have the same number and type. [entries] must be sorted
     * newest first. [nameFor] supplies the name to show for a call, or null when unknown.
     */
    fun group(
        entries: List<CallLogEntry>,
        nameFor: (CallLogEntry) -> String? = { it.cachedName?.takeIf { name -> name.isNotBlank() } },
    ): List<CallLogGroup> {
        val groups = ArrayList<CallLogGroup>()
        for (entry in entries) {
            val last = groups.lastOrNull()
            if (last != null && last.type == entry.type &&
                PhoneKeys.matchKey(last.number) == PhoneKeys.matchKey(entry.number)
            ) {
                groups[groups.lastIndex] = last.copy(entries = last.entries + entry)
            } else {
                groups.add(
                    CallLogGroup(
                        number = entry.number,
                        displayName = nameFor(entry),
                        type = entry.type,
                        entries = listOf(entry),
                    ),
                )
            }
        }
        return groups
    }
}
