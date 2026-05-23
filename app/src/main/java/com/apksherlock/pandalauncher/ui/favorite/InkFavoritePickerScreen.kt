package com.apksherlock.pandalauncher.ui.favorite

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.components.InkSearchField
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.home.InkAppRow
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkSurface

@Composable
fun InkFavoritePickerScreen(
    apps: List<LaunchableApp>,
    onAppSelected: (LaunchableApp) -> Unit,
    onNavigateBack: () -> Unit,
    swipeThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text
    var query by remember { mutableStateOf("") }

    val filtered = remember(apps, query) {
        val q = query.trim()
        if (q.isEmpty()) {
            apps
        } else {
            apps.filter { it.label.contains(q, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .inkSurface(color = palette.canvas)
            .detectSwipeLeftToNavigateBack(swipeThresholdPx, onNavigateBack)
            .padding(horizontal = 16.dp),
    ) {
        InkText(
            text = stringResource(R.string.favorite_picker_title),
            style = text.greeting,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        )
        InkText(
            text = stringResource(R.string.favorite_picker_subtitle),
            style = text.notificationEmpty,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        InkSearchField(
            value = query,
            onValueChange = { query = it },
            placeholder = stringResource(R.string.favorite_search_hint),
            modifier = Modifier.padding(bottom = 10.dp),
        )
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            items(
                items = filtered,
                key = { it.componentName.flattenToString() },
            ) { app ->
                InkAppRow(
                    app = app,
                    showDivider = app != filtered.lastOrNull(),
                    onClick = { onAppSelected(app) },
                )
            }
        }
    }
}

private fun Modifier.detectSwipeLeftToNavigateBack(
    thresholdPx: Float,
    onNavigateBack: () -> Unit,
): Modifier = pointerInput(thresholdPx) {
    var accumulated = 0f
    detectHorizontalDragGestures(
        onDragStart = { accumulated = 0f },
        onDragEnd = {
            if (accumulated >= thresholdPx) {
                onNavigateBack()
            }
            accumulated = 0f
        },
        onHorizontalDrag = { _, dragAmount ->
            accumulated += dragAmount
        },
    )
}
