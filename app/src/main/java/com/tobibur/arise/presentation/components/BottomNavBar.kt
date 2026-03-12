package com.tobibur.arise.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tobibur.arise.presentation.navigation.BottomNavItem

@Composable
fun BottomNavBar(
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar {
        BottomNavItem.items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemClick(item) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
