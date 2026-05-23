package com.apksherlock.pandalauncher.data

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.apksherlock.pandalauncher.ui.theme.defaultSchemeId
import com.apksherlock.pandalauncher.ui.theme.familyIdForSchemeId
import com.apksherlock.pandalauncher.ui.theme.migrateLegacyAppearance
import com.apksherlock.pandalauncher.ui.theme.schemeIdForFamily
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppearanceStore(context: Context) {

    private val dataStore = context.applicationContext.launcherPreferencesDataStore

    val hasCompletedAppearanceSetup: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_SETUP_COMPLETE] == true
    }

    /** Canonical theme family (e.g. [blood] → hematite in light, blood in dark). */
    fun colorSchemeFamilyId(): Flow<String> = dataStore.data.map { prefs ->
        readFamilyId(prefs)
    }

    /** Resolved palette id for the current system light/dark mode. */
    fun colorSchemeId(darkTheme: Boolean): Flow<String> = dataStore.data.map { prefs ->
        val family = readFamilyId(prefs, darkTheme)
        schemeIdForFamily(family, darkTheme)
    }

    suspend fun setColorScheme(darkTheme: Boolean, schemeId: String) {
        val family = familyIdForSchemeId(schemeId)
        dataStore.edit { prefs ->
            prefs[KEY_SCHEME_FAMILY] = family
            prefs.remove(KEY_SCHEME)
            prefs.remove(KEY_SCHEME_LIGHT)
            prefs.remove(KEY_SCHEME_DARK)
            clearLegacyTintKeys(prefs)
        }
    }

    suspend fun normalizeLegacyAppearance() {
        dataStore.edit { prefs ->
            var changed = false
            for (darkTheme in listOf(false, true)) {
                val schemeKey = if (darkTheme) KEY_SCHEME_DARK else KEY_SCHEME_LIGHT
                val tintKey = if (darkTheme) KEY_TINT_DARK else KEY_TINT_LIGHT
                val legacyTint = prefs[tintKey] ?: prefs[LEGACY_TINT_KEY] ?: continue
                if (legacyTint.isBlank() || legacyTint == LEGACY_MATCH_TINT_ID) continue
                val stored = prefs[schemeKey] ?: prefs[LEGACY_SCHEME_KEY] ?: defaultSchemeId(darkTheme)
                val migrated = migrateLegacyAppearance(stored, legacyTint, darkTheme)
                prefs[schemeKey] = migrated
                changed = true
            }
            if (prefs[KEY_SCHEME_FAMILY] == null) {
                val merged = prefs[KEY_SCHEME]
                    ?: prefs[KEY_SCHEME_DARK]
                    ?: prefs[KEY_SCHEME_LIGHT]
                    ?: prefs[LEGACY_SCHEME_KEY]
                if (merged != null) {
                    prefs[KEY_SCHEME_FAMILY] = familyIdForSchemeId(merged)
                    changed = true
                }
            }
            if (changed || prefs.contains(KEY_SCHEME) || prefs.contains(LEGACY_TINT_KEY) ||
                prefs.contains(KEY_TINT_LIGHT) || prefs.contains(KEY_TINT_DARK)
            ) {
                prefs.remove(KEY_SCHEME)
                clearLegacyTintKeys(prefs)
            }
        }
    }

    suspend fun completeOnboardingSetup(schemeId: String) {
        dataStore.edit { prefs ->
            prefs[KEY_SETUP_COMPLETE] = true
            prefs[KEY_SCHEME_FAMILY] = familyIdForSchemeId(schemeId)
            prefs.remove(KEY_SCHEME)
            prefs.remove(KEY_SCHEME_LIGHT)
            prefs.remove(KEY_SCHEME_DARK)
            clearLegacyTintKeys(prefs)
        }
    }

    private fun readFamilyId(
        prefs: androidx.datastore.preferences.core.Preferences,
        darkTheme: Boolean = false,
    ): String {
        prefs[KEY_SCHEME_FAMILY]?.let { return it }
        val raw = prefs[KEY_SCHEME]
            ?: prefs[KEY_SCHEME_DARK]
            ?: prefs[KEY_SCHEME_LIGHT]
            ?: prefs[LEGACY_SCHEME_KEY]
            ?: return defaultSchemeId(darkTheme).let { familyIdForSchemeId(it) }
        val tintKey = if (darkTheme) KEY_TINT_DARK else KEY_TINT_LIGHT
        val legacyTint = prefs[tintKey] ?: prefs[LEGACY_TINT_KEY]
        val schemeId = if (legacyTint.isNullOrBlank() || legacyTint == LEGACY_MATCH_TINT_ID) {
            raw
        } else {
            migrateLegacyAppearance(raw, legacyTint, darkTheme)
        }
        return familyIdForSchemeId(schemeId)
    }

    companion object {
        private const val LEGACY_MATCH_TINT_ID = "match"

        private val KEY_SETUP_COMPLETE = booleanPreferencesKey("appearance_setup_complete")
        private val KEY_SCHEME_FAMILY = stringPreferencesKey("appearance_scheme_family")
        private val KEY_SCHEME = stringPreferencesKey("appearance_scheme")
        private val KEY_SCHEME_LIGHT = stringPreferencesKey("appearance_scheme_light")
        private val KEY_SCHEME_DARK = stringPreferencesKey("appearance_scheme_dark")
        private val KEY_TINT_LIGHT = stringPreferencesKey("appearance_tint_light")
        private val KEY_TINT_DARK = stringPreferencesKey("appearance_tint_dark")
        private val LEGACY_SCHEME_KEY = stringPreferencesKey("appearance_color_scheme")
        private val LEGACY_TINT_KEY = stringPreferencesKey("appearance_icon_tint")

        private fun clearLegacyTintKeys(prefs: MutablePreferences) {
            prefs.remove(KEY_TINT_LIGHT)
            prefs.remove(KEY_TINT_DARK)
            prefs.remove(LEGACY_TINT_KEY)
        }
    }
}
