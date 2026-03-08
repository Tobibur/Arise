package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.repository.ReminderRepository
import javax.inject.Inject

class GetAllRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke() = reminderRepository.getAllReminders()
}