package com.tobibur.subalarm.presentation.screens.reminders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.tobibur.subalarm.domain.model.Reminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddReminderBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    existingReminder: Reminder? = null,
    onSave: (title: String, description: String, dateTimeMillis: Long, repeatDays: Int) -> Unit
) {
    val isEditing = existingReminder != null
    val scope = rememberCoroutineScope()

    val initialCal = remember(existingReminder) {
        Calendar.getInstance().apply {
            if (existingReminder != null) timeInMillis = existingReminder.dateTimeMillis
        }
    }

    var title by rememberSaveable { mutableStateOf(existingReminder?.title ?: "") }
    var description by rememberSaveable { mutableStateOf(existingReminder?.description ?: "") }
    var selectedDateMillis by rememberSaveable {
        mutableStateOf(
            if (existingReminder != null) normalizeToDay(existingReminder.dateTimeMillis)
            else normalizeToDay(System.currentTimeMillis())
        )
    }
    var repeatDays by rememberSaveable { mutableIntStateOf(existingReminder?.repeatDays ?: 0) }
    var isCustomRepeat by rememberSaveable {
        mutableStateOf(
            existingReminder != null && existingReminder.repeatDays > 0 &&
                    buildRepeatOptions(normalizeToDay(existingReminder.dateTimeMillis))
                        .none { it.daysBitmask == existingReminder.repeatDays }
        )
    }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    var selectedHour by rememberSaveable { mutableIntStateOf(initialCal.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by rememberSaveable { mutableIntStateOf(initialCal.get(Calendar.MINUTE)) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDateMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                showTimePicker = false
            }
        )
    }

    val titleFocusRequester = remember { FocusRequester() }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = if (isEditing) "Edit Reminder" else "New Reminder",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "TITLE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(8.dp))

            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocusRequester),
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
                        if (title.isEmpty()) {
                            Text(
                                text = "e.g. Call John",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )

            LaunchedEffect(Unit) {
                titleFocusRequester.requestFocus()
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "DESCRIPTION",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(8.dp))

            BasicTextField(
                value = description,
                onValueChange = { description = it },
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = false,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
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
                        if (description.isEmpty()) {
                            Text(
                                text = "e.g. Discuss about the presentation",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            DateTimeSelector(
                dateMillis = selectedDateMillis,
                hour = selectedHour,
                minute = selectedMinute,
                onDateClick = { showDatePicker = true },
                onTimeClick = { showTimePicker = true }
            )

            Spacer(Modifier.height(16.dp))

            RepeatOptionSelector(
                repeatDays = repeatDays,
                selectedDateMillis = selectedDateMillis,
                isCustom = isCustomRepeat,
                onRepeatChanged = { days, custom ->
                    repeatDays = days
                    isCustomRepeat = custom
                }
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                }) { Text("Cancel") }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        val dateTimeMillis = combineDateAndTime(
                            selectedDateMillis, selectedHour, selectedMinute
                        )
                        onSave(title.trim(), description.trim(), dateTimeMillis, repeatDays)
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    enabled = title.isNotBlank()
                ) { Text(if (isEditing) "Update" else "Save") }
            }
        }
    }
}

@Composable
private fun DateTimeSelector(
    dateMillis: Long,
    hour: Int,
    minute: Int,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()) }
    val dateFormatted = dateFormat.format(Date(dateMillis))
    val timeFormatted = formatTime12(hour, minute)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SelectorChip(
            icon = Icons.Default.CalendarToday,
            text = dateFormatted,
            onClick = onDateClick,
            modifier = Modifier.weight(1f)
        )
        SelectorChip(
            icon = Icons.Default.AccessTime,
            text = timeFormatted,
            onClick = onTimeClick
        )
    }
}

@Composable
private fun SelectorChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = { TimePicker(state = timePickerState) }
    )
}

private fun combineDateAndTime(dateMillis: Long, hour: Int, minute: Int): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

private fun formatTime12(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    return String.format(Locale.getDefault(), "%d:%02d %s", h, minute, amPm)
}

private data class RepeatOption(val label: String, val daysBitmask: Int)

private fun buildRepeatOptions(selectedDateMillis: Long): List<RepeatOption> {
    val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    // Map Calendar day to app bitmask index (Mon=0, Tue=1, ..., Sun=6)
    val bitIndex = (dayOfWeek + 5) % 7
    val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(cal.time)

    return listOf(
        RepeatOption("None", 0),
        RepeatOption("Every day", 0b1111111),
        RepeatOption("Every week on $dayName", 1 shl bitIndex),
        RepeatOption("Every weekday (Mon - Fri)", 0b0011111),
        RepeatOption("Every weekend (Sat - Sun)", 0b1100000),
    )
}

private fun repeatLabel(repeatDays: Int, selectedDateMillis: Long): String {
    return buildRepeatOptions(selectedDateMillis)
        .find { it.daysBitmask == repeatDays }?.label ?: "Custom"
}

private const val CUSTOM_SENTINEL = -1

@Composable
private fun RepeatOptionSelector(
    repeatDays: Int,
    selectedDateMillis: Long,
    isCustom: Boolean,
    onRepeatChanged: (days: Int, isCustom: Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = remember(selectedDateMillis) {
        buildRepeatOptions(selectedDateMillis) + RepeatOption("Custom", CUSTOM_SENTINEL)
    }
    val currentLabel = if (isCustom) "Custom" else remember(repeatDays, selectedDateMillis) {
        repeatLabel(repeatDays, selectedDateMillis)
    }

    Column {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Repeat",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = currentLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            if (option.daysBitmask == CUSTOM_SENTINEL) {
                                onRepeatChanged(repeatDays, true)
                            } else {
                                onRepeatChanged(option.daysBitmask, false)
                            }
                            expanded = false
                        }
                    )
                }
            }
        }

        AnimatedVisibility(visible = isCustom) {
            Column {
                Spacer(Modifier.height(8.dp))
                CustomDaySelector(
                    repeatDays = repeatDays,
                    onToggle = { index ->
                        onRepeatChanged(repeatDays xor (1 shl index), true)
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomDaySelector(
    repeatDays: Int,
    onToggle: (index: Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DAY_LABELS.forEachIndexed { index, label ->
            val selected = repeatDays and (1 shl index) != 0
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                    .clickable { onToggle(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
