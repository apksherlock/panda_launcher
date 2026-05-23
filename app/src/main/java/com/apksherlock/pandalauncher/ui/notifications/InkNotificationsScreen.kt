package com.apksherlock.pandalauncher.ui.notifications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.model.InkNotification
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import com.apksherlock.pandalauncher.ui.theme.inkSurface
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay

@Composable
fun InkNotificationsScreen(
    notifications: List<InkNotification>,
    hasListenerAccess: Boolean,
    onNotificationClick: (InkNotification) -> Unit,
    onDismissNotification: (InkNotification) -> Unit,
    onRequestAccess: () -> Unit,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    swipeThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text
    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler(onBack = onNavigateBack)

    DisposableEffect(lifecycleOwner, hasListenerAccess) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onRefresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(hasListenerAccess) {
        if (!hasListenerAccess) return@LaunchedEffect
        while (isActive) {
            onRefresh()
            delay(2_000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .inkSurface(color = palette.canvas)
            .detectSwipeUpToNavigateBack(swipeThresholdPx, onNavigateBack)
            .padding(horizontal = 16.dp),
    ) {
        InkText(
            text = stringResource(R.string.notifications_screen_title),
            style = text.greeting,
            modifier = Modifier.padding(top = 20.dp, bottom = 4.dp),
        )
        InkText(
            text = stringResource(R.string.notifications_screen_hint),
            style = text.notificationEmpty,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        when {
            !hasListenerAccess -> {
                InkText(
                    text = stringResource(R.string.notifications_enable_access),
                    style = text.notificationEmpty,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .inkClickable(onClick = onRequestAccess),
                )
            }
            notifications.isEmpty() -> {
                InkText(
                    text = stringResource(R.string.home_notifications_none),
                    style = text.notificationEmpty,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    items(
                        items = notifications,
                        key = { it.key },
                    ) { notification ->
                        InkNotificationRow(
                            notification = notification,
                            dismissThresholdPx = swipeThresholdPx,
                            onClick = { onNotificationClick(notification) },
                            onDismiss = { onDismissNotification(notification) },
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.detectSwipeUpToNavigateBack(
    thresholdPx: Float,
    onNavigateBack: () -> Unit,
): Modifier = pointerInput(thresholdPx) {
    var accumulated = 0f
    detectVerticalDragGestures(
        onDragStart = { accumulated = 0f },
        onDragEnd = {
            if (accumulated <= -thresholdPx) {
                onNavigateBack()
            }
            accumulated = 0f
        },
        onVerticalDrag = { _, dragAmount ->
            accumulated += dragAmount
        },
    )
}
