package com.apksherlock.pandalauncher

import android.app.Application
import com.apksherlock.pandalauncher.notifications.PandaNotificationListenerService

class PandaLauncherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PandaNotificationListenerService.requestRebind(this)
    }
}
