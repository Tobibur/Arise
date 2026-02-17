package com.tobibur.subalarm.presentation.screens.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.domain.usecase.GetAllAlarmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlarmHomeViewModel @Inject constructor(
    private val getAllAlarmsUseCase: GetAllAlarmsUseCase,
): ViewModel(){

    val alarms: StateFlow<List<Alarm>> =
        getAllAlarmsUseCase().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
}