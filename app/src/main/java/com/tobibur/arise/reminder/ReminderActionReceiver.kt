package com.tobibur.arise.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.tobibur.arise.R
import com.tobibur.arise.domain.repository.ReminderRepository
import com.tobibur.arise.domain.scheduler.ReminderScheduler
import com.tobibur.arise.util.ReminderConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderActionReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderActionReceiver"
        const val ACTION_DISMISS = "com.tobibur.arise.REMINDER_DISMISS"
        const val ACTION_COMPLETE = "com.tobibur.arise.REMINDER_COMPLETE"
    }

    @Inject lateinit var reminderRepository: ReminderRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(ReminderConstants.EXTRA_REMINDER_ID, -1)
        if (reminderId == -1L) return

        when (intent.action) {
            ACTION_DISMISS -> {
                Log.d(TAG, "Reminder dismissed: id=$reminderId")
                cancelNotification(context, reminderId)
            }

            ACTION_COMPLETE -> {
                Log.d(TAG, "Reminder completed: id=$reminderId")
                cancelNotification(context, reminderId)
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                    try {
                        reminderRepository.toggleCompleted(reminderId, true)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            else -> {
                // Trigger: reminder time arrived — show notification and reschedule if repeating
                val title = intent.getStringExtra(ReminderConstants.EXTRA_REMINDER_TITLE) ?: "Reminder"
                val description = intent.getStringExtra(ReminderConstants.EXTRA_REMINDER_DESCRIPTION) ?: ""

                Log.d(TAG, "Reminder fired: id=$reminderId, title=$title")
                showNotification(context, reminderId, title, description)
                rescheduleIfRepeating(reminderId)
            }
        }
    }

    private fun showNotification(context: Context, reminderId: Long, title: String, description: String) {
        val notificationId = reminderId.toInt()

        val dismissIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra(ReminderConstants.EXTRA_REMINDER_ID, reminderId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, notificationId * 2, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ACTION_COMPLETE
            putExtra(ReminderConstants.EXTRA_REMINDER_ID, reminderId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context, notificationId * 2 + 1, completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ReminderConstants.REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: replace with reminder icon
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .addAction(0, "Done", completePendingIntent)
            .addAction(0, "Dismiss", dismissPendingIntent)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
    }

    private fun cancelNotification(context: Context, reminderId: Long) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(reminderId.toInt())
    }

    private fun rescheduleIfRepeating(reminderId: Long) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val reminder = reminderRepository.getReminderById(reminderId).first()
                if (reminder != null && reminder.repeatDays != 0) {
                    Log.d(TAG, "Rescheduling repeating reminder: id=$reminderId")
                    reminderScheduler.schedule(reminder)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
