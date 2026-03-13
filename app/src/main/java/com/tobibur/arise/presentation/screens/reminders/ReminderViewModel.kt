package com.tobibur.arise.presentation.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobibur.arise.domain.model.Reminder
import com.tobibur.arise.domain.usecase.GetAllRemindersUseCase
import com.tobibur.arise.domain.usecase.GetReminderByIdUseCase
import com.tobibur.arise.domain.usecase.SaveReminderUseCase
import com.tobibur.arise.domain.usecase.ToggleReminderCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val getAllRemindersUseCase: GetAllRemindersUseCase,
    private val saveReminderUseCase: SaveReminderUseCase,
    private val toggleCompletedUseCase: ToggleReminderCompletedUseCase
): ViewModel() {
    val reminders: StateFlow<List<Reminder>> = getAllRemindersUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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