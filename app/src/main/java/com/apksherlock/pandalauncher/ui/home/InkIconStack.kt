package com.apksherlock.pandalauncher.ui.home

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.components.InkAppIcon
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.inkClickable

data class IconStackMetrics(
    val slotSize: Dp,
    val drawSize: Dp,
    val gap: Dp,
    val totalHeight: Dp,
)

@Composable
fun InkIconStack(
    apps: List<LaunchableApp>,
    maxHeight: Dp,
    onAppClick: (LaunchableApp) -> Unit,
    modifier: Modifier = Modifier,
    metrics: IconStackMetrics? = null,
) {
    val count = apps.size.coerceAtMost(5)
    if (count == 0) return

    val density = LocalDensity.current
    val resolved = metrics ?: remember(count, maxHeight) {
        computeIconStackMetrics(count, maxHeight, density)
    }

    Column(
        modifier = modifier.height(resolved.totalHeight),
        verticalArrangement = Arrangement.spacedBy(resolved.gap, Alignment.Bottom),
        horizontalAlignment = Alignment.End,
    ) {
        apps.take(count).forEach { app ->
            val interactionSource = remember(app.componentName) { MutableInteractionSource() }
            val pressed = interactionSource.collectIsPressedAsState().value
            InkAppIcon(
                drawable = app.icon,
                modifier = Modifier
                    .size(resolved.slotSize)
                    .inkClickable(
                        onClick = { onAppClick(app) },
                        contentPadding = InkClickMetrics.none,
                        interactionSource = interactionSource,
                    ),
                compact = true,
                drawSize = resolved.drawSize,
                loadingProgress = app.loadingProgress,
                pressed = pressed,
            )
        }
    }
}

fun computeIconStackMetrics(
    count: Int,
    maxHeight: Dp,
    density: androidx.compose.ui.unit.Density,
): IconStackMetrics {
    val maxHpx = with(density) { maxHeight.roundToPx() }
    val minSlot = with(density) { 32.dp.roundToPx() }
    val maxSlot = with(density) { 44.dp.roundToPx() }
    val minGap = with(density) { 6.dp.roundToPx() }
    val maxGap = with(density) { 12.dp.roundToPx() }

    var gapPx = (maxHpx * 0.08f / count).toInt().coerceIn(minGap, maxGap)
    var slotPx = ((maxHpx - gapPx * (count - 1)) / count).coerceIn(minSlot, maxSlot)
    if (slotPx * count + gapPx * (count - 1) > maxHpx) {
        gapPx = minGap
        slotPx = ((maxHpx - gapPx * (count - 1)) / count).coerceIn(minSlot, maxSlot)
    }
    val drawPx = (slotPx * 0.82f).toInt()

    return IconStackMetrics(
        slotSize = with(density) { slotPx.toDp() },
        drawSize = with(density) { drawPx.toDp() },
        gap = with(density) { gapPx.toDp() },
        totalHeight = with(density) { (slotPx * count + gapPx * (count - 1)).toDp() },
    )
}
