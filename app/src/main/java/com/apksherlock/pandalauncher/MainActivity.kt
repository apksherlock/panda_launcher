package com.apksherlock.pandalauncher

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import com.apksherlock.pandalauncher.ui.launcher.InkLauncherScreen
import com.apksherlock.pandalauncher.ui.theme.InkTheme
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
        setContent {
            InkTheme {
                InkSystemBars()
                InkLauncherScreen()
            }
        }
    }
}

@Composable
private fun InkSystemBars() {
    val dark = isSystemInDarkTheme()
    val activity = androidx.compose.ui.platform.LocalContext.current as? ComponentActivity ?: return
    androidx.compose.runtime.SideEffect {
        val window = activity.window
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !dark
            isAppearanceLightNavigationBars = !dark
        }
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }
}
