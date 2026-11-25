package com.saiful.dialme.ui

/** A request to dial that came from outside the app (tel: link or DIAL intent). */
data class DialRequest(val number: String, val token: Long)
