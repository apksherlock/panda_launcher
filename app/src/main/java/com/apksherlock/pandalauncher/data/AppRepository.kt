package com.apksherlock.pandalauncher.data

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Process
import android.os.UserHandle
import com.apksherlock.pandalauncher.MainActivity
import com.apksherlock.pandalauncher.model.LaunchableApp

class AppRepository(context: Context) {

    private val appContext = context.applicationContext
    private val launcherApps =
        appContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    private val packageManager = appContext.packageManager
    private val installSessions = AppInstallSessionMonitor(context)
    private val ourPackage = appContext.packageName
    private val homeActivityClass = MainActivity::class.java.name
    private val user: UserHandle = Process.myUserHandle()

    fun hasActiveInstallSessions(): Boolean = installSessions.hasActiveSessions()

    fun loadLaunchableApps(): List<LaunchableApp> {
        val fromLauncher = launcherApps.getActivityList(null, user)
            .asSequence()
            .filter { !isExcludedFromAllApps(it) }
            .mapNotNull { info ->
                val label = info.label?.toString()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                LaunchableApp(
                    packageName = info.componentName.packageName,
                    label = label,
                    componentName = info.componentName,
                    icon = loadLauncherIcon(info),
                    loadingProgress = readLoadingProgress(info),
                )
            }
            .distinctBy { it.componentName.flattenToString() }
            .toList()

        return mergeWithInstallSessions(fromLauncher)
            .sortedBy { it.label.lowercase() }
    }

    fun registerOnAppsChanged(onChanged: () -> Unit): () -> Unit {
        val callback = object : LauncherApps.Callback() {
            override fun onPackageAdded(packageName: String, userHandle: UserHandle) {
                if (userHandle == user) onChanged()
            }

            override fun onPackageChanged(packageName: String, userHandle: UserHandle) {
                if (userHandle == user) onChanged()
            }

            override fun onPackageRemoved(packageName: String, userHandle: UserHandle) {
                if (userHandle == user) onChanged()
            }

            override fun onPackagesAvailable(
                packageNames: Array<out String>,
                userHandle: UserHandle,
                replacing: Boolean,
            ) {
                if (userHandle == user) onChanged()
            }

            override fun onPackagesUnavailable(
                packageNames: Array<out String>,
                userHandle: UserHandle,
                replacing: Boolean,
            ) {
                if (userHandle == user) onChanged()
            }
        }
        launcherApps.registerCallback(callback)
        return { launcherApps.unregisterCallback(callback) }
    }

    fun registerOnInstallSessionsChanged(onChanged: () -> Unit): () -> Unit =
        installSessions.register(onChanged)

    private fun mergeWithInstallSessions(launchable: List<LaunchableApp>): List<LaunchableApp> {
        val byPackage = launchable.associateBy { it.packageName }.toMutableMap()

        for (session in installSessions.activeSessions()) {
            if (session.packageName == ourPackage) continue
            val existing = byPackage[session.packageName]
            if (existing == null) {
                byPackage[session.packageName] = launchableAppFromSession(session)
            } else {
                byPackage[session.packageName] = existing.copy(
                    loadingProgress = minOf(existing.loadingProgress, session.progress),
                )
            }
        }

        return byPackage.values.toList()
    }

    private fun launchableAppFromSession(session: AppInstallSessionMonitor.InstallSession): LaunchableApp {
        val packageName = session.packageName
        val component = launcherApps.getActivityList(packageName, user)
            .firstOrNull()
            ?.componentName
            ?: ComponentName(packageName, INSTALL_STUB_ACTIVITY)

        return LaunchableApp(
            packageName = packageName,
            label = session.label,
            componentName = component,
            icon = loadPackageIcon(packageName),
            loadingProgress = session.progress,
        )
    }

    private fun loadPackageIcon(packageName: String): Drawable {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (_: PackageManager.NameNotFoundException) {
            packageManager.defaultActivityIcon
        }
    }

    private fun readLoadingProgress(info: LauncherActivityInfo): Float {
        return try {
            info.loadingProgress.coerceIn(0f, 1f)
        } catch (_: Exception) {
            1f
        }
    }

    private fun isExcludedFromAllApps(info: LauncherActivityInfo): Boolean {
        if (info.applicationInfo.packageName != ourPackage) return false
        if (isDebugBuild()) {
            return info.componentName.className == homeActivityClass
        }
        return true
    }

    private fun isDebugBuild(): Boolean =
        (appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private fun loadLauncherIcon(info: LauncherActivityInfo): Drawable {
        return try {
            val density = appContext.resources.displayMetrics.densityDpi
            info.getIcon(density)
        } catch (_: Exception) {
            try {
                info.getBadgedIcon(appContext.resources.displayMetrics.densityDpi)
            } catch (_: Exception) {
                packageManager.getApplicationIcon(info.applicationInfo.packageName)
            }
        }
    }

    fun launch(app: LaunchableApp) {
        if (app.isInstalling) return
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = app.componentName
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        appContext.startActivity(intent)
    }

    fun canUninstall(app: LaunchableApp): Boolean {
        if (app.packageName == ourPackage) return false
        return try {
            val flags = packageManager
                .getApplicationInfo(app.packageName, 0)
                .flags
            (flags and ApplicationInfo.FLAG_SYSTEM) == 0
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Opens the system uninstall flow (Package Installer). Confirmation and progress UI are
     * owned by the OS — third-party launchers cannot theme those screens.
     */
    fun startUninstall(from: Context, app: LaunchableApp): Boolean {
        val component = launcherApps.getActivityList(app.packageName, user)
            .firstOrNull()
            ?.componentName
            ?: app.componentName
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.fromParts("package", component.packageName, component.className)
            if (from !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        return try {
            from.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    companion object {
        private const val INSTALL_STUB_ACTIVITY = "com.apksherlock.pandalauncher.InstallStubActivity"
    }
}
