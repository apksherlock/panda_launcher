package com.apksherlock.pandalauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.Process
import android.os.UserHandle
import com.apksherlock.pandalauncher.model.LaunchableApp

class AppRepository(context: Context) {

    private val appContext = context.applicationContext
    private val launcherApps =
        appContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    private val ourPackage = appContext.packageName

    fun loadLaunchableApps(): List<LaunchableApp> {
        val user = Process.myUserHandle()
        return launcherApps.getActivityList(null, user)
            .asSequence()
            .filter { it.applicationInfo.packageName != ourPackage }
            .mapNotNull { info ->
                val label = info.label?.toString()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val icon = loadLauncherIcon(info)
                LaunchableApp(
                    packageName = info.componentName.packageName,
                    label = label,
                    componentName = info.componentName,
                    icon = icon,
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    private fun loadLauncherIcon(info: android.content.pm.LauncherActivityInfo): Drawable {
        return try {
            val density = appContext.resources.displayMetrics.densityDpi
            info.getIcon(density)
        } catch (_: Exception) {
            try {
                info.getBadgedIcon(appContext.resources.displayMetrics.densityDpi)
            } catch (_: Exception) {
                appContext.packageManager.getApplicationIcon(info.applicationInfo.packageName)
            }
        }
    }

    fun launch(app: LaunchableApp) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = app.componentName
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        appContext.startActivity(intent)
    }
}
