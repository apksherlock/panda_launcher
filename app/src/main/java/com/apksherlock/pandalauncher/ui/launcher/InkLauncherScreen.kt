package com.apksherlock.pandalauncher.ui.launcher

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.data.AppRepository
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.apps.InkAllAppsSheet
import com.apksherlock.pandalauncher.ui.home.InkHomeScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun InkLauncherScreen() {
    val context = LocalContext.current
    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val appRepository = remember(context) { AppRepository(context) }
    val swipeThresholdPx = with(density) { 72.dp.toPx() }

    var allAppsOpen by rememberSaveable { mutableStateOf(false) }
    var allApps by remember { mutableStateOf<List<LaunchableApp>>(emptyList()) }
    var appsLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(allAppsOpen, appRepository) {
        if (allAppsOpen && !appsLoaded) {
            allApps = withContext(Dispatchers.Default) {
                appRepository.loadLaunchableApps()
            }
            appsLoaded = true
        }
    }

    BackHandler(enabled = allAppsOpen) {
        allAppsOpen = false
    }

    InkLauncherContent(
        allAppsOpen = allAppsOpen,
        allApps = allApps,
        swipeThresholdPx = swipeThresholdPx,
        onOpenAllApps = { allAppsOpen = true },
        onDismissAllApps = { allAppsOpen = false },
        onAppLaunch = { app ->
            keyboardController?.hide()
            appRepository.launch(app)
        },
    )
}

@Composable
private fun InkLauncherContent(
    allAppsOpen: Boolean,
    allApps: List<LaunchableApp>,
    swipeThresholdPx: Float,
    onOpenAllApps: () -> Unit,
    onDismissAllApps: () -> Unit,
    onAppLaunch: (LaunchableApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        InkHomeScreen(
            modifier = if (!allAppsOpen) {
                Modifier.detectSwipeUpToOpen(swipeThresholdPx, onOpenAllApps)
            } else {
                Modifier
            },
        )

        InkAllAppsSheet(
            visible = allAppsOpen,
            apps = allApps,
            onDismiss = onDismissAllApps,
            onAppClick = onAppLaunch,
            swipeThresholdPx = swipeThresholdPx,
        )
    }
}

private fun Modifier.detectSwipeUpToOpen(
    thresholdPx: Float,
    onOpen: () -> Unit,
): Modifier = pointerInput(thresholdPx) {
    var accumulated = 0f
    detectVerticalDragGestures(
        onDragStart = { accumulated = 0f },
        onDragEnd = {
            if (accumulated <= -thresholdPx) {
                onOpen()
            }
            accumulated = 0f
        },
        onVerticalDrag = { _, dragAmount ->
            accumulated += dragAmount
        },
    )
}
