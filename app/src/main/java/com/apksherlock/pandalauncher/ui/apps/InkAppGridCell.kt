package com.apksherlock.pandalauncher.ui.apps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.components.InkAppIcon
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkCombinedClickable

private val GridIconDrawSize = 44.dp
private val GridIconSlotSize = GridIconDrawSize + 8.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InkAppGridCell(
    app: LaunchableApp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value

    Column(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 6.dp)
            .inkCombinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                interactionSource = interactionSource,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        InkAppIcon(
            drawable = app.icon,
            modifier = Modifier.size(GridIconSlotSize),
            compact = true,
            drawSize = GridIconDrawSize,
            loadingProgress = app.loadingProgress,
            pressed = pressed,
        )
        Spacer(Modifier.height(8.dp))
        InkText(
            text = app.label,
            style = text.gridLabel,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}
