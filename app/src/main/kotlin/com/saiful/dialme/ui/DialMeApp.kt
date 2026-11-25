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
import com.saiful.dialme.R
import com.saiful.dialme.core.model.theme.ThemeSpec
import com.saiful.dialme.ui.navigation.ROUTE_DIAGNOSTICS
import com.saiful.dialme.ui.navigation.ROUTE_TELECOM_SPIKE
import com.saiful.dialme.ui.navigation.TopLevelDestination
import com.saiful.dialme.ui.screens.DiagnosticsScreen
import com.saiful.dialme.ui.screens.PlaceholderScreen
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

    // A tel: link or DIAL intent from another app opens the Telecom spike screen with the number.
    LaunchedEffect(dialRequest) {
        if (dialRequest != null) {
            navController.navigate(ROUTE_TELECOM_SPIKE) { launchSingleTop = true }
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
            composable(TopLevelDestination.FAVORITES.route) {
                PlaceholderScreen(titleRes = R.string.nav_favorites, plannedPhase = 4)
            }
            composable(TopLevelDestination.CONTACTS.route) {
                PlaceholderScreen(titleRes = R.string.nav_contacts, plannedPhase = 4)
            }
            composable(TopLevelDestination.RECENTS.route) {
                PlaceholderScreen(titleRes = R.string.nav_recents, plannedPhase = 4)
            }
            composable(TopLevelDestination.DIALPAD.route) {
                PlaceholderScreen(titleRes = R.string.nav_dialpad, plannedPhase = 3)
            }
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
                    initialNumber = dialRequest?.number,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
