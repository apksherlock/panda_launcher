package com.apksherlock.pandalauncher.ui.wallpaper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.apksherlock.pandalauncher.ui.home.InkHomeRibbonWidth
import com.apksherlock.pandalauncher.ui.theme.InkPalette
import com.apksherlock.pandalauncher.ui.theme.inkSurface
import com.apksherlock.pandalauncher.wallpaper.isWallpaperRibbon

/** Settings / onboarding: solid fill + optional Compose ribbon (same as home). */
@Composable
fun InkWallpaperStylePreview(
    styleId: String,
    palette: InkPalette,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .inkSurface(color = palette.canvas),
    ) {
        if (isWallpaperRibbon(styleId)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(InkHomeRibbonWidth)
                    .inkSurface(color = palette.ribbon),
            )
        }
    }
}
