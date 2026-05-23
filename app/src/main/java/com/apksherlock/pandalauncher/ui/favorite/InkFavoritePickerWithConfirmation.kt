package com.apksherlock.pandalauncher.ui.favorite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.apksherlock.pandalauncher.model.LaunchableApp

@Composable
fun InkFavoritePickerWithConfirmation(
    apps: List<LaunchableApp>,
    onNavigateBack: () -> Unit,
    onConfirmed: (LaunchableApp) -> Unit,
    swipeThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    var pendingApp by remember { mutableStateOf<LaunchableApp?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        InkFavoritePickerScreen(
            apps = apps,
            onAppSelected = { pendingApp = it },
            onNavigateBack = onNavigateBack,
            swipeThresholdPx = swipeThresholdPx,
        )

        pendingApp?.let { app ->
            InkFavoriteConfirmDialog(
                app = app,
                onConfirm = {
                    onConfirmed(app)
                    pendingApp = null
                },
                onDismiss = { pendingApp = null },
            )
        }
    }
}
