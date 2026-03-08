package com.tobibur.subalarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tobibur.subalarm.data.local.entity.AlarmEntity
import com.tobibur.subalarm.data.local.entity.SubAlarmEntity
import com.tobibur.subalarm.data.local.relation.AlarmWithSubAlarms
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    //Alarm

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    //SubAlarm

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubAlarm(subAlarm: SubAlarmEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubAlarms(subAlarms: List<SubAlarmEntity>)

    @Update
    suspend fun updateSubAlarm(subAlarm: SubAlarmEntity)

    @Delete
    suspend fun deleteSubAlarm(subAlarm: SubAlarmEntity)

    //Queries

    @Transaction
    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAllAlarmsWithSubAlarms(): Flow<List<AlarmWithSubAlarms>>

    @Transaction
    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    fun getAlarmWithSubAlarms(alarmId: Long): Flow<AlarmWithSubAlarms?>

    @Query("UPDATE alarms SET isActive = :isActive WHERE id = :alarmId")
    suspend fun updateAlarmActive(isActive: Boolean, alarmId: Long)

    @Query("DELETE FROM alarms WHERE id = :alarmId")
    suspend fun deleteAlarmById(alarmId: Long)
}