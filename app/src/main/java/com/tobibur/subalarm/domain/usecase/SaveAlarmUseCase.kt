package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.domain.repository.AlarmRepository
import javax.inject.Inject

class SaveAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {

    suspend operator fun invoke(alarm: Alarm): Long {
        val alarmId = repository.insertAlarm(alarm)
        if (alarm.subAlarms.isNotEmpty()) {
            repository.insertSubAlarms(alarmId, alarm.subAlarms)
        }
        return alarmId
    }
}