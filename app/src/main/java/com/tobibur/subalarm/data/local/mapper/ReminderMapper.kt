package com.tobibur.subalarm.data.local.mapper

import com.tobibur.subalarm.data.local.entity.ReminderEntity
import com.tobibur.subalarm.domain.model.Reminder

fun ReminderEntity.toDomain(): Reminder = Reminder(
    id = id, title = title, description = description,
    dateTimeMillis = dateTimeMillis, repeatDays = repeatDays,
    isCompleted = isCompleted
)

fun Reminder.toEntity(): ReminderEntity = ReminderEntity(
    id = id, title = title, description = description,
    dateTimeMillis = dateTimeMillis, repeatDays = repeatDays,
    isCompleted = isCompleted
)