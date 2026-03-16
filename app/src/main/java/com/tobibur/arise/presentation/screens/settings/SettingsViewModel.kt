package com.tobibur.arise.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobibur.arise.domain.usecase.GetAllAlarmsUseCase
import com.tobibur.arise.domain.usecase.GetAllRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getAllAlarmsUseCase: GetAllAlarmsUseCase,
    getAllRemindersUseCase: GetAllRemindersUseCase
): ViewModel(){

    val alarms = getAllAlarmsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val remindersFlow = getAllRemindersUseCase()

    val reminders = remindersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedRemindersCount = remindersFlow
        .map { reminders -> reminders.count { it.isCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val upcomingRemindersCount = remindersFlow
        .map { reminders -> reminders.count { !it.isCompleted && it.dateTimeMillis > System.currentTimeMillis() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}