package com.tobibur.subalarm.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AlarmDetailScreen(alarmId: Int) {

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Add Alarm Screen for $alarmId",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "This is the add alarm screen view. #$alarmId",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }}