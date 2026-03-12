package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.repository.AlarmRepository
import javax.inject.Inject

class DeleteAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
){
    suspend operator fun invoke(alarmId: Long) {
        alarmRepository.deleteAlarm(alarmId)
    }
}