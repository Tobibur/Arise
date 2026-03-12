package com.tobibur.arise.alarm

object RequestCodeGenerator {
    private const val TYPE_MAIN_ALARM = 1
    private const val TYPE_SUB_ALARM = 2
    private const val TYPE_DISMISS = 3
    private const val TYPE_SNOOZE = 4
    private const val TYPE_SNOOZE_RESCHEDULE = 5
    private const val TYPE_NOTIFICATION = 6

    fun forMainAlarm(alarmId: Long): Int = hash(alarmId, 0L, TYPE_MAIN_ALARM)
    fun forSubAlarm(alarmId: Long, subAlarmId: Long): Int = hash(alarmId, subAlarmId, TYPE_SUB_ALARM)
    fun forDismiss(alarmId: Long): Int = hash(alarmId, 0L, TYPE_DISMISS)
    fun forSnooze(alarmId: Long): Int = hash(alarmId, 0L, TYPE_SNOOZE)
    fun forSnoozeReschedule(alarmId: Long): Int = hash(alarmId, 0L, TYPE_SNOOZE_RESCHEDULE)
    fun forNotification(alarmId: Long): Int = hash(alarmId, 0L, TYPE_NOTIFICATION).coerceAtLeast(1)

    private fun hash(alarmId: Long, subAlarmId: Long, type: Int): Int {
        var result = alarmId.hashCode()
        result = 31 * result + subAlarmId.hashCode()
        result = 31 * result + type
        return result and 0x7FFFFFFF // mask sign bit -> always positive
    }
}
