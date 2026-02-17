package com.tobibur.subalarm.domain.model

data class SubAlarm(
    val id: Long,
    val title: String,
    val time: Long,
    val isActive: Boolean = true
)