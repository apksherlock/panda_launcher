package com.apksherlock.pandalauncher.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LauncherProfileStore(context: Context) {

    private val dataStore = context.applicationContext.launcherPreferencesDataStore

    /** `false` until the user finishes first-run naming (including skipping with a blank name). */
    val hasCompletedNameSetup: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs.contains(KEY_DISPLAY_NAME)
    }

    /** Stored name, or `null` if setup never completed. May be blank when the user skipped naming. */
    val displayName: Flow<String?> = dataStore.data.map { prefs ->
        if (!prefs.contains(KEY_DISPLAY_NAME)) return@map null
        prefs[KEY_DISPLAY_NAME].orEmpty()
    }

    suspend fun setDisplayName(name: String) {
        val stored = sanitizeStoredName(name)
        dataStore.edit { prefs ->
            prefs[KEY_DISPLAY_NAME] = stored
        }
    }

    suspend fun clearDisplayName() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_DISPLAY_NAME)
        }
    }

    companion object {
        /** How many characters of the saved name appear on the home header. */
        const val HEADER_DISPLAY_NAME_LENGTH = 24

        private val KEY_DISPLAY_NAME = stringPreferencesKey("display_name")

        /** Stores the full string; no length cap. */
        fun sanitizeStoredName(input: String): String =
            input.replace("\n", "").trimEnd('.')

        /** Home / settings summary — first [HEADER_DISPLAY_NAME_LENGTH] chars only. */
        fun formatForHeader(stored: String?): String {
            val base = stored
                ?.takeIf { it.isNotBlank() }
                ?.take(HEADER_DISPLAY_NAME_LENGTH)
                ?: DEFAULT_NAME
            return "${base.trimEnd('.')}."
        }
    }
}

private const val DEFAULT_NAME = "friend"
