package com.tobibur.arise.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tobibur.arise.domain.repository.AlarmRepository
import com.tobibur.arise.domain.repository.ReminderRepository
import com.tobibur.arise.domain.scheduler.AlarmScheduler
import com.tobibur.arise.domain.scheduler.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmRepository: AlarmRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var reminderRepository: ReminderRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()  // extends time limit from 10s to ~30s

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                alarmRepository.getAllAlarms().first()
                    .filter { it.isActive }
                    .forEach { alarmScheduler.schedule(it) }

                reminderRepository.getAllReminders().first()
                    .filter { !it.isCompleted }
                    .forEach { reminderScheduler.schedule(it) }
            } finally {
                pendingResult.finish()  // MUST call or system ANRs
            }
        }
    }
}