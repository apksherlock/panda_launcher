package com.apksherlock.pandalauncher

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.apksherlock.pandalauncher.ui.launcher.InkLauncherScreen
import com.apksherlock.pandalauncher.ui.theme.InkSystemBars
import com.apksherlock.pandalauncher.ui.theme.InkTheme
import com.apksherlock.pandalauncher.ui.theme.enableInkEdgeToEdge
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MainActivity : ComponentActivity() {

    private val _goHomeRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val goHomeRequests: SharedFlow<Unit> = _goHomeRequests.asSharedFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableInkEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
        setContent {
            InkTheme {
                InkSystemBars()
                InkLauncherScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (isHomeIntent(intent)) {
            _goHomeRequests.tryEmit(Unit)
        }
    }

    private fun isHomeIntent(intent: Intent?): Boolean {
        return intent?.action == Intent.ACTION_MAIN &&
            intent.hasCategory(Intent.CATEGORY_HOME)
    }
}
