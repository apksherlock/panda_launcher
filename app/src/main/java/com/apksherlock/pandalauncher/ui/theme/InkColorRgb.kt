package com.apksherlock.pandalauncher.ui.theme

/** Shared milk / ink RGBA for GLES wallpaper and Compose fallbacks. */
object InkColorRgb {
    private val BODY_LIGHT = rgba(0xFFD2D6C6.toInt())
    private val PIXEL_LIGHT = rgba(0xFF1A2218.toInt())
    private val BODY_DARK = rgba(0xFF121612.toInt())
    private val PIXEL_DARK = rgba(0xFF9AB092.toInt())

    /** Wallpaper / LCD “off” — milk in light mode, housing in dark mode. */
    fun base(darkTheme: Boolean): FloatArray = if (darkTheme) BODY_DARK else BODY_LIGHT

    /** Wallpaper / LCD “on” — ink in light mode, lit segments in dark mode. */
    fun accent(darkTheme: Boolean): FloatArray = if (darkTheme) PIXEL_DARK else PIXEL_LIGHT

    private fun rgba(argb: Int): FloatArray {
        val a = ((argb ushr 24) and 0xFF) / 255f
        val r = ((argb ushr 16) and 0xFF) / 255f
        val g = ((argb ushr 8) and 0xFF) / 255f
        val b = (argb and 0xFF) / 255f
        return floatArrayOf(r, g, b, a)
    }
}
