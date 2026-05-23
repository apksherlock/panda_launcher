package com.apksherlock.pandalauncher.ui.shortcuts

import com.apksherlock.pandalauncher.model.PinnedShortcut

val ShortcutIndexLetters: List<Char> =
    ('A'..'Z').toList() + '#'

internal sealed interface ShortcutListEntry {
    val stableKey: String

    data class LetterHeader(val letter: Char) : ShortcutListEntry {
        override val stableKey: String = "letter:$letter"
    }

    data class AppHeader(val appLabel: String) : ShortcutListEntry {
        override val stableKey: String = "app:$appLabel"
    }

    data class ShortcutItem(
        val shortcut: PinnedShortcut,
        val showDivider: Boolean,
    ) : ShortcutListEntry {
        override val stableKey: String = "shortcut:${shortcut.packageName}:${shortcut.id}"
    }
}

internal data class ShortcutCatalog(
    val entries: List<ShortcutListEntry>,
    val letterToIndex: Map<Char, Int>,
)

internal fun buildShortcutCatalog(shortcuts: List<PinnedShortcut>): ShortcutCatalog {
    val sorted = shortcuts.sortedWith(
        compareBy(
            { it.appLabel.lowercase() },
            { it.label.lowercase() },
        ),
    )

    val entries = mutableListOf<ShortcutListEntry>()
    val letterToIndex = mutableMapOf<Char, Int>()

    var currentLetter: Char? = null
    var currentApp: String? = null
    var appShortcutCount = 0

    fun flushAppGroup() {
        if (appShortcutCount == 0) return
        val lastIndex = entries.lastIndex
        if (lastIndex >= 0) {
            val last = entries[lastIndex]
            if (last is ShortcutListEntry.ShortcutItem) {
                entries[lastIndex] = last.copy(showDivider = false)
            }
        }
        appShortcutCount = 0
    }

    for (shortcut in sorted) {
        val letter = shortcut.indexLetter()
        if (letter != currentLetter) {
            flushAppGroup()
            letterToIndex.putIfAbsent(letter, entries.size)
            entries.add(ShortcutListEntry.LetterHeader(letter))
            currentLetter = letter
            currentApp = null
        }
        if (shortcut.appLabel != currentApp) {
            flushAppGroup()
            entries.add(ShortcutListEntry.AppHeader(shortcut.appLabel))
            currentApp = shortcut.appLabel
        }
        entries.add(
            ShortcutListEntry.ShortcutItem(
                shortcut = shortcut,
                showDivider = true,
            ),
        )
        appShortcutCount++
    }
    flushAppGroup()

    return ShortcutCatalog(entries, letterToIndex)
}

private fun PinnedShortcut.indexLetter(): Char {
    val first = appLabel.firstOrNull() ?: return '#'
    val upper = first.uppercaseChar()
    return if (upper in 'A'..'Z') upper else '#'
}
