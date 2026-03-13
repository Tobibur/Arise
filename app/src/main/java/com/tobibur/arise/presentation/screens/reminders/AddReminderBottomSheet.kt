package com.tobibur.arise.presentation.screens.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.tobibur.arise.domain.model.Reminder
import java.util.Calendar

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
