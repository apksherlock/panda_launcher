package com.apksherlock.pandalauncher.data

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.apksherlock.pandalauncher.model.LaunchableApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Observable app list: launcher callbacks, install sessions, resume, and install polling.
 */
class LaunchableAppCatalog(
    context: Context,
    private val repository: AppRepository = AppRepository(context),
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _apps = MutableStateFlow<List<LaunchableApp>>(emptyList())
    val apps: StateFlow<List<LaunchableApp>> = _apps.asStateFlow()

    private var started = false
    private var pollJob: Job? = null
    private var unregisterApps: (() -> Unit)? = null
    private var unregisterInstalls: (() -> Unit)? = null
    private var lifecycleObserver: LifecycleEventObserver? = null

    fun start(lifecycle: Lifecycle) {
        if (started) return
        started = true

        scope.launch { refresh() }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch { refresh() }
            }
        }
        lifecycleObserver = observer
        lifecycle.addObserver(observer)

        unregisterApps = repository.registerOnAppsChanged {
            scope.launch { refresh() }
        }
        unregisterInstalls = repository.registerOnInstallSessionsChanged {
            scope.launch { refresh() }
        }
    }

    fun stop(lifecycle: Lifecycle) {
        if (!started) return
        started = false
        pollJob?.cancel()
        pollJob = null
        lifecycleObserver?.let { lifecycle.removeObserver(it) }
        lifecycleObserver = null
        unregisterApps?.invoke()
        unregisterApps = null
        unregisterInstalls?.invoke()
        unregisterInstalls = null
    }

    fun canUninstall(app: LaunchableApp): Boolean = repository.canUninstall(app)

    fun launch(app: LaunchableApp) = repository.launch(app)

    fun startUninstall(from: Context, app: LaunchableApp): Boolean = repository.startUninstall(from, app)

    private suspend fun refresh() {
        val loaded = repository.loadLaunchableApps()
        _apps.value = loaded
        reconcilePolling(loaded)
    }

    private fun reconcilePolling(loaded: List<LaunchableApp>) {
        val installActive = loaded.any { it.isInstalling } || repository.hasActiveInstallSessions()
        if (!installActive) {
            pollJob?.cancel()
            pollJob = null
            return
        }
        if (pollJob?.isActive == true) return
        pollJob = scope.launch {
            while (isActive) {
                delay(POLL_INTERVAL_MS)
                val next = repository.loadLaunchableApps()
                _apps.value = next
                if (!next.any { it.isInstalling } && !repository.hasActiveInstallSessions()) {
                    break
                }
            }
            pollJob = null
        }
    }

    companion object {
        private const val POLL_INTERVAL_MS = 400L
    }
}
