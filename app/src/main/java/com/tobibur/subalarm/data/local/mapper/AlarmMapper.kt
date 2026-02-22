package com.tobibur.subalarm.data.local.mapper

import com.tobibur.subalarm.data.local.entity.AlarmEntity
import com.tobibur.subalarm.data.local.entity.SubAlarmEntity
import com.tobibur.subalarm.data.local.relation.AlarmWithSubAlarms
import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.domain.model.SubAlarm

// Entity → Domain
fun AlarmWithSubAlarms.toDomain(): Alarm = Alarm(
    id = alarm.id,
    title = alarm.title,
    time = alarm.time,
    isActive = alarm.isActive,
    repeatDays = alarm.repeatDays,
    subAlarms = subAlarms.map { it.toDomain() }
)

fun SubAlarmEntity.toDomain(): SubAlarm = SubAlarm(
    id = id, uuid = id.toString(), title = title, time = time, isActive = isActive
)

// Domain → Entity
fun Alarm.toEntity(): AlarmEntity = AlarmEntity(
    id = id, title = title, time = time, repeatDays = repeatDays, isActive = isActive
)

fun SubAlarm.toEntity(alarmId: Long): SubAlarmEntity = SubAlarmEntity(
    id = id, alarmId = alarmId, title = title, time = time, isActive = isActive
)
