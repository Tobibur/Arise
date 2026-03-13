package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.repository.ReminderRepository
import javax.inject.Inject

class GetAllRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke() = reminderRepository.getAllReminders()
}