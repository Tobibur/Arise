package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.repository.AlarmRepository
import javax.inject.Inject

class GetAllAlarmsUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke() = repository.getAllAlarms()
}