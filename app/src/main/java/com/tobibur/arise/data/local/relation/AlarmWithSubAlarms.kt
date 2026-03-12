package com.tobibur.arise.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.tobibur.arise.data.local.entity.AlarmEntity
import com.tobibur.arise.data.local.entity.SubAlarmEntity

data class AlarmWithSubAlarms(
    @Embedded
    val alarm: AlarmEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "alarmId"
    )

    val subAlarms: List<SubAlarmEntity>
)
