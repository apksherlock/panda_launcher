package com.apksherlock.pandalauncher.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

data class InkColorScheme(
    val id: String,
    val label: String,
    val forDarkTheme: Boolean,
    /** Borders, ghosts, wallpaper structure — not primary copy. */
    val ink: Color,
    val canvas: Color,
    val backplate: Color,
    val backplatePressed: Color,
    /** Text, icons, and wallpaper accent — chosen with the scheme. */
    val accent: Color,
)

private val LightSchemes = listOf(
    InkColorScheme(
        id = "ledger",
        label = "ledger",
        forDarkTheme = false,
        ink = Color(0xFF2A2C28),
        canvas = Color(0xFFE2E4DC),
        backplate = Color(0xFFB8BDB0),
        backplatePressed = Color(0xFF9FA598),
        accent = Color(0xFF2A2C28),
    ),
    InkColorScheme(
        id = "ash",
        label = "ash",
        forDarkTheme = false,
        ink = Color(0xFF32302E),
        canvas = Color(0xFFD6D3CC),
        backplate = Color(0xFFAEA9A0),
        backplatePressed = Color(0xFF969088),
        accent = Color(0xFF32302E),
    ),
    InkColorScheme(
        id = "terminal",
        label = "terminal",
        forDarkTheme = false,
        ink = Color(0xFF1E3A24),
        canvas = Color(0xFFE4EDE6),
        backplate = Color(0xFF9CB8A4),
        backplatePressed = Color(0xFF7A9A84),
        accent = Color(0xFF1E3A24),
    ),
    InkColorScheme(
        id = "trace",
        label = "trace",
        forDarkTheme = false,
        ink = Color(0xFF1A3A4A),
        canvas = Color(0xFFE0E8EC),
        backplate = Color(0xFF9AB4C0),
        backplatePressed = Color(0xFF7A98A8),
        accent = Color(0xFF1A3A4A),
    ),
    InkColorScheme(
        id = "solder",
        label = "solder",
        forDarkTheme = false,
        ink = Color(0xFF4A3018),
        canvas = Color(0xFFEDE6D8),
        backplate = Color(0xFFC4A878),
        backplatePressed = Color(0xFFA88858),
        accent = Color(0xFF4A3018),
    ),
    InkColorScheme(
        id = "hematite",
        label = "blood",
        forDarkTheme = false,
        ink = Color(0xFF2A2C28),
        canvas = Color(0xFFE2E4DC),
        backplate = Color(0xFFB8BDB0),
        backplatePressed = Color(0xFF9FA598),
        accent = Color(0xFFC41E1E),
    ),
    InkColorScheme(
        id = "oxide",
        label = "rust",
        forDarkTheme = false,
        ink = Color(0xFF2A2C28),
        canvas = Color(0xFFE2E4DC),
        backplate = Color(0xFFB8BDB0),
        backplatePressed = Color(0xFF9FA598),
        accent = Color(0xFFB84A20),
    ),
    InkColorScheme(
        id = "beacon",
        label = "signal",
        forDarkTheme = false,
        ink = Color(0xFF2A2C28),
        canvas = Color(0xFFE2E4DC),
        backplate = Color(0xFFB8BDB0),
        backplatePressed = Color(0xFF9FA598),
        accent = Color(0xFFC49018),
    ),
    InkColorScheme(
        id = "slate",
        label = "graphite",
        forDarkTheme = false,
        ink = Color(0xFF2A2C28),
        canvas = Color(0xFFE2E4DC),
        backplate = Color(0xFFB8BDB0),
        backplatePressed = Color(0xFF9FA598),
        accent = Color(0xFF3D4038),
    ),
    InkColorScheme(
        id = "azure",
        label = "azure",
        forDarkTheme = false,
        ink = Color(0xFF2A2C28),
        canvas = Color(0xFFE2E4DC),
        backplate = Color(0xFFB8BDB0),
        backplatePressed = Color(0xFF9FA598),
        accent = Color(0xFF2070A0),
    ),
)

