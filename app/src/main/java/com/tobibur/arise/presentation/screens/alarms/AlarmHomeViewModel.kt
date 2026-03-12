package com.tobibur.arise.presentation.screens.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobibur.arise.domain.model.Alarm
import com.tobibur.arise.domain.usecase.DeleteAlarmUseCase
import com.tobibur.arise.domain.usecase.GetAllAlarmsUseCase
import com.tobibur.arise.domain.usecase.ToggleAlarmActiveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmHomeViewModel @Inject constructor(
    private val getAllAlarmsUseCase: GetAllAlarmsUseCase,
    private val toggleAlarmActiveUseCase: ToggleAlarmActiveUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase
) : ViewModel() {

    val alarms: StateFlow<List<Alarm>> =
        getAllAlarmsUseCase().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

    fun toggleAlarmActive(alarm: Alarm, isActive: Boolean) {
        viewModelScope.launch {
            toggleAlarmActiveUseCase(alarm, isActive)
        }
    }

    fun deleteAlarm(alarmId: Long) {
        viewModelScope.launch {
            deleteAlarmUseCase(alarmId)
        }
    }
}