package com.tobibur.subalarm.di

import com.tobibur.subalarm.domain.repository.AlarmRepository
import com.tobibur.subalarm.domain.repository.AlarmRepositoryImpl
import com.tobibur.subalarm.domain.repository.ReminderRepository
import com.tobibur.subalarm.domain.repository.ReminderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAlarmRepository(alarmRepositoryImpl: AlarmRepositoryImpl): AlarmRepository

    @Binds
    abstract fun bindReminderRepository(reminderRepositoryImpl: ReminderRepositoryImpl): ReminderRepository

}