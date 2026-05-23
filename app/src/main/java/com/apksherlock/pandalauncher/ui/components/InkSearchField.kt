package com.apksherlock.pandalauncher.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.ui.theme.InkShape
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkSurface

@Composable
fun InkSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text

    BasicTextField(
        value = value,
        onValueChange = { raw -> onValueChange(raw.replace("\n", "")) },
        modifier = modifier
            .fillMaxWidth()
            .inkSurface(color = palette.inkGhost, shape = InkShape.corners)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        textStyle = text.appLabel.copy(color = palette.accent),
        maxLines = 1,
        cursorBrush = SolidColor(palette.accent),
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    InkText(
                        text = placeholder,
                        style = text.notificationEmpty,
                    )
                }
                inner()
            }
        },
    )
}
