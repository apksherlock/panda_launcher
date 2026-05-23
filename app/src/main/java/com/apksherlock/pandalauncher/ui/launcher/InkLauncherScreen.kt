package com.apksherlock.pandalauncher.ui.launcher

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.apksherlock.pandalauncher.MainActivity
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.requireAppearanceStore
import com.apksherlock.pandalauncher.requireWallpaperStore
import com.apksherlock.pandalauncher.data.LaunchableAppCatalog
import com.apksherlock.pandalauncher.ui.theme.coerceSchemeId
import com.apksherlock.pandalauncher.ui.theme.defaultSchemeId
import com.apksherlock.pandalauncher.ui.theme.resolveInkPalette
import com.apksherlock.pandalauncher.wallpaper.InkSystemWallpaper
import com.apksherlock.pandalauncher.ui.theme.inkSchemeLabel
import com.apksherlock.pandalauncher.data.FavoriteAppStore
import com.apksherlock.pandalauncher.data.HomeScreenAppStore
import com.apksherlock.pandalauncher.data.LauncherProfileStore
import com.apksherlock.pandalauncher.data.ShortcutRepository
import com.apksherlock.pandalauncher.favorite.FavoriteLauncher
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.model.PinnedShortcut
import com.apksherlock.pandalauncher.navigation.LauncherActivityTransitions
import com.apksherlock.pandalauncher.ui.apps.InkHomeSlotAssignDialog
import com.apksherlock.pandalauncher.ui.components.InkDisplayNameDialog
import com.apksherlock.pandalauncher.launcher.LauncherHomeRole
import com.apksherlock.pandalauncher.ui.onboarding.InkOnboardingFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun InkLauncherScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val appCatalog = remember(context) { LaunchableAppCatalog(context) }
    val favoriteStore = remember(context) { FavoriteAppStore(context) }
    val homeScreenStore = remember(context) { HomeScreenAppStore(context) }
    val profileStore = remember(context) { LauncherProfileStore(context) }
    val shortcutRepository = remember(context) { ShortcutRepository(context) }
    val appearanceStore = remember { context.requireAppearanceStore() }
    val wallpaperStore = remember { context.requireWallpaperStore() }
    val swipeThresholdPx = with(density) { 72.dp.toPx() }
    val favoriteComponent by favoriteStore.favoriteComponent.collectAsStateWithLifecycle(null)
    val slotComponents by homeScreenStore.slotComponents.collectAsStateWithLifecycle(
        List(HomeScreenAppStore.SLOT_COUNT) { null },
    )
    val storedDisplayName by profileStore.displayName.collectAsStateWithLifecycle(null)
    val hasCompletedWallpaperSetup by wallpaperStore.hasCompletedWallpaperSetup
        .collectAsStateWithLifecycle(false)
    val hasCompletedAppearanceSetup by appearanceStore.hasCompletedAppearanceSetup
        .collectAsStateWithLifecycle(false)
    val hasCompletedNameSetup by profileStore.hasCompletedNameSetup.collectAsStateWithLifecycle(false)
    var isDefaultLauncher by remember { mutableStateOf(LauncherHomeRole.isDefaultHomeLauncher(context)) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val allApps by appCatalog.apps.collectAsStateWithLifecycle()
    var shortcuts by remember { mutableStateOf<List<PinnedShortcut>>(emptyList()) }
    var shortcutRevision by remember { mutableIntStateOf(0) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var menuApp by remember { mutableStateOf<LaunchableApp?>(null) }
    var appPendingHomeSlot by remember { mutableStateOf<LaunchableApp?>(null) }
    val needsOnboarding = !isDefaultLauncher || !hasCompletedWallpaperSetup ||
        !hasCompletedAppearanceSetup || !hasCompletedNameSetup
    val settingsDisplayNameLine = remember(storedDisplayName) {
        LauncherProfileStore.formatForHeader(storedDisplayName)
    }

    DisposableEffect(lifecycleOwner, appCatalog) {
        appCatalog.start(lifecycleOwner.lifecycle)
        onDispose { appCatalog.stop(lifecycleOwner.lifecycle) }
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isDefaultLauncher = LauncherHomeRole.isDefaultHomeLauncher(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(allApps) {
        if (allApps.isEmpty()) return@LaunchedEffect
        homeScreenStore.pruneMissingApps(allApps)
        withContext(Dispatchers.Default) {
            homeScreenStore.ensureInitialized(allApps)
        }
    }

    val favoriteApp = remember(favoriteComponent, allApps) {
        FavoriteAppStore.resolveFavorite(favoriteComponent, allApps)
    }
    val settingsFavoriteLine = favoriteApp?.label
        ?: stringResource(R.string.settings_favorite_not_set)
    val homeSlotApps = remember(slotComponents, allApps) {
        HomeScreenAppStore.resolveSlotList(slotComponents, allApps)
    }
    val homeAppsSummary = remember(homeSlotApps) {
        HomeScreenAppStore.formatSettingsSummary(homeSlotApps.filterNotNull())
    }
    val settingsHomeAppsLine = homeAppsSummary.ifBlank {
        stringResource(R.string.settings_home_apps_empty)
    }
    val darkTheme = isSystemInDarkTheme()
    val schemeFlow = remember(darkTheme) { appearanceStore.colorSchemeId(darkTheme) }
    val colorSchemeId by schemeFlow.collectAsStateWithLifecycle(defaultSchemeId(darkTheme))
    val settingsAppearanceLine = remember(colorSchemeId) {
        inkSchemeLabel(colorSchemeId)
    }
    val ribbonEnabled by wallpaperStore.ribbonEnabled().collectAsStateWithLifecycle(false)
    val settingsWallpaperLine = if (ribbonEnabled) {
        stringResource(R.string.settings_wallpaper_ribbon_on)
    } else {
        stringResource(R.string.settings_wallpaper_ribbon_off)
    }
    val menuAppCanUninstall = menuApp?.let { appCatalog.canUninstall(it) } == true

    LaunchedEffect(context, hasCompletedWallpaperSetup, colorSchemeId, darkTheme) {
        if (!hasCompletedWallpaperSetup) return@LaunchedEffect
        val palette = resolveInkPalette(colorSchemeId, darkTheme)
        withContext(Dispatchers.IO) {
            InkSystemWallpaper.apply(context, palette)
        }
    }

    LaunchedEffect(currentRoute, shortcutRevision, shortcutRepository) {
        if (currentRoute == LauncherRoute.Shortcuts) {
            shortcuts = withContext(Dispatchers.Default) {
                shortcutRepository.loadPinnedShortcuts()
            }
        }
    }

    DisposableEffect(shortcutRepository) {
        val unregister = shortcutRepository.registerOnShortcutsChanged {
            shortcutRevision++
        }
        onDispose { unregister() }
    }

    val canNavigateBack = navController.previousBackStackEntry != null
    BackHandler(enabled = canNavigateBack && !needsOnboarding) {
        navController.navigateUp()
    }

    val activity = context as? ComponentActivity

    LaunchedEffect(currentRoute) {
        if (currentRoute != LauncherRoute.AllApps) {
            menuApp = null
        }
    }

    LaunchedEffect(activity) {
        val main = activity as? MainActivity ?: return@LaunchedEffect
        main.goHomeRequests.collect {
            menuApp = null
            appPendingHomeSlot = null
            showEditNameDialog = false
            if (navController.currentDestination?.route != LauncherRoute.Home) {
                navController.popBackStack(LauncherRoute.Home, inclusive = false)
            }
        }
    }

    val homeModifier = if (currentRoute == LauncherRoute.Home && !needsOnboarding) {
        Modifier.detectHomeSwipes(
            thresholdPx = swipeThresholdPx,
            onSwipeUp = { navigateToOverlay(navController, LauncherRoute.AllApps) },
            onSwipeDown = { navigateToOverlay(navController, LauncherRoute.Settings) },
            onSwipeLeft = { navigateToOverlay(navController, LauncherRoute.Shortcuts) },
            onSwipeRight = {
                activity?.let { act ->
                    val favorite = favoriteApp
                    if (favorite != null) {
                        FavoriteLauncher.launchGuest(act, favorite)
                    } else {
                        LauncherActivityTransitions.openFavoritePicker(act)
                    }
                }
            },
        )
    } else {
        Modifier
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (needsOnboarding) {
            InkOnboardingFlow(modifier = Modifier.fillMaxSize())
        } else {
        InkLauncherNavHost(
            navController = navController,
            allApps = allApps,
            homeSlotApps = homeSlotApps,
            shortcuts = shortcuts,
            settingsDisplayNameLine = settingsDisplayNameLine,
            settingsFavoriteLine = settingsFavoriteLine,
            settingsHomeAppsLine = settingsHomeAppsLine,
            settingsAppearanceLine = settingsAppearanceLine,
            settingsWallpaperLine = settingsWallpaperLine,
            appearanceSchemeId = colorSchemeId,
            swipeThresholdPx = swipeThresholdPx,
            onAppLaunch = { app ->
                keyboardController?.hide()
                appCatalog.launch(app)
            },
            onShortcutLaunch = { shortcut ->
                keyboardController?.hide()
                shortcutRepository.launch(shortcut)
            },
            onEditDisplayName = { showEditNameDialog = true },
            onFavoriteAppSelected = { app ->
                scope.launch { favoriteStore.setFavorite(app) }
            },
            onEditHomeApps = {
                navController.navigate(LauncherRoute.SettingsHomeApps)
            },
            onEditAppearance = {
                navController.navigate(LauncherRoute.SettingsAppearance)
            },
            onEditWallpaper = {
                navController.navigate(LauncherRoute.SettingsWallpaper)
            },
            onAppearanceSchemeSelected = { id ->
                scope.launch { appearanceStore.setColorScheme(darkTheme, id) }
            },
            onWallpaperRibbonChanged = { enabled ->
                scope.launch { wallpaperStore.setRibbonEnabled(enabled) }
            },
            onHomeSlotAppSelected = { index, app ->
                scope.launch { homeScreenStore.setSlot(index, app.componentName) }
            },
            onHomeSlotCleared = { index ->
                scope.launch { homeScreenStore.setSlot(index, null) }
            },
            menuApp = menuApp,
            canUninstallMenuApp = menuAppCanUninstall,
            onAppLongClick = { menuApp = it },
            onAddMenuAppToHome = {
                val app = menuApp ?: return@InkLauncherNavHost
                menuApp = null
                appPendingHomeSlot = app
            },
            onUninstallMenuApp = {
                val app = menuApp ?: return@InkLauncherNavHost
                menuApp = null
                scope.launch { homeScreenStore.removePackage(app.packageName) }
                val host = activity ?: context
                appCatalog.startUninstall(host, app)
            },
            onDismissAppMenu = { menuApp = null },
            onOpenNotifications = {
                navigateToOverlay(navController, LauncherRoute.Notifications)
            },
            homeModifier = homeModifier,
        )
        }

        appPendingHomeSlot?.let { app ->
            BackHandler { appPendingHomeSlot = null }
            InkHomeSlotAssignDialog(
                app = app,
                slotApps = homeSlotApps,
                onSlotSelected = { index ->
                    scope.launch {
                        homeScreenStore.assignToSlot(index, app, allApps)
                        appPendingHomeSlot = null
                    }
                },
                onDismiss = { appPendingHomeSlot = null },
            )
        }

        if (showEditNameDialog) {
            InkDisplayNameDialog(
                title = stringResource(R.string.onboarding_name_title),
                initialValue = storedDisplayName.orEmpty(),
                onDismiss = { showEditNameDialog = false },
                onConfirm = { name ->
                    scope.launch {
                        profileStore.setDisplayName(name)
                    }
                    showEditNameDialog = false
                },
            )
        }
    }
}

private fun navigateToOverlay(
    navController: androidx.navigation.NavHostController,
    route: String,
) {
    navController.navigate(route) {
        popUpTo(LauncherRoute.Home) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun Modifier.detectHomeSwipes(
    thresholdPx: Float,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
): Modifier = pointerInput(thresholdPx) {
    var totalX = 0f
    var totalY = 0f
    detectDragGestures(
        onDragStart = {
            totalX = 0f
            totalY = 0f
        },
        onDragEnd = {
            if (abs(totalY) > abs(totalX)) {
                when {
                    totalY <= -thresholdPx -> onSwipeUp()
                    totalY >= thresholdPx -> onSwipeDown()
                }
            } else if (totalX <= -thresholdPx) {
                onSwipeLeft()
            } else if (totalX >= thresholdPx) {
                onSwipeRight()
            }
            totalX = 0f
            totalY = 0f
        },
        onDrag = { change, dragAmount ->
            change.consume()
            totalX += dragAmount.x
            totalY += dragAmount.y
        },
    )
}
