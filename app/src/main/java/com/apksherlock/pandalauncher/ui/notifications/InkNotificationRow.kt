package com.apksherlock.pandalauncher.ui.notifications

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.model.InkNotification
import com.apksherlock.pandalauncher.ui.components.InkAppIcon
import com.apksherlock.pandalauncher.ui.components.InkSwipeDismissRow
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor

@Composable
fun InkNotificationRow(
    notification: InkNotification,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    dismissThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val text = InkThemeAccessor.text
    val icon: Drawable? = remember(notification.packageName) {
        runCatching {
            context.packageManager.getApplicationIcon(notification.packageName)
        }.getOrNull()
    }

    InkSwipeDismissRow(
        onDismiss = onDismiss,
        onClick = onClick,
        dismissThresholdPx = dismissThresholdPx,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InkAppIcon(drawable = icon)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                InkText(
                    text = notification.title,
                    style = text.notificationTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (notification.subtitle.isNotBlank()) {
                    InkText(
                        text = notification.subtitle,
                        style = text.notificationSubtitle,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}
