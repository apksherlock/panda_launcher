package com.apksherlock.pandalauncher.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.apksherlock.pandalauncher.model.InkNotification
import com.apksherlock.pandalauncher.notifications.PandaNotificationListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NotificationRepository(context: Context) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val notifications: StateFlow<List<InkNotification>> =
        PandaNotificationListenerService.notifications

    val notificationCount: StateFlow<Int> =
        notifications
            .map { it.size }
            .stateIn(scope, SharingStarted.WhileSubscribed(5_000), 0)

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

    fun dismissNotification(notification: InkNotification): Boolean =
        PandaNotificationListenerService.dismissNotification(notification.key)
}
