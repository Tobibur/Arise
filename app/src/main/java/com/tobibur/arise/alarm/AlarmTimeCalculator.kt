package com.tobibur.arise.alarm

import java.util.Calendar

object AlarmTimeCalculator {

    /**
     * Given a stored time (1970-epoch millis encoding just HH:mm)
     * and a repeatDays bitmask (bit 0 = Mon, ..., bit 6 = Sun),
     * calculate the next real trigger time from now.
     */
    fun calculateNextTriggerTime(storedTime: Long, repeatDays: Int): Long {
        // Extract hour and minute from stored time
        val storedCal = Calendar.getInstance().apply { timeInMillis = storedTime }
        val hour = storedCal.get(Calendar.HOUR_OF_DAY)
        val minute = storedCal.get(Calendar.MINUTE)

        val now = Calendar.getInstance()

        // Build a calendar for today at alarm's hour:minute
        val candidate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if(repeatDays == 0) {
            // ONE-SHOT: today if in future, else tomorrow
            if(candidate.after(now)) return candidate.timeInMillis
            candidate.add(Calendar.DAY_OF_MONTH, 1)
            return candidate.timeInMillis
        }

        // REPEATING: find the next day that matches the bitmask
        for(daysAhead in 0..6){
            val testCal = (candidate.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, daysAhead)
            }
            val dayOfWeek = testCal.get(Calendar.DAY_OF_WEEK)
            val bitmaskIndex = calendarDayToBitmaskIndex(dayOfWeek)

            if(repeatDays and (1 shl bitmaskIndex) != 0) {
                if(daysAhead == 0 && !testCal.after(now)) continue
                return testCal.timeInMillis
            }
        }

        // Fallback (should not happen if bitmask has at least one bit)
        candidate.add(Calendar.DAY_OF_MONTH, 7)
        return candidate.timeInMillis
    }

    /**
     * Calendar.DAY_OF_WEEK:  Sun=1, Mon=2, Tue=3, Wed=4, Thu=5, Fri=6, Sat=7
     * Your bitmask:          Mon=0, Tue=1, Wed=2, Thu=3, Fri=4, Sat=5, Sun=6
     */
    private fun calendarDayToBitmaskIndex(calendarDay: Int): Int {
        return when (calendarDay) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }

    /**
     * Sub-alarms store absolute 1970-epoch time. The offset from main alarm
     * must be preserved when computing the real trigger time.
     */
    fun calculateSubAlarmTriggerTime(
        mainAlarmStoredTime: Long,
        subAlarmStoredTime: Long,
        mainALarmTriggerTime: Long
    ): Long {
        val offsetMillis = subAlarmStoredTime - mainAlarmStoredTime
        return mainALarmTriggerTime + offsetMillis
    }
}