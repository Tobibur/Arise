package com.tobibur.subalarm.presentation.screens.alarms

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tobibur.subalarm.presentation.components.AlarmTitleField
import com.tobibur.subalarm.presentation.components.CustomIconButton
import com.tobibur.subalarm.presentation.components.SubAlarmBottomSheet
import com.tobibur.subalarm.presentation.components.SubAlarmItemCard
import com.tobibur.subalarm.presentation.components.SwitchWithIcon
import com.tobibur.subalarm.presentation.utils.convertTime
import com.tobibur.subalarm.presentation.utils.formatSubAlarmTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetailScreen(
    onDone: (() -> Unit) -> Unit,
    viewModel: AlarmDetailsViewModel = hiltViewModel()
) {

    val alarm = viewModel.alarmUIState.collectAsStateWithLifecycle().value
    val calendar = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = false,
    )

    LaunchedEffect(alarm.time) {
        if (alarm.id != 0L) {
            calendar.timeInMillis = alarm.time
            timePickerState.hour = calendar.get(Calendar.HOUR_OF_DAY)
            timePickerState.minute = calendar.get(Calendar.MINUTE)
        }
    }

    LaunchedEffect(Unit) {
        onDone {
            viewModel.onTimeChanged(convertTime(timePickerState.hour, timePickerState.minute))
            viewModel.saveAlarm()
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            TimePickerLayout(timePickerState)
        }
        item {
            AlarmTitleField(title = alarm.title) {
                viewModel.onTitleChanged(it)
            }
        }
        item {
            RepeatAlarmLayout(
                repeatDays = alarm.repeatDays,
                onRepeatDaysChanged = { viewModel.onRepeatDaysChanged(it) }
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SUB ALARMS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                CustomIconButton(
                    onClick = {
                        showBottomSheet = !showBottomSheet
                    },
                    text = "ADD NEW",
                    icon = Icons.Default.AddCircleOutline
                )
            }
        }
        items(items = alarm.subAlarms, key = { it.uuid }) { subAlarm ->
            SubAlarmItemCard(
                modifier = Modifier,
                title = subAlarm.title,
                time = formatSubAlarmTime(alarm.time, subAlarm.time),
                isActive = subAlarm.isActive,
                onDelete = { viewModel.onRemoveSubAlarmItem(subAlarm.uuid) }
            ) { }
        }
    }

    if (showBottomSheet) {
        SubAlarmBottomSheet(
            alarm.time,
            sheetState, { showBottomSheet = it }) { time, title ->
            viewModel.onAddSubAlarmItem(title, time)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerLayout(timePickerState: TimePickerState) {
    Log.d("TAG", "TimePickerLayout: ${timePickerState.hour}")
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}


@Composable
fun RepeatAlarmLayout(repeatDays: Int, onRepeatDaysChanged: (Int) -> Unit) {
    val days = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    val todayIndex = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    var isRepeatEnabled by remember(repeatDays) { mutableStateOf(repeatDays != 0) }

    var selectedDays by remember(repeatDays) {
        mutableStateOf((0..6).filter { repeatDays and (1 shl it) != 0 }.toSet())
    }

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "REPEAT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = repeatSummary(selectedDays, isRepeatEnabled),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            SwitchWithIcon(
                modifier = Modifier.scale(0.7f),
                checked = isRepeatEnabled,
                onCheckedChange = {
                    isRepeatEnabled = it
                    if (!it) {
                        selectedDays = emptySet()
                        onRepeatDaysChanged(0)
                    } else {
                        selectedDays = setOf(todayIndex)
                        onRepeatDaysChanged(1 shl todayIndex)
                    }
                }
            )
        }

        AnimatedVisibility(visible = isRepeatEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                days.forEachIndexed { index, day ->
                    val isSelected = index in selectedDays
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        label = "chip_bg_$index"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        label = "chip_text_$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .clickable {
                                selectedDays =
                                    if (isSelected) selectedDays - index else selectedDays + index
                                val encoded = selectedDays.fold(0) { acc, i -> acc or (1 shl i) }
                                onRepeatDaysChanged(encoded)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

fun repeatSummary(selectedDays: Set<Int>, isEnabled: Boolean): String {
    if (!isEnabled || selectedDays.isEmpty()) return "Never"
    return when {
        selectedDays.size == 7 -> "Every day"
        selectedDays == setOf(0, 1, 2, 3, 4) -> "Weekdays"
        selectedDays == setOf(5, 6) -> "Weekends"
        else -> selectedDays.sorted().joinToString(", ") {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[it]
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AlarmDetailScreenPreview() {
    AlarmDetailScreen(onDone = {})
}