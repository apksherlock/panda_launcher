package com.apksherlock.pandalauncher.ui.apps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
private val GridVerticalGap = 12.dp

@Composable
fun InkAllAppsSheet(
    visible: Boolean,
    apps: List<LaunchableApp>,
    onDismiss: () -> Unit,
    onAppClick: (LaunchableApp) -> Unit,
    swipeThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    var query by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()
    var pullDownDistance by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(visible) {
        if (!visible) {
            query = ""
            pullDownDistance = 0f
        }
    }

    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val dismissOnPullDown = remember(gridState, swipeThresholdPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput) return Offset.Zero
                if (available.y > 0f && !gridState.canScrollBackward) {
                    pullDownDistance += available.y
                    if (pullDownDistance >= swipeThresholdPx) {
                        pullDownDistance = 0f
                        currentOnDismiss()
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

    AnimatedVisibility(
        visible = visible,
        modifier = modifier.fillMaxSize(),
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .inkSurface(color = palette.milk)
                .padding(horizontal = 12.dp),
        ) {
            InkSearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = stringResource(R.string.all_apps_search_hint),
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 10.dp)
                    .detectSwipeDownToClose(swipeThresholdPx, onDismiss),
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = GridMinCellSize),
                state = gridState,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(dismissOnPullDown),
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
                        onClick = { onAppClick(app) },
                    )
                }
            }
        }
    }
}

private fun Modifier.detectSwipeDownToClose(
    thresholdPx: Float,
    onClose: () -> Unit,
): Modifier = pointerInput(thresholdPx) {
    var accumulated = 0f
    detectVerticalDragGestures(
        onDragStart = { accumulated = 0f },
        onDragEnd = {
            if (accumulated >= thresholdPx) {
                onClose()
            }
            accumulated = 0f
        },
        onVerticalDrag = { _, dragAmount ->
            accumulated += dragAmount
        },
    )
}
