package com.apksherlock.pandalauncher.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor

/** Terminal-style hard blink for the `>` prompt. */
@Composable
fun InkBlinkingPrompt(
    modifier: Modifier = Modifier,
    style: TextStyle = InkThemeAccessor.text.greeting,
) {
    val palette = InkThemeAccessor.palette
    val infiniteTransition = rememberInfiniteTransition(label = "promptBlink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                1f at 0
                1f at 450
                0f at 450
                0f at 900
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "promptAlpha",
    )
    InkText(
        text = ">",
        style = style.copy(color = palette.accent.copy(alpha = alpha)),
        modifier = modifier,
    )
}
