package com.tobibur.subalarm.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tobibur.subalarm.data.DummyAlarms.alarms
import com.tobibur.subalarm.presentation.components.AlarmItemCard
import com.tobibur.subalarm.ui.theme.SubAlarmTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlarmHomeScreen(onAlarmClick: (Int) -> Unit) {

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(alarms, key = {
            it.id
        }) { alarm ->
            val (time, amPm) = formatTime(alarm.time)
            AlarmItemCard(
                modifier = Modifier,
                title = alarm.title,
                time = time,
                amOrPm = amPm,
                subAlarmCount = alarm.subAlarms.size,
                isActive = alarm.isActive,
                onSwitchChange = { !alarm.isActive }
            ) {
                // On card click, navigate to view alarm screen
                onAlarmClick(alarm.id)
            }
        }
    }
}

fun formatTime(time: Long): Pair<String, String> {
    val date = Date(time)
    val timePart = SimpleDateFormat("hh:mm", Locale.getDefault()).format(date)
    val amPm = SimpleDateFormat("a", Locale.getDefault()).format(date)
    return Pair(timePart, amPm)
}

@Preview
@Composable
fun HomeHomeScreenPreview() {
    SubAlarmTheme {
        AlarmHomeScreen({})
    }
}