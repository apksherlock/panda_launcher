package com.apksherlock.pandalauncher.debug

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.apksherlock.pandalauncher.MainActivity
import com.apksherlock.pandalauncher.R

private const val CHANNEL_ID = "panda_debug"
private const val ID_BASE = 9001

private data class Sample(
    val title: String,
    val text: String,
)

private val samples = listOf(
    Sample("Alex", "hey — are we still on for tonight?"),
    Sample("Calendar", "dentist · tomorrow 9:30"),
    Sample("Panda Launcher", "build finished successfully"),
)

object DebugNotificationSender {

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Debug",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Test notifications for Panda Launcher UI"
        }
        manager.createNotificationChannel(channel)
    }

    fun postOne(context: Context) {
        ensureChannel(context)
        post(context, samples.take(1))
    }

    fun postThree(context: Context) {
        ensureChannel(context)
        post(context, samples)
    }

    fun clear(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        samples.indices.forEach { manager.cancel(ID_BASE + it) }
    }

    private fun post(context: Context, items: List<Sample>) {
        val manager = context.getSystemService(NotificationManager::class.java)
        items.forEachIndexed { index, sample ->
            manager.notify(ID_BASE + index, build(context, sample, ID_BASE + index))
        }
    }

    private fun build(context: Context, sample: Sample, id: Int): Notification {
        val openHome = PendingIntent.getActivity(
            context,
            id,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(sample.title)
            .setContentText(sample.text)
            .setStyle(Notification.BigTextStyle().bigText(sample.text))
            .setContentIntent(openHome)
            .setAutoCancel(true)
            .build()
    }
}
