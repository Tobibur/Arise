package com.tobibur.arise.data.scheduler

import android.content.Context
import com.tobibur.arise.domain.model.Reminder
import com.tobibur.arise.domain.scheduler.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): ReminderScheduler {
    override fun schedule(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override fun cancel(reminder: Reminder) {
        TODO("Not yet implemented")
    }


}