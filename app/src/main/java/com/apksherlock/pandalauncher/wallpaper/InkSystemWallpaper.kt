package com.apksherlock.pandalauncher.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.apksherlock.pandalauncher.ui.theme.InkPalette
import androidx.compose.ui.graphics.toArgb

/**
 * Sets the device wallpaper to a flat [InkPalette.canvas] fill.
 * The home ribbon is drawn in Compose — not in the bitmap (avoids stretch artifacts).
 */
object InkSystemWallpaper {

    fun apply(context: Context, palette: InkPalette) {
        val appContext = context.applicationContext
        val (width, height) = bitmapSize(appContext)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        try {
            Canvas(bitmap).drawColor(palette.canvas.toArgb())
            WallpaperManager.getInstance(appContext).setBitmap(bitmap)
        } finally {
            bitmap.recycle()
        }
    }

    private fun bitmapSize(context: Context): Pair<Int, Int> {
        val wm = WallpaperManager.getInstance(context)
        val dm = context.resources.displayMetrics
        val width = maxOf(wm.desiredMinimumWidth, dm.widthPixels).coerceAtLeast(1)
        val height = maxOf(wm.desiredMinimumHeight, dm.heightPixels).coerceAtLeast(1)
        return width to height
    }
}
