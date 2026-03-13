package com.tobibur.arise.domain.repository

import com.tobibur.arise.data.local.dao.ReminderDao
import com.tobibur.arise.data.local.mapper.toDomain
import com.tobibur.arise.data.local.mapper.toEntity
import com.tobibur.arise.domain.model.Reminder
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

    override suspend fun deleteById(id: Long) {
        reminderDao.deleteById(id)
    }

    override suspend fun toggleCompleted(id: Long, completed: Boolean) {
        reminderDao.updateCompleted(id, completed)
    }
}