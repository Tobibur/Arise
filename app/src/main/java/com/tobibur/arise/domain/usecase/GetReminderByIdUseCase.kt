package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.repository.ReminderRepository
import javax.inject.Inject

class GetReminderByIdUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {
    operator fun invoke(id: Long) = reminderRepository.getReminderById(id)
}