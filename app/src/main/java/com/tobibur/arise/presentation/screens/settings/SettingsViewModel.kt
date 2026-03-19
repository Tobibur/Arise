package com.tobibur.arise.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobibur.arise.domain.usecase.GetAllAlarmsUseCase
import com.tobibur.arise.domain.usecase.GetAllRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getAllAlarmsUseCase: GetAllAlarmsUseCase,
    getAllRemindersUseCase: GetAllRemindersUseCase
): ViewModel(){

    val alarms = getAllAlarmsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val reminders = getAllRemindersUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}