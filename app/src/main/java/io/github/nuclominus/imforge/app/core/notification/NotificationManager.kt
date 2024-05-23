package io.github.nuclominus.imforge.app.core.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import io.github.nuclominus.imforge.app.NotificationConstants

object NotificationManager {

    fun Context.createForegroundInfo(uuid: String): ForegroundInfo {
        val notification = createNotification(this)
        return ForegroundInfo(notificationId(uuid), notification)
    }

    private fun notificationId(uuid: String): Int {
        val sequence = uuid.toCharArray().asSequence()
        val iterator = sequence.iterator()
        var id = 0
        while (iterator.hasNext()) {
            id += iterator.next().code
        }
        return id
    }

    private fun createNotification(context: Context): Notification {
        createNotificationChannel(context)
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle(NotificationConstants.NOTIFICATION_TITLE)
            .setContentText(NotificationConstants.NOTIFICATION_CONTENT)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                NotificationConstants.CHANNEL_ID,
                NotificationConstants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NotificationConstants.CHANNEL_DESCRIPTION
                enableLights(true)
                val att = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), att)
                enableVibration(true)
            }

            with(NotificationManagerCompat.from(context)) {
                createNotificationChannel(channel)
            }
        }
    }

}