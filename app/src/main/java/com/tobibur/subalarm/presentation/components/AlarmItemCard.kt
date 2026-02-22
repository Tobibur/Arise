package com.tobibur.subalarm.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.presentation.utils.formatSubAlarmTime
import com.tobibur.subalarm.presentation.utils.formatTime
import kotlinx.coroutines.NonCancellable.isActive

@Composable
fun AlarmItemCard(
    modifier: Modifier,
    alarm: Alarm,
    onSwitchChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val (time, amPm) = formatTime(alarm.time)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text(
                        text = time,
                        modifier = Modifier.alignByBaseline(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = amPm,
                        modifier = Modifier.alignByBaseline(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                SwitchWithIcon(
                    modifier = Modifier.scale(0.7f),
                    checked = alarm.isActive
                ) {
                    onSwitchChange(it)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = alarm.title,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (alarm.repeatDays > 0) {
                        ". ${getRepeatDaysString(alarm.repeatDays)}"
                    } else ", today",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AlarmAdd,
                        contentDescription = "Sub Alarm icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${alarm.subAlarms.size} sub alarms",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                if (alarm.subAlarms.isNotEmpty()) {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse sub alarms" else "Expand sub alarms"
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    alarm.subAlarms.forEach { subAlarm ->
                        val formattedTime = formatSubAlarmTime(alarm.time, subAlarm.time)
                        SubAlarmItemCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = subAlarm.title,
                            time = formattedTime,
                            isActive = subAlarm.isActive,
                            onDelete = {},
                            onClick = {}
                        )
                        if (subAlarm != alarm.subAlarms.last()) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

fun getRepeatDaysString(repeatDays: Int): String {
    return (0..6).filter { i -> repeatDays and (1 shl i) != 0 }.joinToString(", ") {
        getDayString(it)
    }
}

fun getDayString(day: Int): String {
    return when (day) {
        0 -> "Mon"
        1 -> "Tue"
        2 -> "Wed"
        3 -> "Thu"
        4 -> "Fri"
        5 -> "Sat"
        6 -> "Sun"
        else -> "(R)"
    }
}


@Preview
@Composable
fun AlarmItemCardPreview() {
    AlarmItemCard(
        modifier = Modifier,
        Alarm(
            id = 1,
            title = "Morning Alarm",
            time = System.currentTimeMillis(),
            subAlarms = emptyList(),
            isActive = true
        ),
        onSwitchChange = {}
    ) {}
}