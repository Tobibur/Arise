package com.tobibur.arise.domain.model

data class Alarm(
    val id: Long,
    val title: String,
    val time: Long,
    val subAlarms: List<SubAlarm>,
    val repeatDays: Int = 0,
    val isActive: Boolean = true
)