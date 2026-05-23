package com.apksherlock.pandalauncher.wallpaper

data class InkWallpaperStyle(
    val id: String,
    val label: String,
)

const val WALLPAPER_NONE_ID = "none"
const val WALLPAPER_RIBBON_ID = "ribbon"

private val AllStyles = listOf(
    InkWallpaperStyle(WALLPAPER_RIBBON_ID, "ribbon"),
    InkWallpaperStyle(WALLPAPER_NONE_ID, "none"),
)

fun inkWallpaperStylesFor(darkTheme: Boolean): List<InkWallpaperStyle> = AllStyles

fun defaultWallpaperStyleId(darkTheme: Boolean): String = WALLPAPER_NONE_ID

fun isWallpaperNone(styleId: String): Boolean = styleId == WALLPAPER_NONE_ID

fun isWallpaperRibbon(styleId: String): Boolean = styleId == WALLPAPER_RIBBON_ID

fun coerceWallpaperStyleId(storedId: String, darkTheme: Boolean): String {
    if (storedId == WALLPAPER_NONE_ID) return WALLPAPER_NONE_ID
    if (storedId == WALLPAPER_RIBBON_ID) return WALLPAPER_RIBBON_ID
    return when (storedId) {
        "gradient", "charcoal", "terminal", "dusk",
        "phosphor_plasma", "scan_grid", "terminal_noise", "signal_bleed", "hex_field",
        "gradient_drift", "scan_drift", "ink_wave", "gb_car", "phosphor_well", "signal_split",
        "crt_horizon", "vignette", "diagonal_cut", "dual_glow", "gradient_glow", "grain_field",
        "gradient_horizon", "gradient_apex",
        -> WALLPAPER_RIBBON_ID
        else -> defaultWallpaperStyleId(darkTheme)
    }
}
