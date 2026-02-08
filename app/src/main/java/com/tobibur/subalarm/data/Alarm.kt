package com.tobibur.subalarm.data

data class Alarm(
    val id: Int,
    val title: String,
    val time: Long,
    val subAlarms: List<SubAlarm>,
    val isActive: Boolean = true
)
