package com.apksherlock.pandalauncher.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shared corner language for Ink Edition — one radius for every surface and press state.
 * Soft editorial round (not pill/circle); use this for all new components.
 */
object InkShape {
    val cornerRadius: Dp = 8.dp
    val corners: RoundedCornerShape = RoundedCornerShape(cornerRadius)
}

@Composable
fun Modifier.inkSurface(
    color: Color,
    shape: Shape = InkShape.corners,
): Modifier = clip(shape).background(color)

/** Ink-ghost fill on press — no Material ripple. */
@Composable
fun Modifier.inkClickable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = InkShape.corners,
): Modifier {
    val palette = InkThemeAccessor.palette
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    val fill = if (pressed) palette.inkGhost else Color.Transparent
    return clip(shape)
        .background(fill)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick,
        )
}

@Composable
fun Modifier.inkListRowClickable(onClick: () -> Unit): Modifier =
    fillMaxWidth()
        .inkClickable(onClick = onClick)
