package com.apksherlock.pandalauncher.model

import android.content.ComponentName
import android.graphics.drawable.Drawable

data class LaunchableApp(
    val packageName: String,
    val label: String,
    val componentName: ComponentName,
    val icon: Drawable,
)
