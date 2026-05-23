package com.apksherlock.pandalauncher.ui.theme

import android.graphics.Color
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

/** Transparent edge-to-edge without the default black/white status-bar scrims. */
fun ComponentActivity.enableInkEdgeToEdge() {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.auto(
            lightScrim = Color.TRANSPARENT,
            darkScrim = Color.TRANSPARENT,
        ),
        navigationBarStyle = SystemBarStyle.auto(
            lightScrim = Color.TRANSPARENT,
            darkScrim = Color.TRANSPARENT,
        ),
    )
}

/**
 * Status/nav icon color follows [InkPalette.canvas] (readable on wallpaper + sheets).
 * Disables the system contrast scrim so the bar stays transparent over the wallpaper.
 */
@Composable
fun InkSystemBars() {
    val palette = InkThemeAccessor.palette
    val activity = LocalContext.current as? ComponentActivity ?: return
    val lightBarIcons = palette.canvas.luminance() > 0.5f
    SideEffect {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightBarIcons
            isAppearanceLightNavigationBars = lightBarIcons
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }
}
