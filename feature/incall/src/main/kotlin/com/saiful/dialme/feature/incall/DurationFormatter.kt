package com.saiful.dialme.feature.incall

import java.util.Locale

object DurationFormatter {
    /** 65_000 -> "01:05", 3_725_000 -> "1:02:05". Negative values count as zero. */
    fun format(millis: Long): String {
        val totalSeconds = (if (millis < 0) 0L else millis) / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}
