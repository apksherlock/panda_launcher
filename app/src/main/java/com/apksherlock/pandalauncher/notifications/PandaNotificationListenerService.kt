package com.apksherlock.pandalauncher.notifications

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.apksherlock.pandalauncher.model.InkNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PandaNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        _connected.value = true
        publishActive()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        publishActive()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        publishActive()
    }

    override fun onListenerDisconnected() {
        instance = null
        _connected.value = false
        _notifications.value = emptyList()
        super.onListenerDisconnected()
    }

    private fun publishActive() {
        _notifications.value = mapActiveNotifications(this, activeNotifications)
    }

    companion object {
        private val _notifications = MutableStateFlow<List<InkNotification>>(emptyList())
        val notifications: StateFlow<List<InkNotification>> = _notifications.asStateFlow()

        private val _connected = MutableStateFlow(false)
        val connected: StateFlow<Boolean> = _connected.asStateFlow()

        @Volatile
        var instance: PandaNotificationListenerService? = null

        fun isAccessEnabled(context: Context): Boolean {
            val enabled = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners",
            ) ?: return false
            val component = ComponentName(context, PandaNotificationListenerService::class.java)
            val flat = component.flattenToString()
            val short = component.flattenToShortString()
            return enabled.split(':').any { entry ->
                entry.equals(flat, ignoreCase = true) ||
                    entry.equals(short, ignoreCase = true)
            }
        }

        fun requestRebind(context: Context) {
            if (!isAccessEnabled(context)) return
            val component = ComponentName(context, PandaNotificationListenerService::class.java)
            try {
                requestRebind(component)
            } catch (_: Exception) {
                // Ignore — system may rebind on its own.
            }
        }

        fun refresh(context: Context) {
            val service = instance
            if (service != null) {
                service.publishActive()
            } else if (isAccessEnabled(context)) {
                requestRebind(context)
            }
        }

        fun dismissNotification(key: String): Boolean {
            val service = instance ?: return false
            return try {
                service.cancelNotification(key)
                true
            } catch (_: Exception) {
                false
            }
        }

        private fun mapActiveNotifications(
            context: Context,
            active: Array<StatusBarNotification>?,
        ): List<InkNotification> {
            if (active == null) return emptyList()
            return active
                .asSequence()
                .filter { sbn ->
                    val flags = sbn.notification.flags
                    flags and Notification.FLAG_GROUP_SUMMARY == 0
                }
                .mapNotNull { it.toInkNotification(context) }
                .sortedByDescending { it.postTime }
                .toList()
        }

        private fun StatusBarNotification.toInkNotification(context: Context): InkNotification? {
            val extras = notification.extras
            val title = sequenceOf(
                Notification.EXTRA_TITLE,
                Notification.EXTRA_TITLE_BIG,
                "android.title",
            )
                .mapNotNull { key -> extras.getCharSequence(key)?.toString()?.trim() }
                .firstOrNull { it.isNotBlank() }
                ?: loadAppLabel(context, packageName)
                ?: return null

            val subtitle = sequenceOf(
                Notification.EXTRA_TEXT,
                Notification.EXTRA_BIG_TEXT,
                Notification.EXTRA_SUMMARY_TEXT,
                "android.text",
                "android.bigText",
            )
                .mapNotNull { key -> extras.getCharSequence(key)?.toString()?.trim() }
                .firstOrNull { it.isNotBlank() }
                ?: ""

            return InkNotification(
                key = key,
                title = title,
                subtitle = subtitle,
                postTime = postTime,
                packageName = packageName,
            )
        }

        private fun loadAppLabel(context: Context, packageName: String): String? = try {
            val pm = context.packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
        } catch (_: Exception) {
            null
        }
    }
}
