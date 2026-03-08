package com.tobibur.subalarm.presentation.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobibur.subalarm.domain.model.Reminder
import com.tobibur.subalarm.domain.usecase.GetAllRemindersUseCase
import com.tobibur.subalarm.domain.usecase.GetReminderByIdUseCase
import com.tobibur.subalarm.domain.usecase.SaveReminderUseCase
import com.tobibur.subalarm.domain.usecase.ToggleReminderCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val getAllRemindersUseCase: GetAllRemindersUseCase,
    private val getReminderByIdUseCase: GetReminderByIdUseCase,
    private val saveReminderUseCase: SaveReminderUseCase,
    private val toggleCompletedUseCase: ToggleReminderCompletedUseCase
): ViewModel() {
    val reminders: StateFlow<List<Reminder>> = getAllRemindersUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDateMillis = MutableStateFlow(normalizeToDay(System.currentTimeMillis()))
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    fun selectDate(millis: Long) { _selectedDateMillis.value = millis }
    fun save(title: String, description: String, dateTimeMillis: Long, repeatDays: Int, id: Long = 0) {
        viewModelScope.launch { saveReminderUseCase(
            Reminder(
                id = id,
                title = title,
                description = description,
                dateTimeMillis = dateTimeMillis,
                repeatDays = repeatDays
            )
        ) }
    }
    fun toggleCompleted(id: Long, completed: Boolean) { viewModelScope.launch { toggleCompletedUseCase(id, completed) } }
}