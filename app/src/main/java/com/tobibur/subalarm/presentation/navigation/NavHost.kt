package com.tobibur.subalarm.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tobibur.subalarm.presentation.components.BottomNavBar
import com.tobibur.subalarm.presentation.navigation.screens.home.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavHost() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavScreen.Home.route

    val topBarState = remember {
        mutableStateOf<@Composable (() -> Unit)?>({

            TopAppBar(
                title = {
                    Text(
                        text = "SubAlarm",
                    )
                }
            )
        })
    }

    Scaffold(
        topBar = { topBarState.value?.invoke() },
        floatingActionButton = {
            if (currentRoute == NavScreen.Home.route) {
                FloatingActionButton(onClick = {
                    navController.navigate(
                        NavScreen.AddAlarm.createRoute(
                            0
                        )
                    )
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Entry")
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onItemClick = { item ->
                    when (item) {
                        is BottomNavItem.Home -> navController.navigate(NavScreen.Home.route)
                        is BottomNavItem.Analytics -> navController.navigate(NavScreen.Analytics.route)
                        is BottomNavItem.Settings -> navController.navigate(NavScreen.Settings.route)
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavScreen.Home.route) {
                HomeScreen()
            }
//            composable(
//                route = NavScreen.AddAlarm.route,
//                arguments = listOf(navArgument("entryId") { type = NavType.IntType })
//            ) {
//
//            }
//            composable(
//                route = NavScreen.ViewAlarm.route,
//                arguments = listOf(navArgument("entryId") { type = NavType.IntType })
//            ) {
//            }
            composable(NavScreen.Analytics.route) {

            }
            composable(NavScreen.Settings.route) {

            }
        }
    }
}