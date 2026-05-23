package com.apksherlock.pandalauncher.ui.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkShape
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import com.apksherlock.pandalauncher.ui.theme.inkSurface

@Composable
fun InkOnboardingShell(
    stepLabel: String,
    title: String,
    subtitle: String,
    continueLabel: String,
    continueEnabled: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .inkSurface(color = palette.canvas)
            .padding(horizontal = 16.dp),
    ) {
        InkText(
            text = stepLabel,
            style = text.dateCaps,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        )
        InkText(
            text = title,
            style = text.greeting,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        InkText(
            text = subtitle,
            style = text.notificationEmpty,
            modifier = Modifier.padding(bottom = 20.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            content = content,
        )
        val buttonInk = if (continueEnabled) palette.ink else palette.ink.copy(alpha = 0.35f)
        val buttonCanvas = if (continueEnabled) palette.canvas else palette.canvas.copy(alpha = 0.5f)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .clip(InkShape.corners)
                .then(
                    if (continueEnabled) {
                        Modifier
                            .inkSurface(color = buttonInk)
                            .inkClickable(
                                onClick = onContinue,
                                contentPadding = InkClickMetrics.none,
                            )
                    } else {
                        Modifier.inkSurface(color = palette.backplate)
                    },
                )
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            InkText(
                text = continueLabel,
                style = text.dialogAction.copy(color = if (continueEnabled) buttonCanvas else palette.ink.copy(alpha = 0.4f)),
            )
        }
    }
}
