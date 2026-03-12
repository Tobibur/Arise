package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.model.Reminder
import com.tobibur.arise.domain.repository.ReminderRepository
import javax.inject.Inject

class SaveReminderUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder) {
        reminderRepository.insert(reminder)
    }
}