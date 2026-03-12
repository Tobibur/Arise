package com.tobibur.arise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val time: Long,
    val repeatDays: Int = 0,
    val isActive: Boolean = true
)
