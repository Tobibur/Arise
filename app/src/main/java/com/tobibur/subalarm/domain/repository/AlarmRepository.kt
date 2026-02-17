package com.tobibur.subalarm.domain.repository

import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.domain.model.SubAlarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarms(): Flow<List<Alarm>>
    fun getAlarmById(alarmId: Long): Flow<Alarm?>
    suspend fun insertAlarm(alarm: Alarm): Long
    suspend fun insertSubAlarms(alarmId: Long, subAlarms: List<SubAlarm>)
    suspend fun updateAlarmActive(alarmId: Long, isActive: Boolean)
    suspend fun deleteAlarm(alarmId: Long)
}