private val DarkSchemes = listOf(
    InkColorScheme(
        id = "night",
        label = "night",
        forDarkTheme = true,
        ink = Color(0xFF8FD48F),
        canvas = Color(0xFF121612),
        backplate = Color(0xFF2A3828),
        backplatePressed = Color(0xFF3D5038),
        accent = Color(0xFF8FD48F),
    ),
    InkColorScheme(
        id = "phosphor",
        label = "phosphor",
        forDarkTheme = true,
        ink = Color(0xFF9AE69A),
        canvas = Color(0xFF0C0E0C),
        backplate = Color(0xFF1E2A1E),
        backplatePressed = Color(0xFF2E3E2C),
        accent = Color(0xFF9AE69A),
    ),
    InkColorScheme(
        id = "matrix",
        label = "matrix",
        forDarkTheme = true,
        ink = Color(0xFF5EE85E),
        canvas = Color(0xFF060A06),
        backplate = Color(0xFF142014),
        backplatePressed = Color(0xFF1E301E),
        accent = Color(0xFF5EE85E),
    ),
    InkColorScheme(
        id = "blood",
        label = "blood",
        forDarkTheme = true,
        ink = Color(0xFFE85C5C),
        canvas = Color(0xFF100808),
        backplate = Color(0xFF3A1818),
        backplatePressed = Color(0xFF502020),
        accent = Color(0xFFE85C5C),
    ),
    InkColorScheme(
        id = "ice",
        label = "ice",
        forDarkTheme = true,
        ink = Color(0xFF6EC8D4),
        canvas = Color(0xFF080E10),
        backplate = Color(0xFF1A2830),
        backplatePressed = Color(0xFF243840),
        accent = Color(0xFF6EC8D4),
    ),
    InkColorScheme(
        id = "ember",
        label = "rust",
        forDarkTheme = true,
        ink = Color(0xFF8FD48F),
        canvas = Color(0xFF121612),
        backplate = Color(0xFF2A3828),
        backplatePressed = Color(0xFF3D5038),
        accent = Color(0xFFFF6B35),
    ),
    InkColorScheme(
        id = "flare",
        label = "amber",
        forDarkTheme = true,
        ink = Color(0xFF8FD48F),
        canvas = Color(0xFF121612),
        backplate = Color(0xFF2A3828),
        backplatePressed = Color(0xFF3D5038),
        accent = Color(0xFFFFB020),
    ),
    InkColorScheme(
        id = "cyan",
        label = "cyan",
        forDarkTheme = true,
        ink = Color(0xFF8FD48F),
        canvas = Color(0xFF121612),
        backplate = Color(0xFF2A3828),
        backplatePressed = Color(0xFF3D5038),
        accent = Color(0xFF5EC8E8),
    ),
)

/** @deprecated Use [inkSchemesFor] — kept for migration lookups only. */
val InkColorSchemes: List<InkColorScheme> = LightSchemes + DarkSchemes

fun inkSchemesFor(darkTheme: Boolean): List<InkColorScheme> =
    if (darkTheme) DarkSchemes else LightSchemes

fun defaultSchemeId(darkTheme: Boolean): String = inkSchemesFor(darkTheme).first().id

/**
 * Linked light/dark palettes — picking [blood] in either mode keeps the same family
 * (light [hematite], dark [blood]).
 */
data class InkSchemeFamily(
    val id: String,
    val label: String,
    val lightSchemeId: String,
    val darkSchemeId: String,
)

private val InkSchemeFamilies = listOf(
    InkSchemeFamily("ledger", "ledger", "ledger", "night"),
    InkSchemeFamily("ash", "ash", "ash", "phosphor"),
    InkSchemeFamily("terminal", "terminal", "terminal", "matrix"),
    InkSchemeFamily("trace", "trace", "trace", "cyan"),
    InkSchemeFamily("solder", "solder", "solder", "ember"),
    InkSchemeFamily("blood", "blood", "hematite", "blood"),
    InkSchemeFamily("rust", "rust", "oxide", "ember"),
    InkSchemeFamily("signal", "signal", "beacon", "flare"),
    InkSchemeFamily("graphite", "graphite", "slate", "night"),
    InkSchemeFamily("azure", "azure", "azure", "ice"),
)

fun familyIdForSchemeId(schemeId: String): String {
    InkSchemeFamilies.find { it.lightSchemeId == schemeId || it.darkSchemeId == schemeId }
        ?.let { return it.id }
    return coerceSchemeId(schemeId, darkTheme = true)
        .let { coercedDark ->
            InkSchemeFamilies.find { it.darkSchemeId == coercedDark }?.id
        }
        ?: coerceSchemeId(schemeId, darkTheme = false)
            .let { coercedLight ->
                InkSchemeFamilies.find { it.lightSchemeId == coercedLight }?.id
            }
        ?: InkSchemeFamilies.first().id
}

fun schemeIdForFamily(familyId: String, darkTheme: Boolean): String {
    val family = InkSchemeFamilies.find { it.id == familyId } ?: InkSchemeFamilies.first()
    return if (darkTheme) family.darkSchemeId else family.lightSchemeId
}

