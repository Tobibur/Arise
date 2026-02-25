package com.tobibur.subalarm.alarm

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.session.MediaSession
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.tobibur.subalarm.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmService : Service() {

    companion object {
        private const val TAG = "AlarmService"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timeoutJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1) ?: -1
        val subAlarmId = intent?.getLongExtra(AlarmConstants.EXTRA_SUB_ALARM_ID, -1) ?: -1
        val title = intent?.getStringExtra(AlarmConstants.EXTRA_ALARM_TITLE) ?: "Alarm"
        val time = intent?.getLongExtra(AlarmConstants.EXTRA_ALARM_TIME, 0L) ?: 0L
        val isSubAlarm = intent?.getBooleanExtra(AlarmConstants.EXTRA_IS_SUB_ALARM, false) ?: false

        Log.d(TAG, "onStartCommand: alarmId=$alarmId, title=$title, isSubAlarm=$isSubAlarm")

        // Required on API 34+: mediaPlayback foreground service type needs an active MediaSession
        mediaSession = MediaSession(this, "SubAlarmService").apply { isActive = true }

        val notification = buildNotification(alarmId, subAlarmId, title, time, isSubAlarm)
        try {
            startForeground(RequestCodeGenerator.forNotification(alarmId), notification)
        } catch (e: Exception) {
            Log.e(TAG, "startForeground failed", e)
        }

        startAlarmSound()
        startVibration()

        // Auto-timeout after 5 minutes
        timeoutJob = serviceScope.launch {
            delay(AlarmConstants.ALARM_TIMEOUT_MS)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun buildNotification(
        alarmId: Long, subAlarmId: Long, title: String, time: Long, isSubAlarm: Boolean
    ): Notification {
        // Full-screen intent → AlarmActivity
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmConstants.EXTRA_SUB_ALARM_ID, subAlarmId)
            putExtra(AlarmConstants.EXTRA_ALARM_TITLE, title)
            putExtra(AlarmConstants.EXTRA_ALARM_TIME, time)
            putExtra(AlarmConstants.EXTRA_IS_SUB_ALARM, isSubAlarm)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, RequestCodeGenerator.forMainAlarm(alarmId), fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss action
        val dismissIntent = Intent(this, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_DISMISS
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            this, RequestCodeGenerator.forDismiss(alarmId), dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze action
        val snoozeIntent = Intent(this, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_SNOOZE
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmConstants.EXTRA_ALARM_TITLE, title)
            putExtra(AlarmConstants.EXTRA_ALARM_TIME, time)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            this, RequestCodeGenerator.forSnooze(alarmId), snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, AlarmConstants.ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: replace with alarm icon
            .setContentTitle(if (isSubAlarm) "Sub-Alarm" else "Alarm")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .setSound(null)
            .addAction(0, "Dismiss", dismissPendingIntent)
            .addAction(0, "Snooze", snoozePendingIntent)

        mediaSession?.sessionToken?.let { token ->
            builder.extras.putParcelable(Notification.EXTRA_MEDIA_SESSION, token)
        }

        return builder.build()
    }

    private fun startAlarmSound() {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)       // Uses alarm volume stream
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(this@AlarmService, alarmUri)
            isLooping = true
            prepare()
            start()
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 500, 500) // wait, vibrate 500ms, pause 500ms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0)) // 0 = repeat from index 0
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeoutJob?.cancel()
        serviceScope.cancel()
        mediaPlayer?.apply { if (isPlaying) stop(); release() }
        mediaPlayer = null
        vibrator?.cancel()
        mediaSession?.release()
        mediaSession = null
    }
}