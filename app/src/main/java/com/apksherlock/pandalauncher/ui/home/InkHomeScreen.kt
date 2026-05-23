package com.apksherlock.pandalauncher.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.apksherlock.pandalauncher.data.AppRepository
import com.apksherlock.pandalauncher.data.HomeScreenAppStore
import com.apksherlock.pandalauncher.data.LauncherProfileStore
import com.apksherlock.pandalauncher.data.NotificationRepository
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.requireWallpaperStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun InkHomeScreen(
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val appRepository = remember(context) { AppRepository(context) }
    val homeScreenStore = remember(context) { HomeScreenAppStore(context) }
    val profileStore = remember(context) { LauncherProfileStore(context) }
    val wallpaperStore = remember { context.requireWallpaperStore() }
    val notificationRepository = remember(context) { NotificationRepository(context) }
    val showRibbon by wallpaperStore.ribbonEnabled().collectAsStateWithLifecycle(false)
    val notificationCount by notificationRepository.notificationCount.collectAsStateWithLifecycle(0)
    var hasNotificationAccess by remember { mutableStateOf(notificationRepository.isAccessEnabled()) }
    val scope = rememberCoroutineScope()
    val storedDisplayName by profileStore.displayName.collectAsStateWithLifecycle(null)
    val headerDisplayName = remember(storedDisplayName) {
        LauncherProfileStore.formatForHeader(storedDisplayName)
    }
    val slotComponents by homeScreenStore.slotComponents.collectAsStateWithLifecycle(
        List(HomeScreenAppStore.SLOT_COUNT) { null },
    )

    var allApps by remember { mutableStateOf<List<LaunchableApp>>(emptyList()) }
    var dateLine by remember { mutableStateOf(formatDateLine(Date())) }
    val homeApps = remember(slotComponents, allApps) {
        HomeScreenAppStore.resolveHomeApps(slotComponents, allApps)
    }

    val refreshApps: () -> Unit = {
        scope.launch {
            val loaded = withContext(Dispatchers.Default) {
                appRepository.loadLaunchableApps()
            }
            homeScreenStore.pruneMissingApps(loaded)
            allApps = loaded
            withContext(Dispatchers.Default) {
                homeScreenStore.ensureInitialized(loaded)
            }
        }
    }

    LaunchedEffect(appRepository) {
        if (allApps.isNotEmpty()) return@LaunchedEffect
        val loaded = withContext(Dispatchers.Default) {
            appRepository.loadLaunchableApps()
        }
        allApps = loaded
        withContext(Dispatchers.Default) {
            homeScreenStore.ensureInitialized(loaded)
        }
    }

    DisposableEffect(lifecycleOwner, appRepository, homeScreenStore) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshApps()
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        val unregisterApps = appRepository.registerOnAppsChanged { refreshApps() }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            unregisterApps()
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(60_000)
            dateLine = formatDateLine(Date())
        }
    }

    DisposableEffect(lifecycleOwner, notificationRepository) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasNotificationAccess = notificationRepository.isAccessEnabled()
                if (hasNotificationAccess) {
                    notificationRepository.rebindIfNeeded()
                    notificationRepository.refresh()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(hasNotificationAccess) {
        if (!hasNotificationAccess) return@LaunchedEffect
        while (isActive) {
            notificationRepository.refresh()
            delay(5_000)
        }
    }

    InkHomeContent(
        modifier = modifier,
        username = headerDisplayName,
        dateLine = dateLine,
        notificationCount = if (hasNotificationAccess) notificationCount else null,
        apps = homeApps,
        onAppClick = { appRepository.launch(it) },
        onOpenNotifications = onOpenNotifications,
        showRibbon = showRibbon,
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun InkHomeContent(
    username: String,
    dateLine: String,
    notificationCount: Int?,
    apps: List<LaunchableApp>,
    onAppClick: (LaunchableApp) -> Unit,
    onOpenNotifications: () -> Unit,
    showRibbon: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val pad = 16.dp
    val stackLift = 56.dp
    val stackBottom = pad + 8.dp + stackLift

    Box(modifier = modifier.fillMaxSize()) {
        if (showRibbon) {
            InkHomeRibbon(modifier = Modifier.align(Alignment.CenterEnd))
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            val iconsBudget = minOf(260.dp, (maxHeight - pad * 2) * 0.45f)
            val iconMetrics = remember(apps.size, iconsBudget) {
                if (apps.isEmpty()) null
                else computeIconStackMetrics(
                    apps.size.coerceAtMost(HomeScreenAppStore.SLOT_COUNT),
                    iconsBudget,
                    density,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = pad, top = pad, end = pad, bottom = stackBottom),
            ) {
                InkHeader(
                    username = username,
                    dateLine = dateLine,
                )
                Spacer(Modifier.height(10.dp))
                InkNotificationCount(
                    count = notificationCount,
                    onClick = if (notificationCount != null && notificationCount > 0) {
                        onOpenNotifications
                    } else {
                        null
                    },
                )
            }

            if (iconMetrics != null) {
                InkIconStack(
                    apps = apps,
                    maxHeight = iconsBudget,
                    onAppClick = onAppClick,
                    metrics = iconMetrics,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = 4.dp,
                            bottom = stackBottom,
                            start = pad,
                        ),
                )
            }
        }
    }
}

private fun formatDateLine(date: Date): String =
    SimpleDateFormat("EEEE · MMM dd, yyyy", Locale.US)
        .format(date)
        .uppercase(Locale.US)
