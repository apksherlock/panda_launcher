package com.apksherlock.pandalauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apksherlock.pandalauncher.requireAppearanceStore

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
    val appearanceStore = LocalContext.current.requireAppearanceStore()
    val darkTheme = isSystemInDarkTheme()
    val schemeFlow = remember(darkTheme) { appearanceStore.colorSchemeId(darkTheme) }
    val schemeId by schemeFlow.collectAsStateWithLifecycle(defaultSchemeId(darkTheme))
    val palette = remember(schemeId, darkTheme) {
        resolveInkPalette(schemeId, darkTheme)
    }
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
