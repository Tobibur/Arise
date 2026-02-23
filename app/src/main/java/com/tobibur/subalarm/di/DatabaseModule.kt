package com.tobibur.subalarm.di

import android.content.Context
import androidx.room.Room
import com.tobibur.subalarm.data.local.SubAlarmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): SubAlarmDatabase {
        return Room.databaseBuilder(context, SubAlarmDatabase::class.java, "sub_alarm_db")
            .build()
    }

    @Provides
    @Singleton
    fun providesAlarmDao(database: SubAlarmDatabase) = database.alarmDao()
}