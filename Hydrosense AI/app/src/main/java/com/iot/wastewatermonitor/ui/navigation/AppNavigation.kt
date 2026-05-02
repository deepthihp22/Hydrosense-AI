package com.iot.wastewatermonitor.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iot.wastewatermonitor.ui.screens.*
import com.iot.wastewatermonitor.ui.theme.AppColors
import com.iot.wastewatermonitor.viewmodel.SensorViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Alerts    : Screen("alerts",    "Alerts",    Icons.Filled.NotificationsActive)
    object History   : Screen("history",   "History",   Icons.Filled.History)
    object Settings  : Screen("settings",  "Settings",  Icons.Filled.Settings)
}

private val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Alerts,
    Screen.History,
    Screen.Settings
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: SensorViewModel = viewModel()

    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = {
            NavigationBar(
                containerColor = AppColors.Surface,
                tonalElevation = androidx.compose.ui.unit.Dp.Unspecified
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick  = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon  = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = AppColors.NeonCyan,
                            selectedTextColor   = AppColors.NeonCyan,
                            unselectedIconColor = AppColors.TextSecondary,
                            unselectedTextColor = AppColors.TextSecondary,
                            indicatorColor      = AppColors.NeonCyan.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Dashboard.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(viewModel) }
            composable(Screen.Alerts.route)    { AlertsScreen(viewModel) }
            composable(Screen.History.route)   { HistoryScreen(viewModel) }
            composable(Screen.Settings.route)  { SettingsScreen(viewModel) }
        }
    }
}
