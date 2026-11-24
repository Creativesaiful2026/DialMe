package com.saiful.dialme.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.saiful.dialme.R

/** The five bottom-bar sections. Routes are plain strings in Phase 1. */
enum class TopLevelDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    FAVORITES("favorites", R.string.nav_favorites, Icons.Filled.Star),
    CONTACTS("contacts", R.string.nav_contacts, Icons.Filled.Person),
    RECENTS("recents", R.string.nav_recents, Icons.Filled.DateRange),
    DIALPAD("dialpad", R.string.nav_dialpad, Icons.Filled.Call),
    SETTINGS("settings", R.string.nav_settings, Icons.Filled.Settings),
}

const val ROUTE_DIAGNOSTICS = "diagnostics"
