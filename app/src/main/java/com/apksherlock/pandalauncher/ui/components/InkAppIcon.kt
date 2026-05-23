package com.apksherlock.pandalauncher.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
    loadingProgress: Float = 1f,
    pressed: Boolean = false,
) {
    val palette = InkThemeAccessor.palette
    val density = LocalDensity.current
    val slotSize = if (compact) drawSize + 8.dp else InkIconLoader.slotDp
    val sizePx = with(density) { drawSize.roundToPx() }
    val installing = loadingProgress < 1f

    val painter = remember(drawable, palette.iconTint, sizePx) {
        drawable?.let {
            BitmapPainter(
                InkIconLoader.prepareBitmap(it, palette.iconTint, sizePx).asImageBitmap(),
            )
        }
    }

    Box(
        modifier = modifier.size(slotSize),
        contentAlignment = Alignment.Center,
    ) {
        val bedSize = if (compact) slotSize else 36.dp
        val plateColor = when {
            pressed -> palette.backplatePressed
            else -> palette.backplate
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
                alpha = if (installing) 0.55f else 1f,
            )
        }
        if (installing) {
            InkInstallProgressRing(
                progress = loadingProgress,
                modifier = Modifier.size(bedSize),
            )
        }
    }
}

@Composable
private fun InkInstallProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    val density = LocalDensity.current
    val strokeWidth = with(density) { 2.dp.toPx() }

    Canvas(modifier = modifier) {
        val inset = strokeWidth / 2f
        val arcSize = size.minDimension - strokeWidth
        drawArc(
            color = palette.ink.copy(alpha = 0.2f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
            size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
        drawArc(
            color = palette.iconTint,
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
            size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
    }
}
