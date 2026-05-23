package com.apksherlock.pandalauncher

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.apksherlock.pandalauncher.data.AppearanceStore
import com.apksherlock.pandalauncher.data.WallpaperStore
import com.apksherlock.pandalauncher.notifications.PandaNotificationListenerService
import com.apksherlock.pandalauncher.ui.theme.resolveInkPalette
import com.apksherlock.pandalauncher.wallpaper.InkSystemWallpaper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PandaLauncherApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    lateinit var appearanceStore: AppearanceStore
        private set

    lateinit var wallpaperStore: WallpaperStore
        private set

    override fun onCreate() {
        super.onCreate()
        appearanceStore = AppearanceStore(this)
        wallpaperStore = WallpaperStore(this)
        appScope.launch {
            appearanceStore.normalizeLegacyAppearance()
        }
        appScope.launch {
            syncSystemWallpaper()
        }
        PandaNotificationListenerService.requestRebind(this)
    }

    private suspend fun syncSystemWallpaper() {
        val darkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        if (!wallpaperStore.hasCompletedWallpaperSetup.first()) return
        val schemeId = appearanceStore.colorSchemeId(darkTheme).first()
        val palette = resolveInkPalette(schemeId, darkTheme)
        withContext(Dispatchers.IO) {
            InkSystemWallpaper.apply(applicationContext, palette)
        }
    }
}

fun Context.requireAppearanceStore(): AppearanceStore =
    (applicationContext as PandaLauncherApplication).appearanceStore

fun Context.requireWallpaperStore(): WallpaperStore =
    (applicationContext as PandaLauncherApplication).wallpaperStore
