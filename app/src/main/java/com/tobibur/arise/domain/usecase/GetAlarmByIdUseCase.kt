package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.repository.AlarmRepository
import javax.inject.Inject

class GetAlarmByIdUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(alarmId: Long) = repository.getAlarmById(alarmId)
}