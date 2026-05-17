package com.apksherlock.pandalauncher.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.apksherlock.pandalauncher.model.InkNotification
import com.apksherlock.pandalauncher.notifications.PandaNotificationListenerService
import kotlinx.coroutines.flow.StateFlow

class NotificationRepository(context: Context) {

    private val appContext = context.applicationContext

    val notifications: StateFlow<List<InkNotification>> =
        PandaNotificationListenerService.notifications

    val listenerConnected: StateFlow<Boolean> =
        PandaNotificationListenerService.connected

    fun isAccessEnabled(): Boolean =
        PandaNotificationListenerService.isAccessEnabled(appContext)

    fun rebindIfNeeded() {
        PandaNotificationListenerService.requestRebind(appContext)
    }

    fun refresh() {
        PandaNotificationListenerService.refresh(appContext)
    }

    fun openNotificationAccessSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        appContext.startActivity(intent)
    }

    fun launchNotification(notification: InkNotification): Boolean {
        val listener = PandaNotificationListenerService.instance ?: return false
        val sbn = listener.activeNotifications?.firstOrNull { it.key == notification.key }
            ?: return false
        val pending: PendingIntent = sbn.notification.contentIntent ?: return false
        return try {
            pending.send()
            true
        } catch (_: PendingIntent.CanceledException) {
            false
        }
    }
}
