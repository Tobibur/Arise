package com.tobibur.subalarm.domain.repository

import com.tobibur.subalarm.data.local.dao.AlarmDao
import com.tobibur.subalarm.data.local.mapper.toDomain
import com.tobibur.subalarm.data.local.mapper.toEntity
import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.domain.model.SubAlarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {
    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarmsWithSubAlarms().map { list -> list.map { it.toDomain() } }
    }

    override fun getAlarmById(alarmId: Long): Flow<Alarm?> {
        return alarmDao.getAlarmWithSubAlarms(alarmId).map { it?.toDomain() }
    }

    override suspend fun insertAlarm(alarm: Alarm): Long {
        return alarmDao.insertAlarm(alarm.toEntity())
    }

    override suspend fun insertSubAlarms(
        alarmId: Long,
        subAlarms: List<SubAlarm>
    ) {
        alarmDao.insertSubAlarms(subAlarms.map { it.toEntity(alarmId) })
    }

    override suspend fun updateAlarmActive(alarmId: Long, isActive: Boolean) {
        alarmDao.updateAlarmActive(isActive, alarmId)
    }

    override suspend fun deleteAlarm(alarmId: Long) {
        alarmDao.deleteAlarmById(alarmId)
    }
}