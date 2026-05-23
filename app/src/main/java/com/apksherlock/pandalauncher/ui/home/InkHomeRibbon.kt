package com.apksherlock.pandalauncher.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkSurface

/** Right-edge strip on home; toggled via wallpaper settings (not in the device bitmap). */
val InkHomeRibbonWidth = 56.dp

@Composable
fun InkHomeRibbon(modifier: Modifier = Modifier) {
    val palette = InkThemeAccessor.palette
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(InkHomeRibbonWidth)
            .inkSurface(color = palette.ribbon),
    )
}
