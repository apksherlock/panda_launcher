package com.apksherlock.pandalauncher.ui.launcher

object LauncherRoute {
    const val Home = "home"
    const val AllApps = "all_apps"
    const val Shortcuts = "shortcuts"
    const val Notifications = "notifications"
    const val Settings = "settings"
    const val SettingsAppearance = "settings_appearance"
    const val SettingsWallpaper = "settings_wallpaper"
    const val SettingsFavoritePicker = "settings_favorite_picker"
    const val SettingsHomeApps = "settings_home_apps"
    const val SettingsHomeSlotPicker = "settings_home_slot_picker/{slotIndex}"

    fun settingsHomeSlotPicker(slotIndex: Int): String =
        "settings_home_slot_picker/$slotIndex"
}
