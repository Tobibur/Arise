package com.tobibur.subalarm.presentation.screens.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.domain.usecase.GetAllAlarmsUseCase
import com.tobibur.subalarm.domain.usecase.ToggleAlarmActiveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmHomeViewModel @Inject constructor(
    private val getAllAlarmsUseCase: GetAllAlarmsUseCase,
    private val toggleAlarmActiveUseCase: ToggleAlarmActiveUseCase
) : ViewModel() {

    val alarms: StateFlow<List<Alarm>> =
        getAllAlarmsUseCase().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

    fun toggleAlarmActive(alarmId: Long, isActive: Boolean) {
        viewModelScope.launch {
            toggleAlarmActiveUseCase(alarmId, isActive)
        }
    }
}