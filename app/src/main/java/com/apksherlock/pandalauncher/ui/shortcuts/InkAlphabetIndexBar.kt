package com.apksherlock.pandalauncher.ui.shortcuts

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor

@Composable
fun InkAlphabetIndexBar(
    letters: List<Char>,
    activeLetters: Set<Char>,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text
    val palette = InkThemeAccessor.palette

    Column(
        modifier = modifier
            .width(28.dp)
            .fillMaxHeight()
            .padding(vertical = 8.dp)
            .letterPickerInput(letters, onLetterSelected),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        letters.forEach { letter ->
            val active = letter in activeLetters
            InkText(
                text = letter.toString(),
                style = text.notificationEmpty.copy(
                    color = if (active) palette.accent else palette.dateMuted.copy(alpha = 0.45f),
                ),
            )
        }
    }
}

private fun Modifier.letterPickerInput(
    letters: List<Char>,
    onLetterSelected: (Char) -> Unit,
): Modifier = pointerInput(letters) {
    fun pickAt(y: Float) {
        if (letters.isEmpty() || size.height <= 0f) return
        val index = ((y / size.height) * letters.size)
            .toInt()
            .coerceIn(0, letters.lastIndex)
        onLetterSelected(letters[index])
    }

    detectTapGestures { offset -> pickAt(offset.y) }
}.pointerInput(letters) {
    fun pickAt(y: Float) {
        if (letters.isEmpty() || size.height <= 0f) return
        val index = ((y / size.height) * letters.size)
            .toInt()
            .coerceIn(0, letters.lastIndex)
        onLetterSelected(letters[index])
    }

    detectVerticalDragGestures(
        onDragStart = { offset -> pickAt(offset.y) },
        onVerticalDrag = { change, _ -> pickAt(change.position.y) },
    )
}
