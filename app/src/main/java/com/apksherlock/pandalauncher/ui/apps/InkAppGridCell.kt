package com.apksherlock.pandalauncher.ui.apps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.components.InkAppIcon
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkClickable

private val GridIconDrawSize = 44.dp
private val GridIconSlotSize = GridIconDrawSize + 8.dp

@Composable
fun InkAppGridCell(
    app: LaunchableApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text

    Column(
        modifier = modifier.padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        InkAppIcon(
            drawable = app.icon,
            modifier = Modifier
                .size(GridIconSlotSize)
                .inkClickable(onClick = onClick),
            compact = true,
            drawSize = GridIconDrawSize,
        )
        Spacer(Modifier.height(6.dp))
        InkText(
            text = app.label,
            style = text.gridLabel,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}
