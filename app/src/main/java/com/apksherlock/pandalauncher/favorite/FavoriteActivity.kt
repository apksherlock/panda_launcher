package com.apksherlock.pandalauncher.favorite

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.data.AppRepository
import com.apksherlock.pandalauncher.data.FavoriteAppStore
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.navigation.LauncherActivityTransitions
import com.apksherlock.pandalauncher.ui.favorite.InkFavoritePickerWithConfirmation
import com.apksherlock.pandalauncher.ui.theme.InkSystemBars
import com.apksherlock.pandalauncher.ui.theme.InkTheme
import com.apksherlock.pandalauncher.ui.theme.enableInkEdgeToEdge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableInkEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)

        setContent {
            InkTheme {
                InkSystemBars()
                FavoriteActivityContent(
                    onNavigateBack = { LauncherActivityTransitions.closeFavorite(this) },
                )
            }
        }
    }
}

@Composable
private fun FavoriteActivityContent(onNavigateBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val swipeThresholdPx = with(density) { 72.dp.toPx() }

    val appRepository = remember(context) { AppRepository(context) }
    val favoriteStore = remember(context) { FavoriteAppStore(context) }

    var apps by remember { mutableStateOf<List<LaunchableApp>>(emptyList()) }

    LaunchedEffect(Unit) {
        apps = withContext(Dispatchers.Default) {
            appRepository.loadLaunchableApps()
        }
    }

    BackHandler(onBack = onNavigateBack)

    InkFavoritePickerWithConfirmation(
        apps = apps,
        onNavigateBack = onNavigateBack,
        swipeThresholdPx = swipeThresholdPx,
        onConfirmed = { app ->
            scope.launch {
                favoriteStore.setFavorite(app)
                onNavigateBack()
            }
        },
    )
}
