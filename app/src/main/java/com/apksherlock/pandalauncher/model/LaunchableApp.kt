package com.apksherlock.pandalauncher.model

import android.content.ComponentName
import android.graphics.drawable.Drawable

data class LaunchableApp(
    val packageName: String,
    val label: String,
    val componentName: ComponentName,
    val icon: Drawable,
    /** Package install progress from [android.content.pm.LauncherActivityInfo.getLoadingProgress]; 1 = ready. */
    val loadingProgress: Float = 1f,
) {
    val isInstalling: Boolean
        get() = loadingProgress < 1f
}
