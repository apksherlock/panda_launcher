package com.apksherlock.pandalauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.model.InkNotification
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkClickable

private val ConnectorHeight = 28.dp

@Composable
fun InkNotificationList(
    notifications: List<InkNotification>,
    hasListenerAccess: Boolean,
    onNotificationClick: (InkNotification) -> Unit,
    onRequestAccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text
    val items = notifications.take(3)

    if (!hasListenerAccess) {
        InkText(
            text = stringResource(R.string.notifications_enable_access),
            style = text.notificationEmpty,
            modifier = modifier.inkClickable(onClick = onRequestAccess),
        )
        return
    }

    if (items.isEmpty()) {
        InkText(
            text = stringResource(R.string.notifications_all_caught_up),
            style = text.notificationEmpty,
            modifier = modifier,
        )
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, notification ->
            InkNotificationRow(
                notification = notification,
                showConnector = index < items.lastIndex,
                onClick = { onNotificationClick(notification) },
            )
            if (index < items.lastIndex) {
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun InkNotificationRow(
    notification: InkNotification,
    showConnector: Boolean,
    onClick: () -> Unit,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(palette.ink, CircleShape),
            )
            if (showConnector) {
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(ConnectorHeight)
                        .background(palette.inkGhost),
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
        ) {
            InkText(
                text = notification.title,
                style = text.notificationTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (notification.subtitle.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                InkText(
                    text = notification.subtitle,
                    style = text.notificationSubtitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
