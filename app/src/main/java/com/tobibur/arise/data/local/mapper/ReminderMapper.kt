package com.tobibur.arise.data.local.mapper

import com.tobibur.arise.data.local.entity.ReminderEntity
import com.tobibur.arise.domain.model.Reminder

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