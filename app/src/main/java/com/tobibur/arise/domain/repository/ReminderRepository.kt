package com.tobibur.arise.domain.repository

import com.tobibur.arise.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    fun getReminderById(id: Long): Flow<Reminder?>
    suspend fun insert(reminder: Reminder): Long
    suspend fun deleteById(id: Long)
    suspend fun toggleCompleted(id: Long, completed: Boolean)
}