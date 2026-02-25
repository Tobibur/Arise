package com.tobibur.subalarm

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.tobibur.subalarm.alarm.AlarmConstants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SubAlarmApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createAlarmNotificationChannel()
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