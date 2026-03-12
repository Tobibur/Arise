package com.tobibur.arise.domain.usecase

import com.tobibur.arise.domain.repository.ReminderRepository
import javax.inject.Inject

class ToggleReminderCompletedUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(id: Long, completed: Boolean) {
        reminderRepository.toggleCompleted(id, completed)
    }
}