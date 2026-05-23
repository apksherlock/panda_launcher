package com.apksherlock.pandalauncher.ui.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import com.apksherlock.pandalauncher.ui.theme.inkSurface

object InkDialogMetrics {
    val screenHorizontalPadding = 20.dp
    val cardPadding = 24.dp
}

@Composable
fun InkDialogContainer(
    onDismiss: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = InkThemeAccessor.palette
    val cardInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeVerticalDragGestures()
                .inkSurface(color = palette.ink.copy(alpha = 0.35f))
                .then(
                    if (onDismiss != null) {
                        Modifier.inkClickable(
                            onClick = onDismiss,
                            contentPadding = InkClickMetrics.none,
                        )
                    } else {
                        Modifier
                    },
                ),
        )
        Column(
            modifier = Modifier
                .padding(horizontal = InkDialogMetrics.screenHorizontalPadding)
                .fillMaxWidth()
                .inkSurface(color = palette.canvas)
                .clickable(
                    interactionSource = cardInteractionSource,
                    indication = null,
                    onClick = {},
                )
                .padding(InkDialogMetrics.cardPadding),
            content = content,
        )
    }
}

/** Dialog text action — padded ink-ghost press target. */
@Composable
fun Modifier.inkDialogAction(onClick: () -> Unit): Modifier =
    inkClickable(onClick = onClick, contentPadding = InkClickMetrics.dialogAction)

/** Keeps vertical swipes on modal scrims from reaching screens underneath. */
private fun Modifier.consumeVerticalDragGestures(): Modifier = pointerInput(Unit) {
    detectVerticalDragGestures { _, _ -> }
}
