package com.tobibur.arise

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.tobibur.arise.util.AlarmConstants
import com.tobibur.arise.util.ReminderConstants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AriseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createAlarmNotificationChannel()
        createReminderNotificationChannel()
    }


    private fun createReminderNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderConstants.REMINDER_CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminder notifications"
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun createAlarmNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AlarmConstants.ALARM_CHANNEL_ID,
                "Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm notifications"
                setBypassDnd(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableVibration(false)
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}