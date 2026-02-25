package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.repository.AlarmRepository
import javax.inject.Inject

class DeleteAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
){
    suspend operator fun invoke(alarmId: Long) {
        alarmRepository.deleteAlarm(alarmId)
    }
}