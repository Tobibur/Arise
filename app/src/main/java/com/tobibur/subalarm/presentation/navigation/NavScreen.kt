package com.tobibur.subalarm.presentation.navigation

sealed class NavScreen(val route: String) {
    object Home : NavScreen("home")
    object Analytics : NavScreen("analytics")
    object Settings : NavScreen("settings")
    object AddAlarm : NavScreen("addAlarm/{alarmId}") {
        fun createRoute(alarmId: Int) = "addAlarm/$alarmId"
    }
    object ViewAlarm : NavScreen("viewAlarm")
}