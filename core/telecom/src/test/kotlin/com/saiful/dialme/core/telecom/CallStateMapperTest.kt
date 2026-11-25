package com.saiful.dialme.core.telecom

import android.telecom.Call
import org.junit.Assert.assertEquals
import org.junit.Test

class CallStateMapperTest {

    @Test
    fun map_knownStates() {
        assertEquals(CallStatus.DIALING, CallStateMapper.map(Call.STATE_DIALING))
        assertEquals(CallStatus.RINGING, CallStateMapper.map(Call.STATE_RINGING))
        assertEquals(CallStatus.ACTIVE, CallStateMapper.map(Call.STATE_ACTIVE))
        assertEquals(CallStatus.HOLDING, CallStateMapper.map(Call.STATE_HOLDING))
        assertEquals(CallStatus.DISCONNECTED, CallStateMapper.map(Call.STATE_DISCONNECTED))
        assertEquals(CallStatus.CONNECTING, CallStateMapper.map(Call.STATE_CONNECTING))
    }

    @Test
    fun map_unknownNumberGivesUnknown() {
        assertEquals(CallStatus.UNKNOWN, CallStateMapper.map(12345))
    }
}
