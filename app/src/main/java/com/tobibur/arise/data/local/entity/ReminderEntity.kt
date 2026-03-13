package com.tobibur.arise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dateTimeMillis: Long,
    val repeatDays: Int = 0,
    val isCompleted: Boolean = false
)
