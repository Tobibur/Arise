package com.tobibur.arise.domain.model

data class SubAlarm(
    val id: Long,
    val uuid: String,
    val title: String,
    val time: Long,
    val isActive: Boolean = true
)