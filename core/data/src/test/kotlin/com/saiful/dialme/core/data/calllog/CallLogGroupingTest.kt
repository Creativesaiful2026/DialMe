package com.saiful.dialme.core.data.calllog

import org.junit.Assert.assertEquals
import org.junit.Test

class CallLogGroupingTest {

    private fun entry(id: Long, number: String, type: CallType, name: String? = null) = CallLogEntry(
        id = id,
        number = number,
        cachedName = name,
        type = type,
        dateMillis = 1_000_000L - id,
        durationSeconds = 10,
        phoneAccountId = null,
    )

    @Test
    fun consecutiveSameNumberAndTypeAreGrouped() {
        val groups = CallLogGrouping.group(
            listOf(
                entry(1, "01711111111", CallType.MISSED),
                entry(2, "+8801711111111", CallType.MISSED),
                entry(3, "01711111111", CallType.INCOMING),
                entry(4, "01822222222", CallType.INCOMING),
            ),
        )
        assertEquals(3, groups.size)
        assertEquals(2, groups[0].count)
        assertEquals(1L, groups[0].latest.id)
        assertEquals(1, groups[1].count)
    }

    @Test
    fun nonConsecutiveCallsStaySeparate() {
        val groups = CallLogGrouping.group(
            listOf(
                entry(1, "01711111111", CallType.OUTGOING),
                entry(2, "01822222222", CallType.OUTGOING),
                entry(3, "01711111111", CallType.OUTGOING),
            ),
        )
        assertEquals(3, groups.size)
    }

    @Test
    fun nameComesFromTheLookupFunction() {
        val groups = CallLogGrouping.group(listOf(entry(1, "01711111111", CallType.INCOMING, name = "Cached"))) {
            "Saved"
        }
        assertEquals("Saved", groups[0].displayName)
    }

    @Test
    fun defaultNameIgnoresBlankCachedNames() {
        val groups = CallLogGrouping.group(listOf(entry(1, "01711111111", CallType.INCOMING, name = " ")))
        assertEquals(null, groups[0].displayName)
    }

    @Test
    fun emptyInputGivesNoGroups() {
        assertEquals(0, CallLogGrouping.group(emptyList()).size)
    }
}
