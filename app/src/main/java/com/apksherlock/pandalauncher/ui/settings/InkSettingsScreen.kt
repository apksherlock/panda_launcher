package com.apksherlock.pandalauncher.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.ui.components.InkDivider
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import com.apksherlock.pandalauncher.ui.theme.inkSurface

@Composable
fun InkSettingsScreen(
    displayNameLine: String,
    favoriteAppLine: String,
    homeAppsLine: String,
    appearanceLine: String,
    wallpaperLine: String,
    onChangeDisplayName: () -> Unit,
    onChangeFavoriteApp: () -> Unit,
    onEditHomeApps: () -> Unit,
    onEditAppearance: () -> Unit,
    onEditWallpaper: () -> Unit,
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
            text = stringResource(R.string.settings_title),
            style = text.greeting,
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
        )
        InkSettingsRow(
            label = stringResource(R.string.settings_appearance),
            value = appearanceLine,
            onClick = onEditAppearance,
        )
        InkDivider(modifier = Modifier.padding(vertical = 4.dp))
        InkSettingsRow(
            label = stringResource(R.string.settings_wallpaper),
            value = wallpaperLine,
            onClick = onEditWallpaper,
        )
        InkDivider(modifier = Modifier.padding(vertical = 4.dp))
        InkSettingsRow(
            label = stringResource(R.string.settings_display_name),
            value = displayNameLine,
            onClick = onChangeDisplayName,
        )
        InkDivider(modifier = Modifier.padding(vertical = 4.dp))
        InkSettingsRow(
            label = stringResource(R.string.settings_favorite_app),
            value = favoriteAppLine,
            onClick = onChangeFavoriteApp,
        )
        InkDivider(modifier = Modifier.padding(vertical = 4.dp))
        InkSettingsRow(
            label = stringResource(R.string.settings_home_apps),
            value = homeAppsLine,
            onClick = onEditHomeApps,
        )
    }
}

@Composable
private fun InkSettingsRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text

    Column(
        modifier = modifier
            .fillMaxWidth()
            .inkClickable(onClick = onClick, contentPadding = InkClickMetrics.settingsRow),
    ) {
        InkText(
            text = "> $label",
            style = text.dateCaps,
        )
        InkText(
            text = value,
            style = text.appLabel,
            modifier = Modifier.padding(top = 6.dp),
        )
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
