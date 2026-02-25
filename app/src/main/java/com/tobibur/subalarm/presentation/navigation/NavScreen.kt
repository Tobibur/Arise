package com.tobibur.subalarm.presentation.navigation

sealed class NavScreen(val route: String) {
    object Home : NavScreen("home")
    object Reminder : NavScreen("reminders")
    object Settings : NavScreen("settings")
    object AlarmDetails : NavScreen("alarmDetails/{alarmId}") {
        fun createRoute(alarmId: Long) = "alarmDetails/$alarmId"
    }
}