package com.apksherlock.pandalauncher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor

@Composable
fun InkDisplayNameDialog(
    title: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = stringResource(R.string.display_name_hint),
    confirmLabel: String = stringResource(R.string.onboarding_name_confirm),
    onDismiss: (() -> Unit)? = null,
) {
    val text = InkThemeAccessor.text
    var value by remember(initialValue) { mutableStateOf(initialValue) }

    InkDialogContainer(
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        InkText(
            text = title,
            style = text.dialogTitle,
        )
        InkText(
            text = stringResource(R.string.display_name_optional_subtitle),
            style = text.dialogBody,
            modifier = Modifier.padding(top = 8.dp),
        )
        InkSearchField(
            value = value,
            onValueChange = { value = it },
            placeholder = hint,
            modifier = Modifier.padding(top = 16.dp, bottom = 20.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            if (onDismiss != null) {
                InkText(
                    text = stringResource(R.string.display_name_cancel),
                    style = text.dialogAction,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .inkDialogAction(onClick = onDismiss),
                )
            }
            InkText(
                text = confirmLabel,
                style = text.dialogAction,
                modifier = Modifier.inkDialogAction(onClick = { onConfirm(value) }),
            )
        }
    }
}
