package com.saiful.dialme.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.saiful.dialme.core.model.theme.ThemeSpec
import com.saiful.dialme.ui.contacts.ContactsScreen
import com.saiful.dialme.ui.contacts.FavoritesScreen
import com.saiful.dialme.ui.dialpad.DialpadScreen
import com.saiful.dialme.ui.navigation.ROUTE_DIAGNOSTICS
import com.saiful.dialme.ui.navigation.ROUTE_TELECOM_SPIKE
import com.saiful.dialme.ui.navigation.TopLevelDestination
import com.saiful.dialme.ui.recents.RecentsScreen
import com.saiful.dialme.ui.screens.DiagnosticsScreen
import com.saiful.dialme.ui.screens.SettingsScreen
import com.saiful.dialme.ui.screens.TelecomSpikeScreen

@Composable
fun DialMeApp(
    themeSpec: ThemeSpec,
    onThemeChange: ((ThemeSpec) -> ThemeSpec) -> Unit,
    onThemeReset: () -> Unit,
    dialRequest: DialRequest?,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = TopLevelDestination.entries.any { it.route == currentRoute }

    // A tel: link or DIAL intent from another app opens the Dialpad with the number filled in.
    LaunchedEffect(dialRequest) {
        if (dialRequest != null) {
            navController.navigate(TopLevelDestination.DIALPAD.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(stringResource(destination.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.DIALPAD.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TopLevelDestination.FAVORITES.route) { FavoritesScreen() }
            composable(TopLevelDestination.CONTACTS.route) { ContactsScreen() }
            composable(TopLevelDestination.RECENTS.route) { RecentsScreen() }
            composable(TopLevelDestination.DIALPAD.route) { DialpadScreen(dialRequest = dialRequest) }
            composable(TopLevelDestination.SETTINGS.route) {
                SettingsScreen(
                    themeSpec = themeSpec,
                    onThemeChange = onThemeChange,
                    onThemeReset = onThemeReset,
                    onOpenDiagnostics = { navController.navigate(ROUTE_DIAGNOSTICS) },
                    onOpenTelecomSpike = { navController.navigate(ROUTE_TELECOM_SPIKE) },
                )
            }
            composable(ROUTE_DIAGNOSTICS) {
                DiagnosticsScreen(onBack = { navController.popBackStack() })
            }
            composable(ROUTE_TELECOM_SPIKE) {
                TelecomSpikeScreen(
                    initialNumber = null,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
