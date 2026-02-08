package com.tobibur.subalarm.presentation.navigation

sealed class NavScreen(val route: String) {
    object Home : NavScreen("home")
    object Analytics : NavScreen("analytics")
    object Settings : NavScreen("settings")
    object AlarmDetails : NavScreen("alarmDetails/{alarmId}") {
        fun createRoute(alarmId: Int) = "alarmDetails/$alarmId"
    }
}