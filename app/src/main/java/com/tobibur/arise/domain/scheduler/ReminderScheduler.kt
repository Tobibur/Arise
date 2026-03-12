package com.tobibur.arise.domain.scheduler

import com.tobibur.arise.domain.model.Reminder

interface ReminderScheduler {
    fun schedule(reminder: Reminder)
    fun cancel(reminder: Reminder)
}