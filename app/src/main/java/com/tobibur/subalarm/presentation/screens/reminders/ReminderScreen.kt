package com.tobibur.subalarm.presentation.screens.reminders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ReminderScreen() {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Reminder Screen",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "This is the reminder screen view.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}