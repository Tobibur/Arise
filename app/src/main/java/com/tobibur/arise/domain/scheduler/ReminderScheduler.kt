package com.tobibur.arise.domain.scheduler

import com.tobibur.arise.domain.model.Reminder

interface ReminderScheduler {

    fun canScheduleExactAlarms(): Boolean
    fun schedule(reminder: Reminder)
    fun cancel(reminder: Reminder)
}