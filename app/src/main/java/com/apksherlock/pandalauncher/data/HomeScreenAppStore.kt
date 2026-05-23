package com.apksherlock.pandalauncher.data

import android.content.ComponentName
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.apksherlock.pandalauncher.model.LaunchableApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeScreenAppStore(context: Context) {

    private val dataStore = context.applicationContext.launcherPreferencesDataStore

    val slotComponents: Flow<List<ComponentName?>> = dataStore.data.map { prefs ->
        List(SLOT_COUNT) { index ->
            unflattenComponent(prefs[slotKey(index)])
        }
    }

    suspend fun ensureInitialized(allApps: List<LaunchableApp>) {
        if (allApps.isEmpty()) return
        dataStore.edit { prefs ->
            normalizeSlots(prefs, allApps)
            if (prefs[KEY_INITIALIZED] == true) return@edit
            if (hasAnyOccupiedSlot(prefs)) {
                prefs[KEY_INITIALIZED] = true
                return@edit
            }
            allApps.take(SLOT_COUNT).forEachIndexed { index, app ->
                prefs[slotKey(index)] = app.componentName.flattenToString()
            }
            prefs[KEY_INITIALIZED] = true
        }
    }

    suspend fun setSlot(index: Int, component: ComponentName?) {
        require(index in 0 until SLOT_COUNT)
        dataStore.edit { prefs ->
            if (component == null) {
                prefs.remove(slotKey(index))
                return@edit
            }
            clearPackageFromOtherSlots(prefs, component.packageName, exceptIndex = index)
            prefs[slotKey(index)] = component.flattenToString()
            prefs[KEY_INITIALIZED] = true
        }
    }

    suspend fun assignToSlot(index: Int, app: LaunchableApp, availableApps: List<LaunchableApp>) {
        require(index in 0 until SLOT_COUNT)
        require(availableApps.any { it.packageName == app.packageName })
        dataStore.edit { prefs ->
            normalizeSlots(prefs, availableApps)
            clearPackageFromOtherSlots(prefs, app.packageName, exceptIndex = index)
            prefs[slotKey(index)] = app.componentName.flattenToString()
            prefs[KEY_INITIALIZED] = true
        }
    }

    suspend fun pruneMissingApps(availableApps: List<LaunchableApp>) {
        if (availableApps.isEmpty()) return
        dataStore.edit { prefs ->
            normalizeSlots(prefs, availableApps)
        }
    }

    suspend fun removePackage(packageName: String) {
        dataStore.edit { prefs ->
            for (index in 0 until SLOT_COUNT) {
                val component = unflattenComponent(prefs[slotKey(index)])
                if (component?.packageName == packageName) {
                    prefs.remove(slotKey(index))
                }
            }
        }
    }

    suspend fun clearSlots() {
        dataStore.edit { prefs ->
            for (index in 0 until SLOT_COUNT) {
                prefs.remove(slotKey(index))
            }
            prefs.remove(KEY_INITIALIZED)
        }
    }

    companion object {
        const val SLOT_COUNT = 5

        private val KEY_INITIALIZED = booleanPreferencesKey("home_screen_initialized")

        private fun slotKey(index: Int) = stringPreferencesKey("home_slot_$index")

        private fun readSlotsFromPrefs(prefs: androidx.datastore.preferences.core.Preferences): List<ComponentName?> =
            List(SLOT_COUNT) { index ->
                unflattenComponent(prefs[slotKey(index)])
            }

        private fun hasAnyOccupiedSlot(prefs: androidx.datastore.preferences.core.Preferences): Boolean =
            (0 until SLOT_COUNT).any { index -> prefs[slotKey(index)] != null }

        private fun unflattenComponent(flattened: String?): ComponentName? {
            if (flattened.isNullOrBlank()) return null
            return try {
                ComponentName.unflattenFromString(flattened)
            } catch (_: Exception) {
                null
            }
        }

        /**
         * Drops invalid entries, upgrades stale launcher components, and removes duplicate packages
         * (keeps the lowest slot index).
         */
        private fun normalizeSlots(
            prefs: androidx.datastore.preferences.core.MutablePreferences,
            availableApps: List<LaunchableApp>,
        ) {
            val canonicalByPackage = availableApps.associateBy { it.packageName }
            val seenPackages = mutableSetOf<String>()

            for (index in 0 until SLOT_COUNT) {
                val key = slotKey(index)
                val component = unflattenComponent(prefs[key])
                if (component == null) {
                    prefs.remove(key)
                    continue
                }
                val canonical = canonicalByPackage[component.packageName]
                if (canonical == null) {
                    prefs.remove(key)
                    continue
                }
                if (!seenPackages.add(component.packageName)) {
                    prefs.remove(key)
                    continue
                }
                val canonicalFlatten = canonical.componentName.flattenToString()
                if (component.flattenToString() != canonicalFlatten) {
                    prefs[key] = canonicalFlatten
                }
            }
        }

        private fun clearPackageFromOtherSlots(
            prefs: androidx.datastore.preferences.core.MutablePreferences,
            packageName: String,
            exceptIndex: Int,
        ) {
            for (index in 0 until SLOT_COUNT) {
                if (index == exceptIndex) continue
                val component = unflattenComponent(prefs[slotKey(index)]) ?: continue
                if (component.packageName == packageName) {
                    prefs.remove(slotKey(index))
                }
            }
        }

        fun resolveHomeApps(
            slots: List<ComponentName?>,
            allApps: List<LaunchableApp>,
        ): List<LaunchableApp> {
            return resolveSlotList(slots, allApps).filterNotNull()
        }

        fun resolveSlotList(
            slots: List<ComponentName?>,
            allApps: List<LaunchableApp>,
        ): List<LaunchableApp?> {
            val byComponent = allApps.associateBy { it.componentName.flattenToString() }
            val byPackage = allApps.associateBy { it.packageName }
            return List(SLOT_COUNT) { index ->
                val component = slots.getOrNull(index) ?: return@List null
                byComponent[component.flattenToString()]
                    ?: byPackage[component.packageName]
            }
        }

        fun formatSettingsSummary(apps: List<LaunchableApp>): String =
            if (apps.isEmpty()) {
                ""
            } else {
                apps.joinToString(separator = ", ") { it.label }
            }
    }
}
