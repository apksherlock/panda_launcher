package com.apksherlock.pandalauncher.data

import android.content.ComponentName
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.apksherlock.pandalauncher.model.LaunchableApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteAppStore(context: Context) {

    private val dataStore = context.applicationContext.launcherPreferencesDataStore

    val favoriteComponent: Flow<ComponentName?> = dataStore.data.map { prefs ->
        val packageName = prefs[KEY_PACKAGE] ?: return@map null
        val className = prefs[KEY_CLASS] ?: return@map null
        ComponentName(packageName, className)
    }

    suspend fun setFavorite(app: LaunchableApp) {
        dataStore.edit { prefs ->
            prefs[KEY_PACKAGE] = app.packageName
            prefs[KEY_CLASS] = app.componentName.className
        }
    }

    suspend fun clearFavorite() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_PACKAGE)
            prefs.remove(KEY_CLASS)
        }
    }

    companion object {
        private val KEY_PACKAGE = stringPreferencesKey("favorite_package")
        private val KEY_CLASS = stringPreferencesKey("favorite_class")

        fun resolveFavorite(
            component: ComponentName?,
            apps: List<LaunchableApp>,
        ): LaunchableApp? {
            if (component == null) return null
            return apps.find { it.componentName == component }
        }
    }
}
