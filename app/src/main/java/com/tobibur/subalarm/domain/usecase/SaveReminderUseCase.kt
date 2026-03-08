package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.model.Reminder
import com.tobibur.subalarm.domain.repository.ReminderRepository
import javax.inject.Inject

class SaveReminderUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder) {
        reminderRepository.insert(reminder)
    }
}