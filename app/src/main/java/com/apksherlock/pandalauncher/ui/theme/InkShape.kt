package com.apksherlock.pandalauncher.ui.theme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
 */
object InkShape {
    val cornerRadius: Dp = 8.dp
    val corners: RoundedCornerShape = RoundedCornerShape(cornerRadius)
}

/** Padding around ink-ghost press fills — keeps tap targets off glyph bounds. */
object InkClickMetrics {
    val none: PaddingValues = PaddingValues(0.dp)

    /** Standalone labels and inline links (home count, settings copy, etc.). */
    val text: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 10.dp)

    /** Dialog footer actions (cancel / confirm). */
    val dialogAction: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp)

    /** Full-width list rows with icon + label. */
    val listRow: PaddingValues = PaddingValues(horizontal = 4.dp, vertical = 4.dp)

    /** Settings and other multi-line tappable blocks. */
    val settingsRow: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 14.dp)
}

@Composable
fun Modifier.inkSurface(
    color: Color,
    shape: Shape = InkShape.corners,
): Modifier = clip(shape).background(color)

/** Ink-ghost fill on press — no Material ripple. [contentPadding] sizes the hit target and highlight. */
@Composable
fun Modifier.inkClickable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = InkShape.corners,
    contentPadding: PaddingValues = InkClickMetrics.text,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
): Modifier {
    val palette = InkThemeAccessor.palette
    val pressed = interactionSource.collectIsPressedAsState().value
    val fill = if (pressed) palette.inkGhost else Color.Transparent
    return padding(contentPadding)
        .clip(shape)
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
        .inkClickable(onClick = onClick, contentPadding = InkClickMetrics.listRow)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.inkCombinedClickable(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
): Modifier =
    combinedClickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = onClick,
        onLongClick = onLongClick,
    )

