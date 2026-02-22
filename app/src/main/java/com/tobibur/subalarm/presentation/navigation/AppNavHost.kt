package com.tobibur.subalarm.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tobibur.subalarm.presentation.components.BottomNavBar
import com.tobibur.subalarm.presentation.screens.alarms.AlarmDetailScreen
import com.tobibur.subalarm.presentation.screens.alarms.AlarmHomeScreen
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
    var onDoneClick by remember { mutableStateOf<(() -> Unit)?>(null) }

    Scaffold(
        topBar = {
            if (currentRoute == NavScreen.AlarmDetails.route) {
                val alarmId = navBackStackEntry?.arguments?.getLong("alarmId") ?: 0L
                val title = if (alarmId == 0L) "Add New Alarm" else "Edit Alarm"
                CenterAlignedTopAppBar(
                    title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        TextButton(onClick = { navController.navigateUp() }) {
                            Text(text = "Cancel")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            onDoneClick?.invoke()
                            navController.navigateUp()
                        }) {
                            Text(
                                text = "Done",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            } else {
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
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentRoute == NavScreen.Home.route,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    navController.navigate(
                        NavScreen.AlarmDetails.createRoute(0L)
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
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            composable(NavScreen.Home.route) {
                AlarmHomeScreen(onAlarmClick = { alarmId ->
                    navController.navigate(NavScreen.AlarmDetails.createRoute(alarmId))
                })
            }
            composable(
                route = NavScreen.AlarmDetails.route,
                arguments = listOf(navArgument("alarmId") { type = NavType.LongType })
            ) {
                AlarmDetailScreen(onDone = { callback -> onDoneClick = callback })
            }
            composable(NavScreen.Analytics.route) {

            }
            composable(NavScreen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}