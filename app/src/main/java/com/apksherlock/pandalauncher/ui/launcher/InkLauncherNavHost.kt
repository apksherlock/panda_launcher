package com.apksherlock.pandalauncher.ui.launcher

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apksherlock.pandalauncher.data.NotificationRepository
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.model.PinnedShortcut
import com.apksherlock.pandalauncher.ui.apps.InkAllAppsScreen
import com.apksherlock.pandalauncher.ui.favorite.InkFavoritePickerWithConfirmation
import com.apksherlock.pandalauncher.ui.home.InkHomeScreen
import com.apksherlock.pandalauncher.ui.launcher.LauncherNavTransitions.enterFromBottom
import com.apksherlock.pandalauncher.ui.launcher.LauncherNavTransitions.enterFromEnd
import com.apksherlock.pandalauncher.ui.launcher.LauncherNavTransitions.enterFromTop
import com.apksherlock.pandalauncher.ui.launcher.LauncherNavTransitions.popExitToBottom
import com.apksherlock.pandalauncher.ui.launcher.LauncherNavTransitions.popExitToEnd
import com.apksherlock.pandalauncher.ui.launcher.LauncherNavTransitions.popExitToTop
import com.apksherlock.pandalauncher.ui.settings.InkHomeAppsEditorScreen
import com.apksherlock.pandalauncher.ui.settings.InkHomeSlotPickerScreen
import com.apksherlock.pandalauncher.ui.settings.InkSettingsAppearanceScreen
import com.apksherlock.pandalauncher.ui.settings.InkSettingsScreen
import com.apksherlock.pandalauncher.ui.settings.InkSettingsWallpaperScreen
import com.apksherlock.pandalauncher.ui.notifications.InkNotificationsScreen
import com.apksherlock.pandalauncher.ui.shortcuts.InkShortcutsScreen
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.inkSurface

@Composable
fun InkLauncherNavHost(
    navController: NavHostController,
    allApps: List<LaunchableApp>,
    homeSlotApps: List<LaunchableApp?>,
    shortcuts: List<PinnedShortcut>,
    settingsDisplayNameLine: String,
    settingsFavoriteLine: String,
    settingsHomeAppsLine: String,
    settingsAppearanceLine: String,
    settingsWallpaperLine: String,
    appearanceSchemeId: String,
    swipeThresholdPx: Float,
    onAppLaunch: (LaunchableApp) -> Unit,
    onShortcutLaunch: (PinnedShortcut) -> Unit,
    onEditDisplayName: () -> Unit,
    onFavoriteAppSelected: (LaunchableApp) -> Unit,
    onEditHomeApps: () -> Unit,
    onEditAppearance: () -> Unit,
    onEditWallpaper: () -> Unit,
    onAppearanceSchemeSelected: (String) -> Unit,
    onWallpaperRibbonChanged: (Boolean) -> Unit,
    onHomeSlotAppSelected: (Int, LaunchableApp) -> Unit,
    onHomeSlotCleared: (Int) -> Unit,
    menuApp: LaunchableApp?,
    canUninstallMenuApp: Boolean,
    onAppLongClick: (LaunchableApp) -> Unit,
    onAddMenuAppToHome: () -> Unit,
    onUninstallMenuApp: () -> Unit,
    onDismissAppMenu: () -> Unit,
    onOpenNotifications: () -> Unit,
    homeModifier: Modifier,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        NavHost(
            navController = navController,
            startDestination = LauncherRoute.Home,
            modifier = Modifier.fillMaxSize(),
        ) {
            homeDestination(
                homeModifier = homeModifier,
                onOpenNotifications = onOpenNotifications,
            )
            notificationsDestination(
                swipeThresholdPx = swipeThresholdPx,
                onNavigateBack = { navController.navigateUp() },
            )
            allAppsDestination(
                apps = allApps,
                swipeThresholdPx = swipeThresholdPx,
                onNavigateBack = {
                    if (menuApp == null) {
                        navController.navigateUp()
                    }
                },
                onAppLaunch = onAppLaunch,
                menuApp = menuApp,
                canUninstallMenuApp = canUninstallMenuApp,
                onAppLongClick = onAppLongClick,
                onAddMenuAppToHome = onAddMenuAppToHome,
                onUninstallMenuApp = onUninstallMenuApp,
                onDismissAppMenu = onDismissAppMenu,
            )
            shortcutsDestination(
                shortcuts = shortcuts,
                swipeThresholdPx = swipeThresholdPx,
                onNavigateBack = { navController.navigateUp() },
                onShortcutLaunch = onShortcutLaunch,
            )
            settingsDestination(
                displayNameLine = settingsDisplayNameLine,
                favoriteAppLine = settingsFavoriteLine,
                homeAppsLine = settingsHomeAppsLine,
                appearanceLine = settingsAppearanceLine,
                wallpaperLine = settingsWallpaperLine,
                swipeThresholdPx = swipeThresholdPx,
                onNavigateBack = { navController.navigateUp() },
                onEditDisplayName = onEditDisplayName,
                onOpenFavoritePicker = {
                    navController.navigate(LauncherRoute.SettingsFavoritePicker)
                },
                onEditHomeApps = onEditHomeApps,
                onEditAppearance = onEditAppearance,
                onEditWallpaper = onEditWallpaper,
            )
            settingsAppearanceDestination(
                selectedSchemeId = appearanceSchemeId,
                swipeThresholdPx = swipeThresholdPx,
                onNavigateBack = { navController.navigateUp() },
                onSchemeSelected = onAppearanceSchemeSelected,
            )
            settingsWallpaperDestination(
                appearanceSchemeId = appearanceSchemeId,
                swipeThresholdPx = swipeThresholdPx,
                onNavigateBack = { navController.navigateUp() },
                onRibbonChanged = onWallpaperRibbonChanged,
            )
            settingsFavoritePickerDestination(
                apps = allApps,
                swipeThresholdPx = swipeThresholdPx,
                onNavigateBack = { navController.navigateUp() },
                onAppSelected = { app ->
                    onFavoriteAppSelected(app)
                    navController.popBackStack(LauncherRoute.Settings, inclusive = false)
                },
            )
            settingsHomeAppsDestination(
                slotApps = homeSlotApps,
                swipeThresholdPx = swipeThresholdPx,
                onNavigateBack = { navController.navigateUp() },
                onEditSlot = { index ->
                    navController.navigate(LauncherRoute.settingsHomeSlotPicker(index))
                },
            )
            settingsHomeSlotPickerDestination(
                apps = allApps,
                swipeThresholdPx = swipeThresholdPx,
                onNavigateBack = { navController.navigateUp() },
                onAppSelected = { slotIndex, app ->
                    onHomeSlotAppSelected(slotIndex, app)
                    navController.popBackStack(LauncherRoute.SettingsHomeApps, inclusive = false)
                },
                onClearSlot = { slotIndex ->
                    onHomeSlotCleared(slotIndex)
                    navController.popBackStack(LauncherRoute.SettingsHomeApps, inclusive = false)
                },
            )
        }
    }
}

