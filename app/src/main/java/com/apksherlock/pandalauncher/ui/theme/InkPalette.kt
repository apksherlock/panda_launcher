package com.apksherlock.pandalauncher.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class InkPalette(
    val ink: Color,
    val canvas: Color,
    val inkGhost: Color,
    val backplate: Color,
    val backplatePressed: Color,
    /** Home ribbon strip — scheme tint, not app tile backplate. */
    val ribbon: Color,
    /** Text, icons — from the active color scheme. */
    val accent: Color,
) {
    /** @deprecated Use [accent] — kept for call sites not yet renamed. */
    val iconTint: Color get() = accent

    val onInk: Color get() = canvas
    val onCanvas: Color get() = accent
    val dateMuted: Color get() = accent.copy(alpha = 0.55f)
}
