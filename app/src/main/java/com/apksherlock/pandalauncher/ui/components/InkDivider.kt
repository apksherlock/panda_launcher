package com.apksherlock.pandalauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor

@Composable
fun InkDivider(modifier: Modifier = Modifier) {
    val palette = InkThemeAccessor.palette
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(palette.inkGhost),
    )
}
