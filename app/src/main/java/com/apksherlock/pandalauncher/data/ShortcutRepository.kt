package com.apksherlock.pandalauncher.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.os.Process
import android.os.UserHandle
import com.apksherlock.pandalauncher.model.PinnedShortcut

class ShortcutRepository(context: Context) {

    private val appContext = context.applicationContext
    private val launcherApps =
        appContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    private val user: UserHandle = Process.myUserHandle()
    private val ourPackage = appContext.packageName

    fun loadPinnedShortcuts(): List<PinnedShortcut> {
        if (!launcherApps.hasShortcutHostPermission()) return emptyList()

        val query = LauncherApps.ShortcutQuery().setQueryFlags(
            LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED or
                LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC,
        )

        return try {
            launcherApps.getShortcuts(query, user)
                .orEmpty()
                .asSequence()
                .filter { it.`package` != ourPackage }
                .mapNotNull(::toPinnedShortcut)
                .sortedWith(
                    compareBy(
                        { it.appLabel.lowercase() },
                        { it.label.lowercase() },
                    ),
                )
                .toList()
        } catch (_: SecurityException) {
            emptyList()
        }
    }

    fun launch(shortcut: PinnedShortcut) {
        if (!launcherApps.hasShortcutHostPermission()) return
        try {
            launcherApps.startShortcut(
                shortcut.packageName,
                shortcut.id,
                null,
                null,
                user,
            )
        } catch (_: SecurityException) {
        } catch (_: IllegalStateException) {
        }
    }

    fun registerOnShortcutsChanged(onChanged: () -> Unit): () -> Unit {
        if (!launcherApps.hasShortcutHostPermission()) return {}

        val callback = object : LauncherApps.Callback() {
            override fun onShortcutsChanged(
                packageName: String,
                shortcuts: MutableList<ShortcutInfo>,
                userHandle: UserHandle,
            ) {
                if (userHandle == user) {
                    onChanged()
                }
            }

            override fun onPackageAdded(packageName: String, userHandle: UserHandle) = Unit

            override fun onPackageChanged(packageName: String, userHandle: UserHandle) = Unit

            override fun onPackageRemoved(packageName: String, userHandle: UserHandle) = Unit

            override fun onPackagesAvailable(
                packageNames: Array<out String>,
                userHandle: UserHandle,
                replacing: Boolean,
            ) = Unit

            override fun onPackagesUnavailable(
                packageNames: Array<out String>,
                userHandle: UserHandle,
                replacing: Boolean,
            ) = Unit
        }
        launcherApps.registerCallback(callback, android.os.Handler(android.os.Looper.getMainLooper()))
        return { launcherApps.unregisterCallback(callback) }
    }

    private fun toPinnedShortcut(info: ShortcutInfo): PinnedShortcut? {
        val label = info.shortLabel?.toString()?.takeIf { it.isNotBlank() }
            ?: info.longLabel?.toString()?.takeIf { it.isNotBlank() }
            ?: return null

        val packageName = info.`package`
        return PinnedShortcut(
            id = info.id,
            packageName = packageName,
            appLabel = resolveAppLabel(packageName),
            label = label,
            icon = loadShortcutIcon(info),
        )
    }

    private fun resolveAppLabel(packageName: String): String {
        return try {
            val pm = appContext.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString().takeIf { it.isNotBlank() }
                ?: packageName
        } catch (_: Exception) {
            packageName
        }
    }

    private fun loadShortcutIcon(info: ShortcutInfo): android.graphics.drawable.Drawable? {
        val density = appContext.resources.displayMetrics.densityDpi
        return try {
            launcherApps.getShortcutIconDrawable(info, density)
        } catch (_: Exception) {
            null
        }
    }
}
