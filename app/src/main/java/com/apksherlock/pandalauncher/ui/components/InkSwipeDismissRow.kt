package com.apksherlock.pandalauncher.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalViewConfiguration
import com.apksherlock.pandalauncher.ui.theme.InkShape
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkSurface
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Horizontal swipe left or right past [dismissThresholdPx] calls [onDismiss].
 * A short drag that stays within touch slop triggers [onClick].
 * Dismiss hint and [content] share one translation so backplates move with the row.
 */
@Composable
fun InkSwipeDismissRow(
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    dismissThresholdPx: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val touchSlop = LocalViewConfiguration.current.touchSlop
    val palette = InkThemeAccessor.palette

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(InkShape.corners)
                .graphicsLayer {
                    translationX = offsetX.value
                    val progress = (abs(offsetX.value) / dismissThresholdPx).coerceIn(0f, 1f)
                    alpha = 1f - progress * 0.2f
                }
                .pointerInput(dismissThresholdPx, touchSlop) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                val displacement = offsetX.value
                                when {
                                    abs(displacement) >= dismissThresholdPx -> onDismiss()
                                    abs(displacement) < touchSlop -> {
                                        offsetX.snapTo(0f)
                                        onClick()
                                    }
                                    else -> offsetX.animateTo(0f, tween(durationMillis = 180))
                                }
                            }
                        },
                    )
                },
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        val progress = (abs(offsetX.value) / dismissThresholdPx).coerceIn(0f, 1f)
                        alpha = progress
                    }
                    .inkSurface(color = palette.inkGhost),
            )
            content()
        }
    }
}
