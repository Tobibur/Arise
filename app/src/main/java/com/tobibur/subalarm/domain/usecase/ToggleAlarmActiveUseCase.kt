package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.repository.AlarmRepository
import javax.inject.Inject

class ToggleAlarmActiveUseCase @Inject constructor(private val repository: AlarmRepository) {

    suspend operator fun invoke(alarmId: Long, isActive: Boolean) {
        repository.updateAlarmActive(alarmId, isActive)
    }
}