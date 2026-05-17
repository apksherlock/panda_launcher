package com.apksherlock.pandalauncher.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.apksherlock.pandalauncher.data.NotificationRepository
import com.apksherlock.pandalauncher.model.InkNotification
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.wallpaper.rememberInkGbCarWallpaperActive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

@Composable
fun InkHomeScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val appRepository = remember(context) { AppRepository(context) }
    val notificationRepository = remember(context) { NotificationRepository(context) }

    var apps by remember { mutableStateOf<List<LaunchableApp>>(emptyList()) }
    var dateLine by remember { mutableStateOf(formatDateLine(Date())) }
    var hasListenerAccess by remember { mutableStateOf(notificationRepository.isAccessEnabled()) }

    val notifications by notificationRepository.notifications.collectAsStateWithLifecycle()
    val wallpaperBehindHome = rememberInkGbCarWallpaperActive()

    LaunchedEffect(appRepository) {
        apps = withContext(Dispatchers.Default) {
            appRepository.loadLaunchableApps().shuffled().take(5)
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(60_000)
            dateLine = formatDateLine(Date())
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasListenerAccess = notificationRepository.isAccessEnabled()
                notificationRepository.rebindIfNeeded()
                notificationRepository.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(hasListenerAccess) {
        if (!hasListenerAccess) return@LaunchedEffect
        while (isActive) {
            notificationRepository.refresh()
            delay(2_000)
        }
    }

    InkHomeContent(
        modifier = modifier,
        wallpaperBehindHome = wallpaperBehindHome,
        username = "friend.",
        dateLine = dateLine,
        notifications = notifications,
        hasListenerAccess = hasListenerAccess,
        apps = apps,
        onNotificationClick = { notificationRepository.launchNotification(it) },
        onRequestNotificationAccess = { notificationRepository.openNotificationAccessSettings() },
        onAppClick = { appRepository.launch(it) },
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun InkHomeContent(
    wallpaperBehindHome: Boolean,
    username: String,
    dateLine: String,
    notifications: List<InkNotification>,
    hasListenerAccess: Boolean,
    apps: List<LaunchableApp>,
    onNotificationClick: (InkNotification) -> Unit,
    onRequestNotificationAccess: () -> Unit,
    onAppClick: (LaunchableApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    val density = LocalDensity.current
    val pad = 16.dp
    val stackLift = 56.dp
    val stackBottom = pad + 8.dp + stackLift

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (wallpaperBehindHome) {
                    Modifier
                } else {
                    Modifier.background(palette.milk)
                },
            )
            .statusBarsPadding(),
    ) {
        val iconsBudget = minOf(260.dp, (maxHeight - pad * 2) * 0.45f)
        val iconMetrics = remember(apps.size, iconsBudget) {
            if (apps.isEmpty()) null
            else computeIconStackMetrics(apps.size.coerceAtMost(5), iconsBudget, density)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = pad, top = pad, end = pad, bottom = pad),
        ) {
            InkHeader(
                username = username,
                dateLine = dateLine,
            )
        }

        InkNotificationList(
            notifications = notifications,
            hasListenerAccess = hasListenerAccess,
            onNotificationClick = onNotificationClick,
            onRequestAccess = onRequestNotificationAccess,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = pad, end = pad, bottom = stackBottom)
                .fillMaxWidth(0.55f),
        )

        if (iconMetrics != null) {
            InkIconStack(
                apps = apps,
                maxHeight = iconsBudget,
                onAppClick = onAppClick,
                metrics = iconMetrics,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 8.dp,
                        bottom = stackBottom,
                        start = pad,
                    ),
            )
        }
    }
}

private fun formatDateLine(date: Date): String =
    SimpleDateFormat("EEEE · MMM dd, yyyy", Locale.US)
        .format(date)
        .uppercase(Locale.US)
