package com.apksherlock.pandalauncher.ui.apps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.data.HomeScreenAppStore
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.components.InkDialogContainer
import com.apksherlock.pandalauncher.ui.components.InkDivider
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.components.inkDialogAction
import com.apksherlock.pandalauncher.ui.home.InkAppRow
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.inkClickable

@Composable
fun InkHomeSlotAssignDialog(
    app: LaunchableApp,
    slotApps: List<LaunchableApp?>,
    onSlotSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text
    val scrollState = rememberScrollState()

    InkDialogContainer(
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
        ) {
            InkText(
                text = stringResource(R.string.home_slot_assign_title),
                style = text.dialogTitle,
            )
            Spacer(Modifier.height(8.dp))
            InkText(
                text = stringResource(R.string.home_slot_assign_subtitle),
                style = text.dialogBody,
            )
            Spacer(Modifier.height(16.dp))
            InkText(
                text = stringResource(R.string.home_slot_assign_adding, app.label),
                style = text.dialogAction,
            )
            Spacer(Modifier.height(16.dp))

            val slots = slotApps.take(HomeScreenAppStore.SLOT_COUNT)
                .let { list ->
                    if (list.size >= HomeScreenAppStore.SLOT_COUNT) {
                        list
                    } else {
                        list + List(HomeScreenAppStore.SLOT_COUNT - list.size) { null }
                    }
                }

            slots.forEachIndexed { index, occupant ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .inkClickable(
                            onClick = { onSlotSelected(index) },
                            contentPadding = InkClickMetrics.settingsRow,
                        ),
                ) {
                    InkText(
                        text = stringResource(R.string.settings_home_slot_label, index + 1),
                        style = text.dateCaps,
                        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
                    )
                    when {
                        occupant == null -> {
                            InkText(
                                text = stringResource(R.string.home_slot_assign_empty_hint),
                                style = text.dialogBody,
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                        }
                        occupant.packageName == app.packageName -> {
                            InkText(
                                text = stringResource(R.string.home_slot_assign_already_here, occupant.label),
                                style = text.dialogBody,
                                modifier = Modifier.padding(bottom = 6.dp),
                            )
                            InkAppRow(
                                app = occupant,
                                showDivider = false,
                                onClick = { onSlotSelected(index) },
                            )
                        }
                        else -> {
                            InkText(
                                text = stringResource(R.string.home_slot_assign_current, occupant.label),
                                style = text.dialogBody,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                            InkText(
                                text = stringResource(R.string.home_slot_assign_replace, occupant.label),
                                style = text.dateCaps,
                                modifier = Modifier.padding(bottom = 6.dp),
                            )
                            InkAppRow(
                                app = occupant,
                                showDivider = false,
                                onClick = { onSlotSelected(index) },
                            )
                        }
                    }
                    if (index < slots.lastIndex) {
                        InkDivider(modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            InkText(
                text = stringResource(R.string.display_name_cancel),
                style = text.dialogBody,
                modifier = Modifier
                    .align(Alignment.End)
                    .inkDialogAction(onClick = onDismiss),
            )
        }
    }
}
