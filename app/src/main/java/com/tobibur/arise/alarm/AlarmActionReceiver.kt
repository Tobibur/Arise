package com.tobibur.arise.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DISMISS = "com.tobibur.arise.ACTION_DISMISS"
        const val ACTION_SNOOZE = "com.tobibur.arise.ACTION_SNOOZE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Stop sound and vibration
        context.stopService(Intent(context, AlarmService::class.java))

        when (intent.action) {
            ACTION_DISMISS -> { /* Service stopped, nothing else needed */
            }

            ACTION_SNOOZE -> {
                val alarmId = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1)
                val title = intent.getStringExtra(AlarmConstants.EXTRA_ALARM_TITLE) ?: "Alarm"
                val snoozeTime = System.currentTimeMillis() + AlarmConstants.SNOOZE_DURATION_MS

                val snoozeAlarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
                    putExtra(AlarmConstants.EXTRA_SUB_ALARM_ID, -1L)
                    putExtra(AlarmConstants.EXTRA_ALARM_TITLE, title)
                    putExtra(AlarmConstants.EXTRA_ALARM_TIME, snoozeTime)
                    putExtra(AlarmConstants.EXTRA_IS_SUB_ALARM, false)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    RequestCodeGenerator.forSnoozeReschedule(alarmId),
                    snoozeAlarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager = context.getSystemService(AlarmManager::class.java)
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(snoozeTime, null),
                    pendingIntent
                )
            }
        }
    }
}