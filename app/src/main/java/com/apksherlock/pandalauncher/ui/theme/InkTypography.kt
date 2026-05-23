package com.apksherlock.pandalauncher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.apksherlock.pandalauncher.R

@Composable
fun inkFontFamilies(): InkFonts {
    val playfairItalic = FontFamily(Font(R.font.playfair_display_italic, weight = FontWeight.Normal, style = FontStyle.Italic))
    val playfairBold = FontFamily(Font(R.font.playfair_display_bold, weight = FontWeight.Bold))
    val script = FontFamily(Font(R.font.dancing_script_regular, weight = FontWeight.Normal))
    val mono = FontFamily.Monospace
    return InkFonts(playfairItalic, playfairBold, script, mono)
}

data class InkFonts(
    val playfairItalic: FontFamily,
    val playfairBold: FontFamily,
    val script: FontFamily,
    val mono: FontFamily,
)

@Composable
fun inkTextStyles(fonts: InkFonts, palette: InkPalette): InkTextStyles = InkTextStyles(
    greeting = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = palette.accent,
        letterSpacing = 0.5.sp,
    ),
    username = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 20.sp,
        fontWeight = FontWeight.Normal,
        color = palette.accent,
        letterSpacing = 0.5.sp,
    ),
    dateCaps = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = palette.dateMuted,
        letterSpacing = 1.2.sp,
    ),
    weather = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        color = palette.accent,
        lineHeight = 14.sp,
    ),
    battery = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = palette.accent,
        letterSpacing = 0.8.sp,
    ),
    clockPill = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = palette.onInk,
        letterSpacing = 0.sp,
    ),
    ribbonDay = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = palette.onInk,
    ),
    appLabel = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = palette.accent,
    ),
    notificationTitle = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = palette.accent,
        letterSpacing = 0.3.sp,
    ),
    notificationSubtitle = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = palette.dateMuted,
        letterSpacing = 0.2.sp,
        lineHeight = 16.sp,
    ),
    notificationEmpty = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        color = palette.dateMuted,
        letterSpacing = 0.5.sp,
    ),
    dialogTitle = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = palette.accent,
        letterSpacing = 0.5.sp,
    ),
    dialogBody = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        color = palette.dateMuted,
        letterSpacing = 0.3.sp,
        lineHeight = 20.sp,
    ),
    dialogAction = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium,
        color = palette.accent,
    ),
    gridLabel = TextStyle(
        fontFamily = fonts.mono,
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        color = palette.accent,
        letterSpacing = 0.2.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
    ),
)

data class InkTextStyles(
    val greeting: TextStyle,
    val username: TextStyle,
    val dateCaps: TextStyle,
    val weather: TextStyle,
    val battery: TextStyle,
    val clockPill: TextStyle,
    val ribbonDay: TextStyle,
    val appLabel: TextStyle,
    val notificationTitle: TextStyle,
    val notificationSubtitle: TextStyle,
    val notificationEmpty: TextStyle,
    val dialogTitle: TextStyle,
    val dialogBody: TextStyle,
    val dialogAction: TextStyle,
    val gridLabel: TextStyle,
)
