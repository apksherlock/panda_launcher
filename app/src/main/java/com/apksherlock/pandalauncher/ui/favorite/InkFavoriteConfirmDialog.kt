package com.apksherlock.pandalauncher.ui.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.components.InkAppIcon
import com.apksherlock.pandalauncher.ui.components.InkDialogContainer
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.components.inkDialogAction
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor

@Composable
fun InkFavoriteConfirmDialog(
    app: LaunchableApp,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text

    InkDialogContainer(
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        InkText(
            text = stringResource(R.string.favorite_confirm_title),
            style = text.dialogTitle,
            modifier = Modifier.align(Alignment.Start),
        )
        Spacer(Modifier.height(20.dp))
        InkAppIcon(
            drawable = app.icon,
            drawSize = 56.dp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(14.dp))
        InkText(
            text = app.label,
            style = text.dialogAction,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            InkText(
                text = stringResource(R.string.display_name_cancel),
                style = text.dialogBody,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .inkDialogAction(onClick = onDismiss),
            )
            InkText(
                text = stringResource(R.string.favorite_confirm_set),
                style = text.dialogAction,
                modifier = Modifier.inkDialogAction(onClick = onConfirm),
            )
        }
    }
}
