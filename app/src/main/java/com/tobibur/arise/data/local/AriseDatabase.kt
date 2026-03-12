package com.tobibur.arise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tobibur.arise.data.local.dao.AlarmDao
import com.tobibur.arise.data.local.dao.ReminderDao
import com.tobibur.arise.data.local.entity.AlarmEntity
import com.tobibur.arise.data.local.entity.ReminderEntity
import com.tobibur.arise.data.local.entity.SubAlarmEntity

@Database(
    entities = [AlarmEntity::class, SubAlarmEntity::class, ReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AriseDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DB_NAME = "arise_db"
    }
}