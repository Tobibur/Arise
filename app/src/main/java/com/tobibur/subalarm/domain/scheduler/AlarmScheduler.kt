package com.tobibur.subalarm.domain.scheduler

import com.tobibur.subalarm.domain.model.Alarm

interface AlarmScheduler {
    fun canScheduleExactAlarms(): Boolean

    fun schedule(alarm: Alarm)

    fun cancel(alarm: Alarm)
}