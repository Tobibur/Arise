package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.model.Alarm
import com.tobibur.arise.domain.repository.AlarmRepository
import com.tobibur.arise.domain.scheduler.AlarmScheduler
import javax.inject.Inject

class ToggleAlarmActiveUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) {

    suspend operator fun invoke(alarm: Alarm, isActive: Boolean) {
        repository.updateAlarmActive(alarm.id, isActive)
        val updatedAlarm = alarm.copy(isActive = isActive)
        if (isActive) alarmScheduler.schedule(updatedAlarm) else alarmScheduler.cancel(updatedAlarm)
    }
}