package com.apksherlock.pandalauncher.ui.shortcuts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.model.PinnedShortcut
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkSurface
import kotlinx.coroutines.launch

@Composable
fun InkShortcutsScreen(
    shortcuts: List<PinnedShortcut>,
    onNavigateBack: () -> Unit,
    onShortcutClick: (PinnedShortcut) -> Unit,
    swipeThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text

    BackHandler(onBack = onNavigateBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .inkSurface(color = palette.canvas)
            .detectSwipeRightToNavigateBack(swipeThresholdPx, onNavigateBack)
            .padding(start = 16.dp, end = 4.dp),
    ) {
        InkText(
            text = stringResource(R.string.shortcuts_title),
            style = text.greeting,
            modifier = Modifier.padding(top = 20.dp, bottom = 12.dp, end = 12.dp),
        )

        if (shortcuts.isEmpty()) {
            InkText(
                text = stringResource(R.string.shortcuts_empty),
                style = text.notificationEmpty,
                modifier = Modifier.padding(top = 8.dp, end = 12.dp),
            )
        } else {
            InkShortcutsList(
                shortcuts = shortcuts,
                onShortcutClick = onShortcutClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun InkShortcutsList(
    shortcuts: List<PinnedShortcut>,
    onShortcutClick: (PinnedShortcut) -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text
    val catalog = remember(shortcuts) { buildShortcutCatalog(shortcuts) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Row(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            items(
                items = catalog.entries,
                key = { it.stableKey },
            ) { entry ->
                when (entry) {
                    is ShortcutListEntry.LetterHeader -> {
                        InkText(
                            text = entry.letter.toString(),
                            style = text.dateCaps,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                        )
                    }
                    is ShortcutListEntry.AppHeader -> {
                        InkText(
                            text = entry.appLabel,
                            style = text.appLabel,
                            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
                        )
                    }
                    is ShortcutListEntry.ShortcutItem -> {
                        InkShortcutRow(
                            shortcut = entry.shortcut,
                            showDivider = entry.showDivider,
                            onClick = { onShortcutClick(entry.shortcut) },
                        )
                    }
                }
            }
        }

        InkAlphabetIndexBar(
            letters = ShortcutIndexLetters,
            activeLetters = catalog.letterToIndex.keys,
            onLetterSelected = { letter ->
                catalog.letterToIndex[letter]?.let { index ->
                    scope.launch {
                        listState.animateScrollToItem(index)
                    }
                }
            },
        )
    }
}

private fun Modifier.detectSwipeRightToNavigateBack(
    thresholdPx: Float,
    onNavigateBack: () -> Unit,
): Modifier = pointerInput(thresholdPx) {
    var accumulated = 0f
    detectHorizontalDragGestures(
        onDragStart = { accumulated = 0f },
        onDragEnd = {
            if (accumulated >= thresholdPx) {
                onNavigateBack()
            }
            accumulated = 0f
        },
        onHorizontalDrag = { _, dragAmount ->
            accumulated += dragAmount
        },
    )
}
