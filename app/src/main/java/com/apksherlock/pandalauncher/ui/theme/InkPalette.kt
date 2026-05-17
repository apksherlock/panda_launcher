package com.apksherlock.pandalauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Vintage LCD / calculator — green-gray body, dark segments (flips when dim). */
@Immutable
data class InkPalette(
    val ink: Color,
    val milk: Color,
    val inkGhost: Color,
) {
    val onInk: Color get() = milk
    val onMilk: Color get() = ink
    val dateMuted: Color get() = ink.copy(alpha = 0.5f)
}

// Calculator case + LCD window tones
private val PixelLight = Color(0xFF1A2218)
private val BodyLight = Color(0xFFD2D6C6)
private val PixelDark = Color(0xFF9AB092)
private val BodyDark = Color(0xFF121612)

@Composable
fun rememberInkPalette(): InkPalette {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        InkPalette(
            ink = PixelDark,
            milk = BodyDark,
            inkGhost = PixelDark.copy(alpha = 0.12f),
        )
    } else {
        InkPalette(
            ink = PixelLight,
            milk = BodyLight,
            inkGhost = PixelLight.copy(alpha = 0.12f),
        )
    }
}
