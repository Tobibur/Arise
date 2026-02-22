package com.tobibur.subalarm.presentation.screens.alarms

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.presentation.components.AlarmItemCard
import com.tobibur.subalarm.ui.theme.SubAlarmTheme

@Composable
fun AlarmHomeScreen(onAlarmClick: (Long) -> Unit, viewModel: AlarmHomeViewModel = hiltViewModel()) {
    val alarms = viewModel.alarms.collectAsStateWithLifecycle()
    AlarmHomeContent(
        alarms = alarms.value,
        onAlarmClick = onAlarmClick,
        onToggleAlarm = viewModel::toggleAlarmActive
    )
}

@Composable
fun AlarmHomeContent(
    alarms: List<Alarm>,
    onAlarmClick: (Long) -> Unit,
    onToggleAlarm: (Long, Boolean) -> Unit
) {
    AnimatedContent(
        targetState = alarms.isEmpty(),
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "alarm_list_transition"
    ) { isEmpty ->
        if (isEmpty) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AlarmAdd,
                        contentDescription = "No alarms",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No alarms set,\n Please create an alarm",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmItemCard(
                        modifier = Modifier.animateItem(),
                        alarm,
                        onSwitchChange = { isActive ->
                            onToggleAlarm(alarm.id, isActive)
                        }
                    ) {
                        onAlarmClick(alarm.id)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyPreview() {
    SubAlarmTheme {
        AlarmHomeContent(
            alarms = emptyList(),
            onAlarmClick = {},
            onToggleAlarm = { _, _ -> }
        )
    }
}

@Preview
@Composable
fun HomeHomeScreenPreview() {
    SubAlarmTheme {
        AlarmHomeContent(
            alarms = listOf(
                Alarm(
                    id = 1,
                    title = "Morning Alarm",
                    time = System.currentTimeMillis(),
                    subAlarms = emptyList(),
                    isActive = true
                )
            ),
            onAlarmClick = {},
            onToggleAlarm = { _, _ -> }
        )
    }
}