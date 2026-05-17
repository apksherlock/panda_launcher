package com.apksherlock.pandalauncher.ui.icons

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.get

/**
 * Launcher icons for ink UI: monochrome layer when available, else grayscale.
 * Normalizes visual weight so sparse mono assets don't read tiny beside dense ones.
 */
object InkIconLoader {

    val slotDp = 40.dp
    val drawDp = 34.dp
    private const val TARGET_FILL = 0.78f

    fun prepareBitmap(source: Drawable, tint: Color, sizePx: Int): Bitmap {
        val prepared = prepareDrawable(source, tint)
        val raw = prepared.toBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        return normalizeVisualWeight(raw, TARGET_FILL)
    }

    private fun prepareDrawable(source: Drawable, tint: Color): Drawable {
        extractMonochrome(source)?.let { mono ->
            return DrawableCompat.wrap(mono.mutate()).apply {
                DrawableCompat.setTint(this, tint.toArgb())
            }
        }
        return DrawableCompat.wrap(source.mutate()).apply {
            colorFilter = ColorMatrixColorFilter(printedMatrix())
        }
    }

    private fun extractMonochrome(drawable: Drawable): Drawable? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null
        return when (drawable) {
            is AdaptiveIconDrawable -> drawable.monochrome
            else -> unwrapAdaptive(drawable)?.monochrome
        }
    }

    private fun unwrapAdaptive(drawable: Drawable): AdaptiveIconDrawable? {
        if (drawable is AdaptiveIconDrawable) return drawable
        if (drawable is android.graphics.drawable.LayerDrawable) {
            for (i in 0 until drawable.numberOfLayers) {
                val layer = drawable.getDrawable(i)
                if (layer is AdaptiveIconDrawable) return layer
            }
        }
        return null
    }

    /**
     * Scales opaque content so its bounding box fills [targetFill] of the canvas — centered.
     */
    private fun normalizeVisualWeight(bitmap: Bitmap, targetFill: Float): Bitmap {
        val bounds = findOpaqueBounds(bitmap) ?: return bitmap
        val contentSize = maxOf(bounds.width(), bounds.height()).coerceAtLeast(1)
        val canvasMin = minOf(bitmap.width, bitmap.height)
        val currentFill = contentSize.toFloat() / canvasMin
        if (currentFill >= targetFill * 0.92f) return bitmap

        val scale = targetFill / currentFill
        val out = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val src = Rect(bounds.left, bounds.top, bounds.right, bounds.bottom)
        val scaledW = (bounds.width() * scale).toInt()
        val scaledH = (bounds.height() * scale).toInt()
        val dst = Rect(
            (bitmap.width - scaledW) / 2,
            (bitmap.height - scaledH) / 2,
            (bitmap.width + scaledW) / 2,
            (bitmap.height + scaledH) / 2,
        )
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(bitmap, src, dst, paint)
        return out
    }

    private fun findOpaqueBounds(bitmap: Bitmap): Rect? {
        val w = bitmap.width
        val h = bitmap.height
        var minX = w
        var minY = h
        var maxX = 0
        var maxY = 0
        var found = false
        for (y in 0 until h) {
            for (x in 0 until w) {
                if (bitmap[x, y].ushr(24) > 20) {
                    found = true
                    if (x < minX) minX = x
                    if (y < minY) minY = y
                    if (x > maxX) maxX = x
                    if (y > maxY) maxY = y
                }
            }
        }
        if (!found) return null
        return Rect(minX, minY, maxX + 1, maxY + 1)
    }

    private fun Drawable.toBitmap(width: Int, height: Int, config: Bitmap.Config): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }

    private fun printedMatrix(): ColorMatrix = ColorMatrix().apply {
        setSaturation(0f)
    }
}
