package com.tobibur.subalarm.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(NavScreen.Home.route, Icons.Default.Alarm, "Alarm")
    object Analytics :
        BottomNavItem(NavScreen.Analytics.route, Icons.Default.DateRange, "Analytics")

    object Settings : BottomNavItem(NavScreen.Settings.route, Icons.Default.Settings, "Settings")

    companion object {
        val items = listOf(Home, Analytics, Settings)
    }
}
