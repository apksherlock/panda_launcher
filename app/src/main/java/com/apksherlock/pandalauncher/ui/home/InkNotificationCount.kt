package com.apksherlock.pandalauncher.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkClickable

@Composable
fun InkNotificationCount(
    count: Int?,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text
    val line = when {
        count == null || count == 0 -> stringResource(R.string.home_notifications_none)
        count == 1 -> stringResource(R.string.home_notifications_one)
        else -> stringResource(R.string.home_notifications_count, count)
    }
    val open = onClick
    val clickable = open != null && count != null && count > 0
    InkText(
        text = line,
        style = text.notificationEmpty,
        modifier = when {
            clickable -> modifier.inkClickable(onClick = open)
            else -> modifier
        },
    )
}
