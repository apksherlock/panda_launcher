package com.apksherlock.pandalauncher.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.ui.icons.InkIconLoader
import com.apksherlock.pandalauncher.ui.theme.InkShape
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkSurface

@Composable
fun InkAppIcon(
    drawable: Drawable?,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    drawSize: Dp = InkIconLoader.drawDp,
) {
    val palette = InkThemeAccessor.palette
    val density = LocalDensity.current
    val slotSize = if (compact) drawSize + 8.dp else InkIconLoader.slotDp
    val sizePx = with(density) { drawSize.roundToPx() }

    val painter = remember(drawable, palette.ink, sizePx) {
        drawable?.let {
            BitmapPainter(
                InkIconLoader.prepareBitmap(it, palette.ink, sizePx).asImageBitmap(),
            )
        }
    }

    Box(
        modifier = modifier.size(slotSize),
        contentAlignment = Alignment.Center,
    ) {
        val bedSize = if (compact) slotSize else 36.dp
        val plateColor = if (compact) {
            palette.ink.copy(alpha = 0.24f)
        } else {
            palette.inkGhost
        }

        Box(
            modifier = Modifier
                .size(bedSize)
                .inkSurface(color = plateColor, shape = InkShape.corners),
        )
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(drawSize),
                contentScale = ContentScale.Fit,
            )
        }
    }
}
