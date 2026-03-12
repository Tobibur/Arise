package com.tobibur.arise.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_alarms",
    foreignKeys = [
        ForeignKey(
            entity = AlarmEntity::class,
            parentColumns = ["id"],
            childColumns = ["alarmId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("alarmId")]
)
data class SubAlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alarmId: Long, //FK -> alarm.id
    val title: String,
    val time: Long,
    val isActive: Boolean
)
