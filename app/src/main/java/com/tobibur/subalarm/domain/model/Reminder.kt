package com.tobibur.subalarm.domain.model

data class Reminder(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dateTimeMillis: Long,
    val repeatDays: Int = 0,
    val isCompleted: Boolean = false
)
