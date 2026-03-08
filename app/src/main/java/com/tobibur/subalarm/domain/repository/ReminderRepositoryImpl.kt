package com.tobibur.subalarm.domain.repository

import com.tobibur.subalarm.data.local.dao.ReminderDao
import com.tobibur.subalarm.data.local.mapper.toDomain
import com.tobibur.subalarm.data.local.mapper.toEntity
import com.tobibur.subalarm.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAll().map { list -> list.map { it.toDomain() } }
    }

    override fun getReminderById(id: Long): Flow<Reminder?> {
        return reminderDao.getById(id).map { it?.toDomain() }
    }

    override suspend fun insert(reminder: Reminder): Long {
        return reminderDao.insert(reminder.toEntity())
    }

    override suspend fun update(reminder: Reminder) {
        return reminderDao.update(reminder.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        return reminderDao.deleteById(id)
    }

    override suspend fun toggleCompleted(id: Long, completed: Boolean) {
        return reminderDao.updateCompleted(id, completed)
    }
}