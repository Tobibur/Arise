package com.tobibur.arise.util

object AlarmConstants {
    const val ALARM_CHANNEL_ID = "alarm_channel"
    const val EXTRA_ALARM_ID = "extra_alarm_id"
    const val EXTRA_SUB_ALARM_ID = "extra_sub_alarm_id"
    const val EXTRA_ALARM_TITLE = "extra_alarm_title"
    const val EXTRA_ALARM_TIME = "extra_alarm_time"
    const val EXTRA_IS_SUB_ALARM = "extra_is_sub_alarm"
    const val ALARM_TIMEOUT_MS = 5 * 60 * 1000L   // 5 min auto-dismiss
    const val SNOOZE_DURATION_MS = 5 * 60 * 1000L  // 5 min snooze
}
