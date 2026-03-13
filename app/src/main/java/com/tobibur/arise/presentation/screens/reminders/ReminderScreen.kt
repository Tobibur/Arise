package com.tobibur.arise.presentation.screens.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tobibur.arise.domain.model.Reminder
import com.tobibur.arise.ui.theme.AriseTheme
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(viewModel: ReminderViewModel = hiltViewModel()) {
    val todayMillis = remember { normalizeToDay(System.currentTimeMillis()) }
    var selectedDateMillis by remember { mutableLongStateOf(todayMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<Reminder?>(null) }
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val reminders = viewModel.reminders.collectAsStateWithLifecycle()

    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
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

    if (showAddSheet) {
        AddReminderBottomSheet(
            sheetState = addSheetState,
            onDismiss = {
                showAddSheet = false
                editingReminder = null
            },
            existingReminder = editingReminder,
            onSave = { title, description, dateTimeMillis, repeatDays ->
                viewModel.save(title, description, dateTimeMillis, repeatDays, id = editingReminder?.id ?: 0)
                showAddSheet = false
                editingReminder = null
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ReminderContent(
            reminders = reminders.value,
            todayMillis = todayMillis,
            selectedDateMillis = selectedDateMillis,
            onDateSelected = { selectedDateMillis = it },
            onMonthClick = { showDatePicker = true },
            onReminderClick = { reminder ->
                editingReminder = reminder
                showAddSheet = true
            },
            onToggleCompleted = { id, completed -> viewModel.toggleCompleted(id, completed) }
        )

        FloatingActionButton(
            onClick = {
                editingReminder = null
                showAddSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Reminder")
        }
    }
}

@Composable
private fun ReminderContent(
    reminders: List<Reminder>,
    todayMillis: Long,
    selectedDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onMonthClick: () -> Unit,
    onReminderClick: (Reminder) -> Unit,
    onToggleCompleted: (Long, Boolean) -> Unit
) {
    val weekDays = remember(selectedDateMillis) { getWeekDays(selectedDateMillis) }
    val groupedReminders = remember(reminders, weekDays, todayMillis) {
        buildGroupedReminders(reminders, weekDays, todayMillis)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MonthHeader(
            dateMillis = selectedDateMillis,
            onMonthClick = onMonthClick
        )

        WeekStrip(
            selectedDateMillis = selectedDateMillis,
            todayMillis = todayMillis,
            onDateSelected = onDateSelected
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (groupedReminders.isEmpty()) {
            EmptyState()
        } else {
            GroupedReminderList(groupedReminders, todayMillis, onReminderClick, onToggleCompleted)
        }
    }
}

@Composable
private fun GroupedReminderList(
    groups: List<Pair<Long, List<Reminder>>>,
    todayMillis: Long,
    onReminderClick: (Reminder) -> Unit,
    onToggleCompleted: (Long, Boolean) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groups.forEach { (dateMillis, reminders) ->
            item(key = "header_$dateMillis") {
                DateSectionHeader(dateMillis, todayMillis)
            }
            items(reminders, key = { "${dateMillis}_${it.id}" }) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    onClick = onReminderClick,
                    onToggleCompleted = { toggled ->
                        onToggleCompleted(toggled.id, !toggled.isCompleted)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No reminders",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// region Preview

private fun sampleReminders(): List<Reminder> {
    val cal = Calendar.getInstance()

    cal.add(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 13); cal.set(Calendar.MINUTE, 0)
    val tomorrowAfternoon = cal.timeInMillis

    cal.add(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 14)
    val dayAfterAfternoon = cal.timeInMillis

    cal.set(Calendar.HOUR_OF_DAY, 16)
    val dayAfterEvening = cal.timeInMillis

    cal.add(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 9); cal.set(Calendar.MINUTE, 0)
    val laterMorning = cal.timeInMillis

    return listOf(
        Reminder(1, "Review documentation", "Check the API docs", tomorrowAfternoon),
        Reminder(
            2, "Opening paragraph", "Can refer to last week lecture notes", dayAfterAfternoon,
            repeatDays = 0b0010000
        ),
        Reminder(3, "Submit writing task", "Chapter 4", dayAfterEvening, isCompleted = true),
        Reminder(4, "Meet with tutor", "", laterMorning)
    )
}

@Preview(showBackground = true)
@Composable
private fun ReminderScreenPreview() {
    AriseTheme { ReminderScreen() }
}

// endregion
