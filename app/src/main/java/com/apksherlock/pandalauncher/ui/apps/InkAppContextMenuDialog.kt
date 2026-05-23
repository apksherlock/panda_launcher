package com.apksherlock.pandalauncher.ui.apps

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
import com.apksherlock.pandalauncher.ui.components.InkDivider
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.components.inkDialogAction
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor

@Composable
fun InkAppContextMenuDialog(
    app: LaunchableApp,
    canUninstall: Boolean,
    onAddToHomeScreen: () -> Unit,
    onUninstall: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text

    InkDialogContainer(
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        InkAppIcon(
            drawable = app.icon,
            drawSize = 56.dp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(12.dp))
        InkText(
            text = app.label,
            style = text.dialogAction,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(20.dp))
        InkText(
            text = stringResource(R.string.app_action_add_to_home),
            style = text.dialogAction,
            modifier = Modifier
                .fillMaxWidth()
                .inkDialogAction(onClick = onAddToHomeScreen),
        )
        if (canUninstall) {
            InkDivider(modifier = Modifier.padding(vertical = 4.dp))
            InkText(
                text = stringResource(R.string.app_action_uninstall),
                style = text.dialogAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .inkDialogAction(onClick = onUninstall),
            )
        }
        InkDivider(modifier = Modifier.padding(vertical = 4.dp))
        InkText(
            text = stringResource(R.string.display_name_cancel),
            style = text.dialogBody,
            modifier = Modifier
                .align(Alignment.End)
                .inkDialogAction(onClick = onDismiss),
        )
    }
}
