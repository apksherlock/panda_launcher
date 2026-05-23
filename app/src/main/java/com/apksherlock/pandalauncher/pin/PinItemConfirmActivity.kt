package com.apksherlock.pandalauncher.pin

import android.content.pm.LauncherApps
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.components.inkDialogAction
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.InkTheme
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import com.apksherlock.pandalauncher.ui.theme.inkSurface

/**
 * Handles [LauncherApps.ACTION_CONFIRM_PIN_SHORTCUT]. Browsers query for this before
 * showing "Add to launcher"; without it the menu item stays hidden.
 */
class PinItemConfirmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launcherApps = getSystemService(LauncherApps::class.java)
        val request = launcherApps.getPinItemRequest(intent)
        if (request == null || !request.isValid) {
            finish()
            return
        }

        when (request.requestType) {
            LauncherApps.PinItemRequest.REQUEST_TYPE_SHORTCUT -> {
                val shortcut = request.shortcutInfo
                if (shortcut == null) {
                    finish()
                    return
                }
                val label = shortcut.shortLabel?.toString()?.takeIf { it.isNotBlank() }
                    ?: shortcut.longLabel?.toString()?.takeIf { it.isNotBlank() }
                    ?: getString(R.string.pin_shortcut_unknown)

                setContent {
                    InkTheme {
                        PinShortcutConfirmDialog(
                            label = label,
                            onAccept = {
                                request.accept(null)
                                setResult(RESULT_OK)
                                finish()
                            },
                            onDismiss = {
                                setResult(RESULT_CANCELED)
                                finish()
                            },
                        )
                    }
                }
            }
            else -> {
                setContent {
                    InkTheme {
                        PinUnsupportedDialog(
                            onDismiss = {
                                setResult(RESULT_CANCELED)
                                finish()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PinShortcutConfirmDialog(
    label: String,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text

    Box(
        modifier = Modifier
            .fillMaxSize()
            .inkClickable(onClick = onDismiss, contentPadding = InkClickMetrics.none),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .inkSurface(color = palette.canvas)
                .padding(20.dp),
        ) {
            InkText(
                text = stringResource(R.string.pin_shortcut_title),
                style = text.greeting,
            )
            InkText(
                text = label,
                style = text.appLabel,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                InkText(
                    text = stringResource(R.string.pin_shortcut_cancel),
                    style = text.dialogAction,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .inkDialogAction(onClick = onDismiss),
                )
                InkText(
                    text = stringResource(R.string.pin_shortcut_add),
                    style = text.dialogAction,
                    modifier = Modifier.inkDialogAction(onClick = onAccept),
                )
            }
        }
    }
}

@Composable
private fun PinUnsupportedDialog(onDismiss: () -> Unit) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text

    Box(
        modifier = Modifier
            .fillMaxSize()
            .inkClickable(onClick = onDismiss, contentPadding = InkClickMetrics.none),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .inkSurface(color = palette.canvas)
                .padding(20.dp),
        ) {
            InkText(
                text = stringResource(R.string.pin_widget_unsupported),
                style = text.notificationEmpty,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            InkText(
                text = stringResource(R.string.pin_shortcut_cancel),
                style = text.dialogAction,
                modifier = Modifier
                    .align(Alignment.End)
                    .inkDialogAction(onClick = onDismiss),
            )
        }
    }
}
