package com.apksherlock.pandalauncher.model

import android.graphics.drawable.Drawable

data class PinnedShortcut(
    val id: String,
    val packageName: String,
    val appLabel: String,
    val label: String,
    val icon: Drawable?,
)
