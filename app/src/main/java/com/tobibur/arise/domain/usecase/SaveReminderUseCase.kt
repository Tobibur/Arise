package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.model.Reminder
import com.tobibur.arise.domain.repository.ReminderRepository
import com.tobibur.arise.domain.scheduler.ReminderScheduler
import javax.inject.Inject

class SaveReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(reminder: Reminder) {
        val id = reminderRepository.insert(reminder)
        reminderScheduler.schedule(reminder.copy(id = id))
    }
}