package com.tobibur.subalarm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tobibur.subalarm.data.local.dao.AlarmDao
import com.tobibur.subalarm.data.local.dao.ReminderDao
import com.tobibur.subalarm.data.local.entity.AlarmEntity
import com.tobibur.subalarm.data.local.entity.ReminderEntity
import com.tobibur.subalarm.data.local.entity.SubAlarmEntity

@Database(
    entities = [AlarmEntity::class, SubAlarmEntity::class, ReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SubAlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    abstract fun reminderDao(): ReminderDao
}