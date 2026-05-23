package com.apksherlock.pandalauncher.ui.wallpaper

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkPalette
import com.apksherlock.pandalauncher.ui.theme.InkShape
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import com.apksherlock.pandalauncher.ui.theme.inkSurface

@Composable
fun InkRibbonToggleRow(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    palette: InkPalette,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text
    val trackColor = if (enabled) palette.accent else palette.inkGhost
    val thumbColor = if (enabled) palette.canvas else palette.backplate

    Row(
        modifier = modifier
            .fillMaxWidth()
            .inkSurface(color = palette.canvas, shape = InkShape.corners)
            .border(1.dp, palette.ink.copy(alpha = 0.15f), InkShape.corners)
            .inkClickable(
                onClick = { onToggle(!enabled) },
                contentPadding = InkClickMetrics.none,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InkText(
            text = stringResource(R.string.wallpaper_ribbon_toggle),
            style = text.notificationTitle,
        )
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 26.dp)
                .clip(InkShape.corners)
                .inkSurface(color = trackColor, shape = InkShape.corners)
                .border(1.dp, palette.ink.copy(alpha = 0.12f), InkShape.corners)
                .padding(3.dp),
            contentAlignment = if (enabled) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(InkShape.corners)
                    .inkSurface(color = thumbColor, shape = InkShape.corners),
            )
        }
    }
}
