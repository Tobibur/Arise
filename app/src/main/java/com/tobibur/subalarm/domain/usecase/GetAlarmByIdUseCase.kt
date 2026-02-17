package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.repository.AlarmRepository
import javax.inject.Inject

class GetAlarmByIdUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(alarmId: Long) = repository.getAlarmById(alarmId)
}