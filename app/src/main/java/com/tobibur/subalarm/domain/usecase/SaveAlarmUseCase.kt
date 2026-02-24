package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.domain.repository.AlarmRepository
import com.tobibur.subalarm.domain.scheduler.AlarmScheduler
import javax.inject.Inject

class SaveAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(alarm: Alarm): Long {
        if (alarm.id != 0L) alarmScheduler.cancel(alarm) //Cancel existing alarm (Edit)

        val alarmId = repository.insertAlarm(alarm)
        if (alarm.subAlarms.isNotEmpty()) {
            repository.insertSubAlarms(alarmId, alarm.subAlarms)
        }

        val savedAlarm = alarm.copy(id = alarmId)
        if(savedAlarm.isActive) alarmScheduler.schedule(savedAlarm)

        return alarmId
    }
}