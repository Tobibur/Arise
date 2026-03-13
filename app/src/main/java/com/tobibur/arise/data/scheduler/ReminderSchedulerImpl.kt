package com.tobibur.arise.data.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.tobibur.arise.reminder.ReminderActionReceiver
import com.tobibur.arise.domain.model.Reminder
import com.tobibur.arise.domain.scheduler.ReminderScheduler
import com.tobibur.arise.domain.usecase.AlarmTimeCalculator
import com.tobibur.arise.util.ReminderConstants
import com.tobibur.arise.util.RequestCodeGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    override fun schedule(reminder: Reminder) {
        if (reminder.isCompleted) return

        if (!canScheduleExactAlarms()) {
            Log.e("ReminderScheduler", "Cannot schedule exact alarms — permission not granted")
        }

        val triggerTime = calculateReminderTriggerTime(reminder)
        if (triggerTime == null) {
            Log.w("ReminderScheduler", "Reminder ${reminder.id} is in the past, not scheduling")
            return
        }

        scheduleExact(
            requestCode = RequestCodeGenerator.forReminder(reminder.id),
            triggerAtMillis = triggerTime,
            reminderId = reminder.id,
            title = reminder.title,
            description = reminder.description
        )

    }

    private fun scheduleExact(
        requestCode: Int,
        triggerAtMillis: Long,
        reminderId: Long,
        title: String,
        description: String,
    ) {

        val intent = Intent(context, ReminderActionReceiver::class.java).apply {
            putExtra(ReminderConstants.EXTRA_REMINDER_ID, reminderId)
            putExtra(ReminderConstants.EXTRA_REMINDER_TITLE, title)
            putExtra(ReminderConstants.EXTRA_REMINDER_DESCRIPTION, description)
            putExtra(ReminderConstants.EXTRA_REMINDER_TIME, triggerAtMillis)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )

    }

    private fun calculateReminderTriggerTime(reminder: Reminder): Long? {
        val now = System.currentTimeMillis()

        // Non-repeating: use the exact datetime, skip if in the past
        if (reminder.repeatDays == 0) {
            return if (reminder.dateTimeMillis > now) reminder.dateTimeMillis else null
        }

        // Repeating: use AlarmTimeCalculator which finds the next matching day
        return AlarmTimeCalculator.calculateNextTriggerTime(
            storedTime = reminder.dateTimeMillis,
            repeatDays = reminder.repeatDays
        )
    }

    override fun cancel(reminder: Reminder) {
        val intent = Intent(context, ReminderActionReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RequestCodeGenerator.forReminder(reminder.id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}