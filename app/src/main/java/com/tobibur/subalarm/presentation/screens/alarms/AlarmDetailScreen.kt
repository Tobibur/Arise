package com.tobibur.subalarm.presentation.screens.alarms

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tobibur.subalarm.presentation.components.CustomIconButton
import com.tobibur.subalarm.presentation.components.SubAlarmItemCard
import com.tobibur.subalarm.presentation.components.SwitchWithIcon
import com.tobibur.subalarm.presentation.utils.convertTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

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
            AlarmTitleField(alarm.title) {
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
                    onClick = {},
                    text = "ADD NEW",
                    icon = Icons.Default.AddCircleOutline
                )
            }
        }
        items(items = alarm.subAlarms, key = { it.id }) { subAlarm ->
            SubAlarmItemCard(
                modifier = Modifier,
                title = subAlarm.title,
                time = formatSubAlarmTime(alarm.time, subAlarm.time),
                isActive = subAlarm.isActive,
                onSwitchChange = { !subAlarm.isActive }
            ) { }
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
fun AlarmTitleField(title: String = "", onTitleChanged: (String) -> Unit) {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ALARM TITLE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            CustomIconButton(
                onClick = {},
                text = "OPTIONS",
                icon = Icons.Default.Settings
            )
        }
        Spacer(Modifier.height(8.dp))

        BasicTextField(
            value = title,
            onValueChange = { newText ->
                onTitleChanged(newText)
            },
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            decorationBox = { innerTextField ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {

                    // Hint / Placeholder — visible only when empty, disappears on typing
                    if (title.isEmpty()) {
                        Text(
                            text = "e.g. Work Morning",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        )
                    }

                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun RepeatAlarmLayout(repeatDays: Int, onRepeatDaysChanged: (Int) -> Unit) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val todayIndex = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    var isRepeatEnabled by remember(repeatDays) { mutableStateOf(repeatDays != 0) }

    var selectedDays by remember(repeatDays) {
        mutableStateOf((0..6).filter { repeatDays and (1 shl it) != 0 }.toSet())
    }


    Log.d("TAG", "RepeatAlarmLayout: repeat days: $repeatDays, repeat enabled: $isRepeatEnabled")


    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REPEAT",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
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

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEachIndexed { index, day ->
                val isSelected = index in selectedDays

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable(enabled = isRepeatEnabled) {
                            selectedDays = if (isSelected) {
                                selectedDays - index
                            } else {
                                selectedDays + index
                            }
                            val encoded = selectedDays.fold(0) { acc, i -> acc or (1 shl i) }
                            onRepeatDaysChanged(encoded)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                }
            }
        }
    }
}

private fun formatSubAlarmTime(alarmTime: Long, subAlarmTime: Long): String {
    val diffMinutes = abs(subAlarmTime - alarmTime) / 60_000
    val sign = if (subAlarmTime >= alarmTime) "+" else "-"
    val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(subAlarmTime))
    return "$sign$diffMinutes min ($timeFormatted)"
}


@Preview(showBackground = true)
@Composable
fun AlarmDetailScreenPreview() {
    AlarmDetailScreen(onDone = {})
}