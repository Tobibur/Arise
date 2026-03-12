package com.tobibur.arise.di

import com.tobibur.arise.data.scheduler.AlarmSchedulerImpl
import com.tobibur.arise.domain.scheduler.AlarmScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Binds
    abstract fun bindAlarmScheduler(impl: AlarmSchedulerImpl): AlarmScheduler
}