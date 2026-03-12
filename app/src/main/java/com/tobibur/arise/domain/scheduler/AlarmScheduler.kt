package com.tobibur.arise.domain.scheduler

import com.tobibur.arise.domain.model.Alarm

interface AlarmScheduler {
    fun canScheduleExactAlarms(): Boolean

    fun schedule(alarm: Alarm)

    fun cancel(alarm: Alarm)
}