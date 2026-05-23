package com.apksherlock.pandalauncher.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.components.InkDivider
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.home.InkAppRow
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import com.apksherlock.pandalauncher.ui.theme.inkSurface

@Composable
fun InkHomeAppsEditorScreen(
    slotApps: List<LaunchableApp?>,
    onEditSlot: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    swipeThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text

    BackHandler(onBack = onNavigateBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .inkSurface(color = palette.canvas)
            .detectSwipeUpToNavigateBack(swipeThresholdPx, onNavigateBack)
            .padding(horizontal = 16.dp),
    ) {
        InkText(
            text = stringResource(R.string.settings_home_apps_title),
            style = text.greeting,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        )
        InkText(
            text = stringResource(R.string.settings_home_apps_subtitle),
            style = text.notificationEmpty,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            itemsIndexed(
                items = slotApps,
                key = { index, _ -> "home_slot_$index" },
            ) { index, app ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .inkClickable(
                            onClick = { onEditSlot(index) },
                            contentPadding = InkClickMetrics.settingsRow,
                        ),
                ) {
                    InkText(
                        text = stringResource(R.string.settings_home_slot_label, index + 1),
                        style = text.dateCaps,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    )
                    if (app != null) {
                        InkAppRow(
                            app = app,
                            showDivider = false,
                            onClick = { onEditSlot(index) },
                        )
                    } else {
                        InkText(
                            text = stringResource(R.string.settings_home_slot_empty),
                            style = text.notificationEmpty,
                            modifier = Modifier.padding(vertical = 16.dp),
                        )
                    }
                    InkDivider()
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
