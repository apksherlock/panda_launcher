package com.apksherlock.pandalauncher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.apksherlock.pandalauncher.wallpaper.WALLPAPER_RIBBON_ID
import com.apksherlock.pandalauncher.wallpaper.isWallpaperRibbon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WallpaperStore(context: Context) {

    private val dataStore = context.applicationContext.launcherPreferencesDataStore

    val hasCompletedWallpaperSetup: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_SETUP_COMPLETE] == true
    }

    /** Home ribbon on/off — same in light and dark. Device wallpaper is always solid. */
    fun ribbonEnabled(): Flow<Boolean> = dataStore.data.map { prefs ->
        if (prefs[KEY_SETUP_COMPLETE] != true) return@map false
        when {
            prefs.contains(KEY_RIBBON) -> prefs[KEY_RIBBON] == true
            else -> isWallpaperRibbon(readLegacyStyleId(prefs))
        }
    }

    /** @deprecated Use [ribbonEnabled]; kept for call-site migration. */
    fun styleId(darkTheme: Boolean): Flow<String> = ribbonEnabled().map { enabled ->
        if (enabled) WALLPAPER_RIBBON_ID else com.apksherlock.pandalauncher.wallpaper.WALLPAPER_NONE_ID
    }

    suspend fun setRibbonEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_RIBBON] = enabled
        }
    }

    /** @deprecated Use [setRibbonEnabled]. */
    suspend fun setStyle(darkTheme: Boolean, id: String) {
        setRibbonEnabled(isWallpaperRibbon(id))
    }

    suspend fun completeOnboardingSetup(ribbonEnabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_SETUP_COMPLETE] = true
            prefs[KEY_RIBBON] = ribbonEnabled
            prefs.remove(KEY_STYLE_LIGHT)
            prefs.remove(KEY_STYLE_DARK)
        }
    }

    companion object {
        private val KEY_SETUP_COMPLETE = booleanPreferencesKey("wallpaper_setup_complete")
        private val KEY_RIBBON = booleanPreferencesKey("wallpaper_ribbon_enabled")
        private val KEY_STYLE_LIGHT = stringPreferencesKey("wallpaper_style_light")
        private val KEY_STYLE_DARK = stringPreferencesKey("wallpaper_style_dark")

        private fun readLegacyStyleId(prefs: androidx.datastore.preferences.core.Preferences): String {
            return prefs[KEY_STYLE_DARK] ?: prefs[KEY_STYLE_LIGHT] ?: ""
        }
    }
}
