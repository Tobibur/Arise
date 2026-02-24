package com.tobibur.subalarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        intent?.let {
            val alarmId = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1)
            val subAlarmId = intent.getLongExtra(AlarmConstants.EXTRA_SUB_ALARM_ID, -1)
            val title = intent.getStringExtra(AlarmConstants.EXTRA_ALARM_TITLE) ?: "Alarm"
            val time = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_TIME, 0L)
            val isSubAlarm = intent.getBooleanExtra(AlarmConstants.EXTRA_IS_SUB_ALARM, false)

            Log.d(TAG, "Alarm fired: id=$alarmId, title=$title, isSubAlarm=$isSubAlarm")

            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
                putExtra(AlarmConstants.EXTRA_SUB_ALARM_ID, subAlarmId)
                putExtra(AlarmConstants.EXTRA_ALARM_TITLE, title)
                putExtra(AlarmConstants.EXTRA_ALARM_TIME, time)
                putExtra(AlarmConstants.EXTRA_IS_SUB_ALARM, isSubAlarm)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

            // Launch AlarmActivity directly; setAlarmClock() grants background activity start
            val activityIntent = Intent(context, AlarmActivity::class.java).apply {
                putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
                putExtra(AlarmConstants.EXTRA_SUB_ALARM_ID, subAlarmId)
                putExtra(AlarmConstants.EXTRA_ALARM_TITLE, title)
                putExtra(AlarmConstants.EXTRA_ALARM_TIME, time)
                putExtra(AlarmConstants.EXTRA_IS_SUB_ALARM, isSubAlarm)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            }
            context.startActivity(activityIntent)
        }
    }
}