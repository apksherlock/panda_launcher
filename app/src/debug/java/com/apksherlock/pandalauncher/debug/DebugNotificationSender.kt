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
private const val MAX_SLOTS = 32

private data class Sample(
    val title: String,
    val text: String,
)

private val samplePool = listOf(
    Sample("Alex", "hey — are we still on for tonight?"),
    Sample("Calendar", "dentist · tomorrow 9:30"),
    Sample("Panda Launcher", "build finished successfully"),
    Sample("Messages", "3 unread threads"),
    Sample("Bank", "card purchase · $12.40"),
    Sample("Weather", "rain expected after 6pm"),
    Sample("Work", "standup moved to 10:15"),
    Sample("Delivery", "package out for delivery"),
    Sample("Podcast", "new episode available"),
    Sample("System", "battery saver is on"),
    Sample("GitHub", "PR #42 needs review"),
    Sample("Reminder", "call mom this weekend"),
)

/** Posts test shade notifications (debug build only) to exercise the home count label. */
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

    fun postOne(context: Context) = postCount(context, 1)

    fun postThree(context: Context) = postCount(context, 3)

    fun postTen(context: Context) = postCount(context, 10)

    fun postTwenty(context: Context) = postCount(context, 20)

    fun postCount(context: Context, count: Int) {
        ensureChannel(context)
        val safeCount = count.coerceIn(1, MAX_SLOTS)
        post(context, samplesForCount(safeCount))
    }

    fun clear(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        repeat(MAX_SLOTS) { manager.cancel(ID_BASE + it) }
    }

    private fun samplesForCount(count: Int): List<Sample> =
        List(count) { index ->
            val base = samplePool[index % samplePool.size]
            if (count <= samplePool.size) base else Sample(base.title, "${base.text} (${index + 1})")
        }

    private fun post(context: Context, items: List<Sample>) {
        clear(context)
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