private fun NavGraphBuilder.homeDestination(
    homeModifier: Modifier,
    onOpenNotifications: () -> Unit,
) {
    composable(route = LauncherRoute.Home) {
        InkHomeScreen(
            onOpenNotifications = onOpenNotifications,
            modifier = homeModifier.fillMaxSize(),
        )
    }
}

private fun NavGraphBuilder.notificationsDestination(
    swipeThresholdPx: Float,
    onNavigateBack: () -> Unit,
) {
    composable(
        route = LauncherRoute.Notifications,
        enterTransition = { enterFromEnd() },
        popExitTransition = { popExitToEnd() },
    ) {
        val context = LocalContext.current
        val notificationRepository = remember(context) { NotificationRepository(context) }
        val notifications by notificationRepository.notifications.collectAsStateWithLifecycle()
        var hasAccess by remember {
            mutableStateOf(notificationRepository.isAccessEnabled())
        }
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner, notificationRepository) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    hasAccess = notificationRepository.isAccessEnabled()
                    notificationRepository.rebindIfNeeded()
                    notificationRepository.refresh()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        InkNotificationsScreen(
            notifications = notifications,
            hasListenerAccess = hasAccess,
            onNotificationClick = { notificationRepository.launchNotification(it) },
            onDismissNotification = { notificationRepository.dismissNotification(it) },
            onRequestAccess = { notificationRepository.openNotificationAccessSettings() },
            onRefresh = { notificationRepository.refresh() },
            onNavigateBack = onNavigateBack,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun NavGraphBuilder.allAppsDestination(
    apps: List<LaunchableApp>,
    swipeThresholdPx: Float,
    onNavigateBack: () -> Unit,
    onAppLaunch: (LaunchableApp) -> Unit,
    menuApp: LaunchableApp?,
    canUninstallMenuApp: Boolean,
    onAppLongClick: (LaunchableApp) -> Unit,
    onAddMenuAppToHome: () -> Unit,
    onUninstallMenuApp: () -> Unit,
    onDismissAppMenu: () -> Unit,
) {
    composable(
        route = LauncherRoute.AllApps,
        enterTransition = { enterFromBottom() },
        popExitTransition = { popExitToBottom() },
    ) {
        InkAllAppsScreen(
            apps = apps,
            onNavigateBack = onNavigateBack,
            onAppClick = onAppLaunch,
            onAppLongClick = onAppLongClick,
            menuApp = menuApp,
            canUninstallMenuApp = canUninstallMenuApp,
            onAddMenuAppToHome = onAddMenuAppToHome,
            onUninstallMenuApp = onUninstallMenuApp,
            onDismissAppMenu = onDismissAppMenu,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier
                .fillMaxSize()
                .inkSurface(color = InkThemeAccessor.palette.canvas),
        )
    }
}

private fun NavGraphBuilder.shortcutsDestination(
    shortcuts: List<PinnedShortcut>,
    swipeThresholdPx: Float,
    onNavigateBack: () -> Unit,
    onShortcutLaunch: (PinnedShortcut) -> Unit,
) {
    composable(
        route = LauncherRoute.Shortcuts,
        enterTransition = { enterFromEnd() },
        popExitTransition = { popExitToEnd() },
    ) {
        InkShortcutsScreen(
            shortcuts = shortcuts,
            onNavigateBack = onNavigateBack,
            onShortcutClick = onShortcutLaunch,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier
                .fillMaxSize()
                .inkSurface(color = InkThemeAccessor.palette.canvas),
        )
    }
}

private fun NavGraphBuilder.settingsAppearanceDestination(
    selectedSchemeId: String,
    swipeThresholdPx: Float,
    onNavigateBack: () -> Unit,
    onSchemeSelected: (String) -> Unit,
) {
    composable(
        route = LauncherRoute.SettingsAppearance,
        enterTransition = { enterFromEnd() },
        popExitTransition = { popExitToEnd() },
    ) {
        InkSettingsAppearanceScreen(
            selectedSchemeId = selectedSchemeId,
            onSchemeSelected = onSchemeSelected,
            onNavigateBack = onNavigateBack,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun NavGraphBuilder.settingsWallpaperDestination(
    appearanceSchemeId: String,
    swipeThresholdPx: Float,
    onNavigateBack: () -> Unit,
    onRibbonChanged: (Boolean) -> Unit,
) {
    composable(
        route = LauncherRoute.SettingsWallpaper,
        enterTransition = { enterFromEnd() },
        popExitTransition = { popExitToEnd() },
    ) {
        InkSettingsWallpaperScreen(
            appearanceSchemeId = appearanceSchemeId,
            onRibbonChanged = onRibbonChanged,
            onNavigateBack = onNavigateBack,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun NavGraphBuilder.settingsDestination(
    displayNameLine: String,
    favoriteAppLine: String,
    homeAppsLine: String,
    appearanceLine: String,
    wallpaperLine: String,
    swipeThresholdPx: Float,
    onNavigateBack: () -> Unit,
    onEditDisplayName: () -> Unit,
    onOpenFavoritePicker: () -> Unit,
    onEditHomeApps: () -> Unit,
    onEditAppearance: () -> Unit,
    onEditWallpaper: () -> Unit,
) {
    composable(
        route = LauncherRoute.Settings,
        enterTransition = { enterFromTop() },
        popExitTransition = { popExitToTop() },
    ) {
        InkSettingsScreen(
            displayNameLine = displayNameLine,
            favoriteAppLine = favoriteAppLine,
            homeAppsLine = homeAppsLine,
            appearanceLine = appearanceLine,
            wallpaperLine = wallpaperLine,
            onChangeDisplayName = onEditDisplayName,
            onChangeFavoriteApp = onOpenFavoritePicker,
            onEditHomeApps = onEditHomeApps,
            onEditAppearance = onEditAppearance,
            onEditWallpaper = onEditWallpaper,
            onNavigateBack = onNavigateBack,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun NavGraphBuilder.settingsFavoritePickerDestination(
    apps: List<LaunchableApp>,
    swipeThresholdPx: Float,
    onNavigateBack: () -> Unit,
    onAppSelected: (LaunchableApp) -> Unit,
) {
    composable(
        route = LauncherRoute.SettingsFavoritePicker,
        enterTransition = { enterFromEnd() },
        popExitTransition = { popExitToEnd() },
    ) {
        InkFavoritePickerWithConfirmation(
            apps = apps,
            onNavigateBack = onNavigateBack,
            onConfirmed = onAppSelected,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun NavGraphBuilder.settingsHomeAppsDestination(
    slotApps: List<LaunchableApp?>,
    swipeThresholdPx: Float,
    onNavigateBack: () -> Unit,
    onEditSlot: (Int) -> Unit,
) {
    composable(
        route = LauncherRoute.SettingsHomeApps,
        enterTransition = { enterFromEnd() },
        popExitTransition = { popExitToEnd() },
    ) {
        InkHomeAppsEditorScreen(
            slotApps = slotApps,
            onEditSlot = onEditSlot,
            onNavigateBack = onNavigateBack,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun NavGraphBuilder.settingsHomeSlotPickerDestination(
    apps: List<LaunchableApp>,
    swipeThresholdPx: Float,
    onNavigateBack: () -> Unit,
    onAppSelected: (Int, LaunchableApp) -> Unit,
    onClearSlot: (Int) -> Unit,
) {
    composable(
        route = LauncherRoute.SettingsHomeSlotPicker,
        arguments = listOf(
            navArgument("slotIndex") { type = NavType.IntType },
        ),
        enterTransition = { enterFromEnd() },
        popExitTransition = { popExitToEnd() },
    ) { backStackEntry ->
        val slotIndex = backStackEntry.arguments?.getInt("slotIndex") ?: 0
        InkHomeSlotPickerScreen(
            slotIndex = slotIndex,
            apps = apps,
            onAppSelected = { app -> onAppSelected(slotIndex, app) },
            onClearSlot = { onClearSlot(slotIndex) },
            onNavigateBack = onNavigateBack,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
