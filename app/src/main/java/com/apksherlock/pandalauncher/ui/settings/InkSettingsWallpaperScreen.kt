package com.apksherlock.pandalauncher.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.requireWallpaperStore
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkShape
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkSurface
import com.apksherlock.pandalauncher.ui.theme.resolveInkPalette
import com.apksherlock.pandalauncher.ui.wallpaper.InkRibbonToggleRow
import com.apksherlock.pandalauncher.ui.wallpaper.InkWallpaperStylePreview
import com.apksherlock.pandalauncher.wallpaper.WALLPAPER_NONE_ID
import com.apksherlock.pandalauncher.wallpaper.WALLPAPER_RIBBON_ID
import androidx.compose.ui.platform.LocalContext

@Composable
fun InkSettingsWallpaperScreen(
    appearanceSchemeId: String,
    onRibbonChanged: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    swipeThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text
    val context = LocalContext.current
    val wallpaperStore = remember { context.requireWallpaperStore() }
    val darkTheme = isSystemInDarkTheme()
    val previewPalette = remember(darkTheme, appearanceSchemeId) {
        resolveInkPalette(appearanceSchemeId, darkTheme)
    }
    val storedRibbon by wallpaperStore.ribbonEnabled().collectAsStateWithLifecycle(false)
    var ribbonOn by remember { mutableStateOf(storedRibbon) }
    LaunchedEffect(storedRibbon) {
        ribbonOn = storedRibbon
    }
    val previewStyleId = if (ribbonOn) WALLPAPER_RIBBON_ID else WALLPAPER_NONE_ID

    BackHandler(onBack = onNavigateBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .inkSurface(color = palette.canvas)
            .detectSwipeUpToNavigateBack(swipeThresholdPx, onNavigateBack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        InkText(
            text = stringResource(R.string.settings_wallpaper_title),
            style = text.greeting,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        )
        InkText(
            text = stringResource(R.string.settings_wallpaper_subtitle),
            style = text.notificationEmpty,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        InkText(
            text = stringResource(R.string.settings_wallpaper_live_preview),
            style = text.dateCaps,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(bottom = 20.dp)
                .clip(InkShape.corners)
                .border(1.dp, palette.ink.copy(alpha = 0.2f), InkShape.corners),
        ) {
            InkWallpaperStylePreview(
                styleId = previewStyleId,
                palette = previewPalette,
                modifier = Modifier.fillMaxSize(),
            )
        }

        InkRibbonToggleRow(
            enabled = ribbonOn,
            onToggle = {
                ribbonOn = it
                onRibbonChanged(it)
            },
            palette = previewPalette,
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
