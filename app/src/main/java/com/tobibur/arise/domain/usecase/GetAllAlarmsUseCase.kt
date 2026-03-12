package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.repository.AlarmRepository
import javax.inject.Inject

class GetAllAlarmsUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke() = repository.getAllAlarms()
}