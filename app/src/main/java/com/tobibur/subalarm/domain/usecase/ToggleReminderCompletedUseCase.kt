package com.tobibur.subalarm.domain.usecase

import com.tobibur.subalarm.domain.repository.ReminderRepository
import javax.inject.Inject

class ToggleReminderCompletedUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(id: Long, completed: Boolean) {
        reminderRepository.toggleCompleted(id, completed)
    }
}