fun inkSchemeFamilyLabel(familyId: String): String =
    InkSchemeFamilies.find { it.id == familyId }?.label ?: familyId

fun schemeFamilyMatches(schemeId: String, familyId: String): Boolean =
    familyIdForSchemeId(schemeId) == familyId

fun findInkScheme(storedId: String, darkTheme: Boolean): InkColorScheme {
    val resolvedId = schemeIdForFamily(familyIdForSchemeId(storedId), darkTheme)
    return inkSchemesFor(darkTheme).find { it.id == resolvedId }
        ?: inkSchemesFor(darkTheme).first()
}

fun inkSchemeLabel(schemeId: String): String =
    inkSchemeFamilyLabel(familyIdForSchemeId(schemeId))

fun coerceSchemeId(storedId: String, darkTheme: Boolean): String {
    val allowed = inkSchemesFor(darkTheme)
    if (allowed.any { it.id == storedId }) return storedId
    return migrateLegacySchemeId(storedId, darkTheme)
}

/**
 * Maps legacy separate scheme + icon-tint picks to a single scheme id.
 */
fun migrateLegacyAppearance(schemeId: String, legacyTintId: String?, darkTheme: Boolean): String {
    if (legacyTintId.isNullOrBlank() || legacyTintId == LEGACY_MATCH_TINT_ID) {
        return coerceSchemeId(schemeId, darkTheme)
    }
    val scheme = coerceSchemeId(schemeId, darkTheme)
    val tint = legacyTintId
    val combined = when {
        darkTheme -> when (scheme to tint) {
            "night" to "blood" -> "blood"
            "night" to "rust" -> "ember"
            "night" to "amber" -> "flare"
            "night" to "ice" -> "cyan"
            "night" to "phosphor" -> "phosphor"
            "phosphor" to "phosphor" -> "phosphor"
            "matrix" to "phosphor" -> "matrix"
            "blood" to "blood" -> "blood"
            "ice" to "ice" -> "ice"
            else -> when (tint) {
                "blood" -> "blood"
                "rust" -> "ember"
                "amber" -> "flare"
                "ice" -> "cyan"
                "phosphor" -> "phosphor"
                else -> scheme
            }
        }
        else -> when (scheme to tint) {
            "ledger" to "blood" -> "hematite"
            "ledger" to "rust" -> "oxide"
            "ledger" to "signal" -> "beacon"
            "ledger" to "graphite" -> "slate"
            "ledger" to "trace" -> "azure"
            "ash" to "blood" -> "hematite"
            "ash" to "rust" -> "oxide"
            "ash" to "signal" -> "beacon"
            "ash" to "graphite" -> "slate"
            "ash" to "trace" -> "azure"
            else -> when (tint) {
                "blood" -> "hematite"
                "rust" -> "oxide"
                "signal" -> "beacon"
                "graphite" -> "slate"
                "trace" -> "azure"
                else -> scheme
            }
        }
    }
    return coerceSchemeId(combined, darkTheme)
}

private const val LEGACY_MATCH_TINT_ID = "match"

private fun migrateLegacySchemeId(legacyId: String, darkTheme: Boolean): String {
    return when {
        darkTheme -> when (legacyId) {
            "night", "phosphor", "matrix", "blood", "ice", "ember", "flare", "cyan" -> legacyId
            "ocean" -> "cyan"
            "amber", "rust" -> "ember"
            "terminal" -> "matrix"
            else -> defaultSchemeId(true)
        }
        else -> when (legacyId) {
            "ledger", "ash", "terminal", "trace", "solder",
            "hematite", "oxide", "beacon", "slate", "azure",
            -> legacyId
            "paper" -> "ledger"
            "ocean" -> "azure"
            "amber" -> "beacon"
            "rust" -> "oxide"
            "night", "phosphor", "matrix", "blood", "ice" -> defaultSchemeId(false)
            else -> defaultSchemeId(false)
        }
    }
}

fun resolveInkPalette(storedFamilyOrSchemeId: String, darkTheme: Boolean): InkPalette {
    val scheme = findInkScheme(storedFamilyOrSchemeId, darkTheme)
    return InkPalette(
        ink = scheme.ink,
        canvas = scheme.canvas,
        backplate = scheme.backplate,
        backplatePressed = scheme.backplatePressed,
        ribbon = lerp(scheme.canvas, scheme.accent, 0.32f),
        accent = scheme.accent,
        inkGhost = scheme.ink.copy(alpha = 0.12f),
    )
}
