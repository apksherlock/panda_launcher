package com.apksherlock.pandalauncher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

internal val Context.launcherPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "launcher_preferences",
)
