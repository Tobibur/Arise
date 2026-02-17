package com.tobibur.subalarm.domain.model

data class Alarm(
    val id: Long,
    val title: String,
    val time: Long,
    val subAlarms: List<SubAlarm>,
    val isActive: Boolean = true
)