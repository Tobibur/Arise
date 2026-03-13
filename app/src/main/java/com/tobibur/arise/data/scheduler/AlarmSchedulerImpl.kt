package com.tobibur.arise.data.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.tobibur.arise.alarm.AlarmReceiver
import com.tobibur.arise.domain.usecase.AlarmTimeCalculator
import com.tobibur.arise.util.AlarmConstants
import com.tobibur.arise.util.RequestCodeGenerator
import com.tobibur.arise.domain.model.Alarm
import com.tobibur.arise.domain.scheduler.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.java

@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    override fun schedule(alarm: Alarm) {
        if (!alarm.isActive) return

        if (!canScheduleExactAlarms()) {
            Log.e("AlarmScheduler", "Cannot schedule exact alarms — permission not granted")
            return
        }

        val triggerTime = AlarmTimeCalculator.calculateNextTriggerTime(
            storedTime = alarm.time,
            repeatDays = alarm.repeatDays
        )

        //Schedule main alarm
        scheduleExact(
            requestCode = RequestCodeGenerator.forMainAlarm(alarm.id),
            triggerAtMillis = triggerTime,
            alarmId = alarm.id,
            subAlarmId = -1L,
            title = alarm.title,
            isSubAlarm = false
        )

        //Schedule each active sub alarm
        alarm.subAlarms.filter { it.isActive }.forEach { subAlarm ->
            val subAlarmTriggerTime = AlarmTimeCalculator.calculateSubAlarmTriggerTime(
                mainAlarmStoredTime = alarm.time,
                subAlarmStoredTime = subAlarm.time,
                mainALarmTriggerTime = triggerTime
            )
            scheduleExact(
                requestCode = RequestCodeGenerator.forSubAlarm(alarm.id, subAlarm.id),
                triggerAtMillis = subAlarmTriggerTime,
                alarmId = alarm.id,
                subAlarmId = subAlarm.id,
                title = subAlarm.title,
                isSubAlarm = true
            )
        }
    }

    override fun cancel(alarm: Alarm) {
        cancelPendingIntent(RequestCodeGenerator.forMainAlarm(alarm.id))
        alarm.subAlarms.filter { it.isActive }.forEach { subAlarm ->
            cancelPendingIntent(RequestCodeGenerator.forSubAlarm(alarm.id, subAlarm.id))
        }
    }

    private fun scheduleExact(
        requestCode: Int,
        triggerAtMillis: Long,
        alarmId: Long,
        subAlarmId: Long,
        title: String,
        isSubAlarm: Boolean
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmConstants.EXTRA_SUB_ALARM_ID, subAlarmId)
            putExtra(AlarmConstants.EXTRA_ALARM_TITLE, title)
            putExtra(AlarmConstants.EXTRA_ALARM_TIME, triggerAtMillis)
            putExtra(AlarmConstants.EXTRA_IS_SUB_ALARM, isSubAlarm)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // setAlarmClock: shows alarm icon in status bar, fully Doze-exempt
        val showIntent = PendingIntent.getActivity(
            context,
            requestCode,
            context.packageManager.getLaunchIntentForPackage(context.packageName)!!,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    private fun cancelPendingIntent(requestCode: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

}