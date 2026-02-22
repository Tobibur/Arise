package com.tobibur.subalarm.presentation.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

fun convertTime(hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, 1970)
    calendar.set(Calendar.MONTH, Calendar.JANUARY)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun formatTime(time: Long): Pair<String, String> {
    val date = Date(time)
    val timePart = SimpleDateFormat("hh:mm", Locale.getDefault()).format(date)
    val amPm = SimpleDateFormat("a", Locale.getDefault()).format(date)
    return Pair(timePart, amPm)
}

fun formatSubAlarmTime(alarmTime: Long, subAlarmTime: Long): String {
    val diffMinutes = abs(subAlarmTime - alarmTime) / 60_000
    val sign = if (subAlarmTime >= alarmTime) "+" else "-"
    val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(subAlarmTime))
    return "$sign$diffMinutes min ($timeFormatted)"
}
