package com.apksherlock.pandalauncher.debug

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.apksherlock.pandalauncher.MainActivity
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.data.NotificationRepository
import com.apksherlock.pandalauncher.data.FavoriteAppStore
import com.apksherlock.pandalauncher.data.HomeScreenAppStore
import com.apksherlock.pandalauncher.data.LauncherProfileStore
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkTheme
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import kotlinx.coroutines.launch

class DebugMenuActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DebugNotificationSender.ensureChannel(this)
        setContent {
            InkTheme {
                DebugSystemBars()
                DebugMenuScreen()
            }
        }
    }
}

@Composable
private fun DebugMenuScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val favoriteStore = remember(context) { FavoriteAppStore(context) }
    val homeScreenStore = remember(context) { HomeScreenAppStore(context) }
    val profileStore = remember(context) { LauncherProfileStore(context) }
    val notificationRepository = remember(context) { NotificationRepository(context) }
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text
    val scroll = rememberScrollState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) DebugNotificationSender.postThree(context)
    }

    fun withPostPermission(post: () -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            post()
            return
        }
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
            PackageManager.PERMISSION_GRANTED -> post()
            else -> permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.canvas)
            .statusBarsPadding()
            .verticalScroll(scroll)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InkText(
            text = stringResource(R.string.debug_menu_title),
            style = text.greeting,
        )

        InkText(
            text = stringResource(R.string.debug_section_notifications),
            style = text.dateCaps,
            modifier = Modifier.padding(top = 8.dp),
        )
        InkText(
            text = stringResource(R.string.debug_notifications_hint),
            style = text.notificationEmpty,
        )
        DebugButton(stringResource(R.string.debug_open_notification_access)) {
            notificationRepository.openNotificationAccessSettings()
        }
        DebugButton(stringResource(R.string.debug_post_one)) {
            withPostPermission { DebugNotificationSender.postOne(context) }
        }
        DebugButton(stringResource(R.string.debug_post_three)) {
            withPostPermission { DebugNotificationSender.postThree(context) }
        }
        DebugButton(stringResource(R.string.debug_post_ten)) {
            withPostPermission { DebugNotificationSender.postTen(context) }
        }
        DebugButton(stringResource(R.string.debug_post_twenty)) {
            withPostPermission { DebugNotificationSender.postTwenty(context) }
        }
        DebugButton(stringResource(R.string.debug_clear_notifications)) {
            DebugNotificationSender.clear(context)
        }

        InkText(
            text = stringResource(R.string.debug_section_launcher),
            style = text.dateCaps,
            modifier = Modifier.padding(top = 8.dp),
        )
        DebugButton(stringResource(R.string.debug_open_home)) {
            context.startActivity(
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
            )
        }
        DebugButton(stringResource(R.string.debug_clear_launcher_prefs)) {
            scope.launch {
                favoriteStore.clearFavorite()
                profileStore.clearDisplayName()
                homeScreenStore.clearSlots()
            }
        }
    }
}

@Composable
private fun DebugButton(label: String, onClick: () -> Unit) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text
    InkText(
        text = label,
        style = text.appLabel,
        modifier = Modifier
            .fillMaxWidth()
            .inkClickable(onClick = onClick, contentPadding = InkClickMetrics.settingsRow)
            .background(palette.inkGhost)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    )
}

@Composable
private fun DebugSystemBars() {
    val dark = isSystemInDarkTheme()
    val activity = LocalContext.current as? ComponentActivity ?: return
    SideEffect {
        val window = activity.window
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !dark
            isAppearanceLightNavigationBars = !dark
        }
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
    }
}
