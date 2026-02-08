package com.tobibur.subalarm.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tobibur.subalarm.presentation.components.BottomNavBar
import com.tobibur.subalarm.presentation.screens.home.AlarmDetailScreen
import com.tobibur.subalarm.presentation.screens.home.AlarmHomeScreen
import com.tobibur.subalarm.presentation.screens.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavScreen.Home.route

    val mainTabs = listOf(NavScreen.Home.route, NavScreen.Analytics.route, NavScreen.Settings.route)

    val canGoBack =
        currentRoute !in mainTabs && navController.previousBackStackEntry != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            NavScreen.Home.route -> "SubAlarm"
                            NavScreen.Analytics.route -> "Analytics"
                            NavScreen.Settings.route -> "Settings"
                            else -> "SubAlarm"
                        }
                    )
                },
                navigationIcon = {
                    if (canGoBack) {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentRoute == NavScreen.Home.route) {
                FloatingActionButton(onClick = {
                    navController.navigate(
                        NavScreen.AlarmDetails.createRoute(
                            0
                        )
                    )
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Alarm")
                }
            }
        },
        bottomBar = {
            if (currentRoute in listOf(
                    NavScreen.Home.route,
                    NavScreen.Analytics.route,
                    NavScreen.Settings.route
                )
            ) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick = {
                        navController.navigate(it.route) {
                            popUpTo(NavScreen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavScreen.Home.route) {
                AlarmHomeScreen(onAlarmClick = { alarmId ->
                    navController.navigate(NavScreen.AlarmDetails.createRoute(alarmId))
                })
            }
            composable(
                route = NavScreen.AlarmDetails.route,
                arguments = listOf(navArgument("alarmId") { type = NavType.IntType })
            ) {
                val alarmId = it.arguments?.getInt("alarmId") ?: 0
                AlarmDetailScreen(alarmId)
            }
            composable(NavScreen.Analytics.route) {

            }
            composable(NavScreen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}