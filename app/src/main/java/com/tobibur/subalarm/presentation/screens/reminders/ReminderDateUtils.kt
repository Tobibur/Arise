package com.tobibur.subalarm.presentation.screens.reminders

import com.tobibur.subalarm.domain.model.Reminder
import java.util.Calendar

internal const val DAY_MILLIS = 86_400_000L
internal val DAY_LABELS = listOf("M", "T", "W", "T", "F", "S", "S")

internal fun normalizeToDay(millis: Long): Long =
    Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

internal fun getWeekDays(dateMillis: Long): List<Long> {
    val cal = Calendar.getInstance().apply { timeInMillis = normalizeToDay(dateMillis) }
    val dow = cal.get(Calendar.DAY_OF_WEEK)
    cal.add(Calendar.DAY_OF_MONTH, -(if (dow == Calendar.SUNDAY) 6 else dow - Calendar.MONDAY))
    return (0..6).map { (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, it) }.timeInMillis }
}

internal fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

internal fun buildGroupedReminders(
    reminders: List<Reminder>,
    weekDays: List<Long>,
    todayMillis: Long
): List<Pair<Long, List<Reminder>>> = weekDays.mapNotNull { dayMillis ->
    val dayIndex = calendarDayToAppIndex(
        Calendar.getInstance().apply { timeInMillis = dayMillis }.get(Calendar.DAY_OF_WEEK)
    )
    val filtered = reminders.filter { reminder ->
        if (reminder.repeatDays == 0) isSameDay(dayMillis, reminder.dateTimeMillis)
        else reminder.repeatDays and (1 shl dayIndex) != 0
    }
    if (filtered.isNotEmpty()) dayMillis to filtered else null
}

private fun calendarDayToAppIndex(calendarDow: Int): Int = (calendarDow + 5) % 7
