package com.tobibur.subalarm.presentation.screens.alarms

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.domain.model.SubAlarm
import com.tobibur.subalarm.domain.usecase.GetAlarmByIdUseCase
import com.tobibur.subalarm.domain.usecase.SaveAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmDetailsViewModel @Inject constructor(
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val saveAlarmUseCase: SaveAlarmUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val alarmId: Long = savedStateHandle["alarmId"] ?: 0L
    val isEditMode = alarmId != 0L

    private val _alarmUIState = MutableStateFlow(
        Alarm(
            id = alarmId,
            title = "",
            time = System.currentTimeMillis(),
            repeatDays = 0,
            subAlarms = emptyList(),
        )
    )
    val alarmUIState: StateFlow<Alarm> = _alarmUIState

    private val _subAlarms = MutableStateFlow(emptyList<SubAlarm>())
    val subAlarms: StateFlow<List<SubAlarm>> = _subAlarms


    init {
        if (isEditMode) {
            loadAlarm()
        }
    }

    private fun loadAlarm() {
        viewModelScope.launch {
            getAlarmByIdUseCase(alarmId).collect { alarm ->
                // Update UI state fields with alarm data
                alarm?.let { editAlarm ->
                    _alarmUIState.update {
                        it.copy(
                            title = editAlarm.title,
                            time = editAlarm.time,
                            subAlarms = editAlarm.subAlarms
                        )
                    }
                }
            }
        }
    }

    fun onTimeChanged(timeInMillis: Long) {
        _alarmUIState.update {
            it.copy(
                time = timeInMillis
            )
        }
    }

    fun onTitleChanged(newTitle: String){
        _alarmUIState.update {
            it.copy(
                title = newTitle
            )
        }
    }

   fun onRepeatDaysChanged(repeatDays: Int) {
       _alarmUIState.update {
           it.copy(
               repeatDays = repeatDays
           )
       }
   }

    fun onSubAlarmAdded(){
        _alarmUIState.update {
            it.copy(
                subAlarms = _subAlarms.value
            )
        }
    }


    fun saveAlarm() {
        viewModelScope.launch {
            saveAlarmUseCase(_alarmUIState.value)
        }
    }

}