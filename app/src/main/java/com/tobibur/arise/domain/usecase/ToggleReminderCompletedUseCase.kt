package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.repository.ReminderRepository
import com.tobibur.arise.domain.scheduler.ReminderScheduler
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleReminderCompletedUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(id: Long, completed: Boolean) {
        reminderRepository.toggleCompleted(id, completed)
        val reminder = reminderRepository.getReminderById(id).first() ?: return
        if (completed) {
            reminderScheduler.cancel(reminder)
        } else {
            reminderScheduler.schedule(reminder)
        }
    }
}