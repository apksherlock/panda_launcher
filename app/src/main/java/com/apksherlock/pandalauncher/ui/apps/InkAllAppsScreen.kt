package com.apksherlock.pandalauncher.ui.apps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.components.InkSearchField
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkSurface

private val GridMinCellSize = 84.dp
private val GridHorizontalGap = 2.dp
private val GridVerticalGap = 16.dp

@Composable
fun InkAllAppsScreen(
    apps: List<LaunchableApp>,
    onNavigateBack: () -> Unit,
    onAppClick: (LaunchableApp) -> Unit,
    onAppLongClick: (LaunchableApp) -> Unit,
    menuApp: LaunchableApp?,
    canUninstallMenuApp: Boolean,
    onAddMenuAppToHome: () -> Unit,
    onUninstallMenuApp: () -> Unit,
    onDismissAppMenu: () -> Unit,
    swipeThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette

    val menuVisible = menuApp != null

    BackHandler {
        if (menuVisible) {
            onDismissAppMenu()
        } else {
            onNavigateBack()
        }
    }

    var query by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()
    var pullDownDistance by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        onDispose {
            query = ""
            pullDownDistance = 0f
        }
    }

    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)
    val dismissOnPullDown = remember(gridState, swipeThresholdPx, menuVisible) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (menuVisible) return Offset.Zero
                if (source != NestedScrollSource.UserInput) return Offset.Zero
                if (available.y > 0f && !gridState.canScrollBackward) {
                    pullDownDistance += available.y
                    if (pullDownDistance >= swipeThresholdPx) {
                        pullDownDistance = 0f
                        currentOnNavigateBack()
                    }
                } else if (available.y < 0f) {
                    pullDownDistance = 0f
                }
                return Offset.Zero
            }
        }
    }

    val filtered = remember(apps, query) {
        val q = query.trim()
        if (q.isEmpty()) {
            apps
        } else {
            apps.filter { it.label.contains(q, ignoreCase = true) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .inkSurface(color = palette.canvas)
            .detectSwipeDownToNavigateBack(
                thresholdPx = swipeThresholdPx,
                enabled = !menuVisible,
                onNavigateBack = onNavigateBack,
            ),
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        InkSearchField(
            value = query,
            onValueChange = { query = it },
            placeholder = stringResource(R.string.all_apps_search_hint),
            modifier = Modifier.padding(top = 12.dp, bottom = 10.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = GridMinCellSize),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(dismissOnPullDown)
                .detectSwipeDownToNavigateBack(
                    thresholdPx = swipeThresholdPx,
                    enabled = !menuVisible,
                    onNavigateBack = onNavigateBack,
                ),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(GridHorizontalGap),
            verticalArrangement = Arrangement.spacedBy(GridVerticalGap),
        ) {
            items(
                items = filtered,
                key = { it.componentName.flattenToString() },
            ) { app ->
                InkAppGridCell(
                    app = app,
                    onClick = {
                        if (!app.isInstalling) {
                            onAppClick(app)
                        }
                    },
                    onLongClick = {
                        if (!app.isInstalling) {
                            onAppLongClick(app)
                        }
                    },
                )
            }
        }
    }

        menuApp?.let { app ->
            InkAppContextMenuDialog(
                app = app,
                canUninstall = canUninstallMenuApp,
                onAddToHomeScreen = onAddMenuAppToHome,
                onUninstall = onUninstallMenuApp,
                onDismiss = onDismissAppMenu,
            )
        }
    }
}

private fun Modifier.detectSwipeDownToNavigateBack(
    thresholdPx: Float,
    enabled: Boolean,
    onNavigateBack: () -> Unit,
): Modifier {
    if (!enabled) return this
    return pointerInput(thresholdPx) {
    var accumulated = 0f
    detectVerticalDragGestures(
        onDragStart = { accumulated = 0f },
        onDragEnd = {
            if (accumulated >= thresholdPx) {
                onNavigateBack()
            }
            accumulated = 0f
        },
        onVerticalDrag = { _, dragAmount ->
            accumulated += dragAmount
        },
    )
}
}
