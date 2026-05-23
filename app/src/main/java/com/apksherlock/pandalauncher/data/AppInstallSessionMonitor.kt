package com.apksherlock.pandalauncher.data

import android.content.Context
import android.content.pm.PackageInstaller
import android.os.Handler
import android.os.Looper
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks in-flight [PackageInstaller] sessions so apps appear with progress before
 * [android.content.pm.LauncherApps] lists them.
 */
class AppInstallSessionMonitor(context: Context) {

    private val appContext = context.applicationContext
    private val packageInstaller = appContext.packageManager.packageInstaller

    private val sessions = ConcurrentHashMap<Int, InstallSession>()

    data class InstallSession(
        val packageName: String,
        val label: String,
        val progress: Float,
    )

    fun hasActiveSessions(): Boolean = sessions.isNotEmpty()

    fun activeSessions(): List<InstallSession> = sessions.values.toList()

    fun register(onChanged: () -> Unit): () -> Unit {
        val handler = Handler(Looper.getMainLooper())
        val callback = object : PackageInstaller.SessionCallback() {
            override fun onCreated(sessionId: Int) {
                upsertSession(sessionId)
                onChanged()
            }

            override fun onProgressChanged(sessionId: Int, progress: Float) {
                val existing = sessions[sessionId]
                if (existing != null) {
                    sessions[sessionId] = existing.copy(progress = progress.coerceIn(0f, 1f))
                } else {
                    upsertSession(sessionId)
                }
                onChanged()
            }

            override fun onFinished(sessionId: Int, success: Boolean) {
                sessions.remove(sessionId)
                onChanged()
            }

            override fun onBadgingChanged(sessionId: Int) {
                upsertSession(sessionId)
                onChanged()
            }

            override fun onActiveChanged(sessionId: Int, active: Boolean) {
                if (active) {
                    upsertSession(sessionId)
                } else {
                    sessions.remove(sessionId)
                }
                onChanged()
            }
        }
        packageInstaller.registerSessionCallback(callback, handler)
        syncActiveSessions()
        return { packageInstaller.unregisterSessionCallback(callback) }
    }

    private fun syncActiveSessions() {
        sessions.clear()
        packageInstaller.allSessions.forEach { info ->
            upsertSessionInfo(info)
        }
    }

    private fun upsertSession(sessionId: Int) {
        val info = packageInstaller.getSessionInfo(sessionId) ?: return
        upsertSessionInfo(info, sessionId)
    }

    private fun upsertSessionInfo(info: PackageInstaller.SessionInfo, sessionId: Int = info.sessionId) {
        val packageName = info.appPackageName ?: return
        if (!info.isActive) return
        val label = info.appLabel?.toString()?.takeIf { it.isNotBlank() } ?: packageName
        val progress = readSessionProgress(info, sessions[sessionId]?.progress ?: 0f)
        sessions[sessionId] = InstallSession(
            packageName = packageName,
            label = label,
            progress = progress,
        )
    }

    private fun readSessionProgress(
        info: PackageInstaller.SessionInfo,
        fallback: Float,
    ): Float {
        return try {
            info.progress.coerceIn(0f, 1f)
        } catch (_: Exception) {
            fallback.coerceIn(0f, 1f)
        }
    }
}
