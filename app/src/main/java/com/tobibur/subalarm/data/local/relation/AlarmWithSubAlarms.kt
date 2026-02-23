package com.tobibur.subalarm.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.tobibur.subalarm.data.local.entity.AlarmEntity
import com.tobibur.subalarm.data.local.entity.SubAlarmEntity

data class AlarmWithSubAlarms(
    @Embedded
    val alarm: AlarmEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "alarmId"
    )

    val subAlarms: List<SubAlarmEntity>
)
