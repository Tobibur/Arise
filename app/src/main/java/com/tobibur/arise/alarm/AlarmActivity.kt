package com.tobibur.arise.alarm

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tobibur.arise.ui.theme.AriseTheme
import com.tobibur.arise.util.AlarmConstants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.jvm.java

class AlarmActivity : ComponentActivity() {

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLockScreenFlags()

        val alarmId = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1)
        val title = intent.getStringExtra(AlarmConstants.EXTRA_ALARM_TITLE) ?: "Alarm"
        val time = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_TIME, 0L)
        val isSubAlarm = intent.getBooleanExtra(AlarmConstants.EXTRA_IS_SUB_ALARM, false)

        setContent {
            AriseTheme {
                AlarmRingScreen(
                    title = title,
                    time = time,
                    isSubAlarm = isSubAlarm,
                    onDismiss = { dismissAlarm() },
                    onSnooze = { snoozeAlarm(alarmId, title, time) }
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun acquireScreenWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "SubAlarm:AlarmScreen"
        ).apply { acquire(60_000L) }
    }

    private fun setupLockScreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        acquireScreenWakeLock()
    }

    override fun onPause() {
        super.onPause()
        if (wakeLock?.isHeld == true) wakeLock?.release()
    }

    private fun dismissAlarm() {
        stopService(Intent(this, AlarmService::class.java))
        finish()
    }

    private fun snoozeAlarm(alarmId: Long, title: String, time: Long) {
        val snoozeIntent = Intent(this, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_SNOOZE
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmConstants.EXTRA_ALARM_TITLE, title)
            putExtra(AlarmConstants.EXTRA_ALARM_TIME, time)
        }
        sendBroadcast(snoozeIntent)
        finish()
    }
}

@Composable
fun AlarmRingScreen(
    title: String,
    time: Long,
    isSubAlarm: Boolean,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(time))

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isSubAlarm) "SUB-ALARM" else "ALARM",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = timeFormatted, fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(64.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                OutlinedButton(onClick = onSnooze, modifier = Modifier.weight(1f)) {
                    Text("Snooze")
                }
                Button(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Dismiss")
                }
            }
        }
    }
}