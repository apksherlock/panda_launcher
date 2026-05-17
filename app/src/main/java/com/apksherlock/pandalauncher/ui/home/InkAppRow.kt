package com.apksherlock.pandalauncher.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.components.InkAppIcon
import com.apksherlock.pandalauncher.ui.components.InkDivider
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkListRowClickable

@Composable
fun InkAppRow(
    app: LaunchableApp,
    showDivider: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .inkListRowClickable(onClick = onClick)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InkAppIcon(drawable = app.icon)
            Spacer(Modifier.width(12.dp))
            InkText(text = app.label, style = text.appLabel)
        }
        if (showDivider) {
            InkDivider()
        }
    }
}
