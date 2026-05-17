package com.apksherlock.pandalauncher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalInkPalette = staticCompositionLocalOf<InkPalette> {
    error("InkPalette not provided")
}
val LocalInkFonts = staticCompositionLocalOf<InkFonts> {
    error("InkFonts not provided")
}
val LocalInkTextStyles = staticCompositionLocalOf<InkTextStyles> {
    error("InkTextStyles not provided")
}

@Composable
fun InkTheme(content: @Composable () -> Unit) {
    val palette = rememberInkPalette()
    val fonts = inkFontFamilies()
    val text = inkTextStyles(fonts, palette)
    CompositionLocalProvider(
        LocalInkPalette provides palette,
        LocalInkFonts provides fonts,
        LocalInkTextStyles provides text,
    ) {
        content()
    }
}

object InkThemeAccessor {
    val palette: InkPalette
        @Composable get() = LocalInkPalette.current
    val text: InkTextStyles
        @Composable get() = LocalInkTextStyles.current
}
