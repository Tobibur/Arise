package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.repository.ReminderRepository
import javax.inject.Inject

class GetReminderByIdUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {
    operator fun invoke(id: Long) = reminderRepository.getReminderById(id)
}