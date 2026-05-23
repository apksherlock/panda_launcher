package com.apksherlock.pandalauncher.ui.onboarding

import android.content.ComponentName
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.launcher.LauncherHomeRole
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.requireAppearanceStore
import com.apksherlock.pandalauncher.requireWallpaperStore
import com.apksherlock.pandalauncher.ui.components.InkSearchField
import com.apksherlock.pandalauncher.ui.settings.AppearancePreviewThemeHost
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkColorScheme
import com.apksherlock.pandalauncher.ui.theme.InkShape
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.coerceSchemeId
import com.apksherlock.pandalauncher.ui.theme.defaultSchemeId
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import com.apksherlock.pandalauncher.ui.theme.familyIdForSchemeId
import com.apksherlock.pandalauncher.ui.theme.inkSchemesFor
import com.apksherlock.pandalauncher.ui.theme.schemeFamilyMatches
import com.apksherlock.pandalauncher.ui.theme.resolveInkPalette
import com.apksherlock.pandalauncher.ui.wallpaper.InkRibbonToggleRow
import com.apksherlock.pandalauncher.ui.wallpaper.InkWallpaperStylePreview
import com.apksherlock.pandalauncher.wallpaper.WALLPAPER_NONE_ID
import com.apksherlock.pandalauncher.wallpaper.WALLPAPER_RIBBON_ID
import com.apksherlock.pandalauncher.data.LauncherProfileStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class OnboardingStep {
    DefaultLauncher,
    Wallpaper,
    Appearance,
    Name,
}

@Composable
fun InkOnboardingFlow(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val darkTheme = isSystemInDarkTheme()
    val wallpaperStore = remember { context.requireWallpaperStore() }
    val appearanceStore = remember { context.requireAppearanceStore() }
    val profileStore = remember { com.apksherlock.pandalauncher.data.LauncherProfileStore(context) }
    val hasWallpaperSetup by wallpaperStore.hasCompletedWallpaperSetup.collectAsStateWithLifecycle(false)
    val hasAppearanceSetup by appearanceStore.hasCompletedAppearanceSetup.collectAsStateWithLifecycle(false)
    val hasNameSetup by profileStore.hasCompletedNameSetup.collectAsStateWithLifecycle(false)

    var isDefaultLauncher by remember { mutableStateOf(LauncherHomeRole.isDefaultHomeLauncher(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isDefaultLauncher = LauncherHomeRole.isDefaultHomeLauncher(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val step = when {
        !isDefaultLauncher -> OnboardingStep.DefaultLauncher
        !hasWallpaperSetup -> OnboardingStep.Wallpaper
        !hasAppearanceSetup -> OnboardingStep.Appearance
        !hasNameSetup -> OnboardingStep.Name
        else -> null
    }

    if (step == null) return

    val roleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) {
        isDefaultLauncher = LauncherHomeRole.isDefaultHomeLauncher(context)
    }

    when (step) {
        OnboardingStep.DefaultLauncher -> {
            InkOnboardingShell(
                stepLabel = stringResource(R.string.onboarding_step, 1, 4),
                title = stringResource(R.string.onboarding_default_title),
                subtitle = stringResource(R.string.onboarding_default_subtitle),
                continueLabel = stringResource(R.string.onboarding_open_settings),
                continueEnabled = true,
                onContinue = { roleLauncher.launch(LauncherHomeRole.createDefaultHomeIntent(context)) },
                modifier = modifier,
            ) {
                InkText(
                    text = if (isDefaultLauncher) {
                        stringResource(R.string.onboarding_default_done)
                    } else {
                        stringResource(R.string.onboarding_default_pending)
                    },
                    style = InkThemeAccessor.text.notificationEmpty,
                )
            }
        }

        OnboardingStep.Wallpaper -> OnboardingWallpaperStep(
            stepLabel = stringResource(R.string.onboarding_step, 2, 4),
            darkTheme = darkTheme,
            modifier = modifier,
            onConfirm = { ribbonEnabled ->
                scope.launch {
                    wallpaperStore.completeOnboardingSetup(ribbonEnabled)
                }
            },
        )

        OnboardingStep.Appearance -> OnboardingAppearanceStep(
            stepLabel = stringResource(R.string.onboarding_step, 3, 4),
            darkTheme = darkTheme,
            modifier = modifier,
            onConfirm = { schemeId ->
                scope.launch {
                    appearanceStore.completeOnboardingSetup(schemeId)
                }
            },
        )

        OnboardingStep.Name -> OnboardingNameStep(
            stepLabel = stringResource(R.string.onboarding_step, 4, 4),
            modifier = modifier,
            onConfirm = { name ->
                scope.launch {
                    profileStore.setDisplayName(name)
                }
            },
        )
    }
}

@Composable
private fun OnboardingWallpaperStep(
    stepLabel: String,
    darkTheme: Boolean,
    onConfirm: (ribbonEnabled: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val previewPalette = remember(darkTheme) {
        resolveInkPalette(defaultSchemeId(darkTheme), darkTheme)
    }
    var ribbonOn by remember { mutableStateOf(false) }
    val previewStyleId = if (ribbonOn) WALLPAPER_RIBBON_ID else WALLPAPER_NONE_ID

    InkOnboardingShell(
        stepLabel = stepLabel,
        title = stringResource(R.string.onboarding_wallpaper_title),
        subtitle = stringResource(R.string.onboarding_wallpaper_subtitle),
        continueLabel = stringResource(R.string.onboarding_continue),
        continueEnabled = true,
        onContinue = { onConfirm(ribbonOn) },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(InkShape.corners)
                .border(1.dp, InkThemeAccessor.palette.ink.copy(alpha = 0.2f), InkShape.corners),
        ) {
            InkWallpaperStylePreview(
                styleId = previewStyleId,
                palette = previewPalette,
                modifier = Modifier.fillMaxSize(),
            )
        }
        InkRibbonToggleRow(
            enabled = ribbonOn,
            onToggle = { ribbonOn = it },
            palette = previewPalette,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun OnboardingAppearanceStep(
    stepLabel: String,
    darkTheme: Boolean,
    onConfirm: (schemeId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val schemes = remember(darkTheme) { inkSchemesFor(darkTheme) }
    var schemeId by remember { mutableStateOf(defaultSchemeId(darkTheme)) }
    val selectedFamilyId = remember(schemeId) { familyIdForSchemeId(schemeId) }
    val previewPalette = remember(schemeId, darkTheme) {
        resolveInkPalette(schemeId, darkTheme)
    }
    val previewIcon = remember(context) {
        context.packageManager.getApplicationIcon(context.applicationInfo)
    }
    val mockLabel = stringResource(R.string.settings_appearance_mock_app_label)
    val mockApp = remember(previewIcon, context.packageName, mockLabel) {
        LaunchableApp(
            packageName = context.packageName,
            label = mockLabel,
            componentName = ComponentName(context.packageName, "${context.packageName}.MainActivity"),
            icon = previewIcon,
        )
    }

    InkOnboardingShell(
        stepLabel = stepLabel,
        title = stringResource(R.string.onboarding_appearance_title),
        subtitle = stringResource(R.string.onboarding_appearance_subtitle),
        continueLabel = stringResource(R.string.onboarding_continue),
        continueEnabled = true,
        onContinue = { onConfirm(schemeId) },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            AppearancePreviewThemeHost(previewPalette) {
                com.apksherlock.pandalauncher.ui.apps.InkAppGridCell(
                    app = mockApp,
                    onClick = {},
                    onLongClick = {},
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            InkText(
                text = stringResource(R.string.settings_appearance_schemes),
                style = InkThemeAccessor.text.dateCaps,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 8.dp),
            ) {
                items(schemes, key = { it.id }) { scheme ->
                    OnboardingSchemeChip(
                        scheme = scheme,
                        selected = schemeFamilyMatches(scheme.id, selectedFamilyId),
                        onClick = { schemeId = scheme.id },
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingSchemeChip(
    scheme: InkColorScheme,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val border = if (selected) 2.dp else 1.dp
    val borderColor = if (selected) scheme.accent else scheme.ink.copy(alpha = 0.25f)
    Column(
        modifier = Modifier
            .border(border, borderColor, InkShape.corners)
            .inkClickable(onClick = onClick, contentPadding = InkClickMetrics.none)
            .padding(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(InkShape.corners)
                .border(1.dp, scheme.ink.copy(alpha = 0.2f), InkShape.corners),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(InkShape.corners),
            ) {
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                    drawRect(scheme.canvas)
                    drawRect(scheme.backplate, topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.5f, 0f))
                    drawRect(
                        scheme.accent,
                        topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.55f, size.height * 0.55f),
                        size = androidx.compose.ui.geometry.Size(size.width * 0.35f, size.height * 0.35f),
                    )
                }
            }
        }
        InkText(text = scheme.label, style = InkThemeAccessor.text.notificationEmpty, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun OnboardingNameStep(
    stepLabel: String,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }

    InkOnboardingShell(
        stepLabel = stepLabel,
        title = stringResource(R.string.onboarding_name_title),
        subtitle = stringResource(R.string.onboarding_name_subtitle),
        continueLabel = stringResource(R.string.onboarding_finish),
        continueEnabled = true,
        onContinue = { onConfirm(name) },
        modifier = modifier,
    ) {
        InkSearchField(
            value = name,
            onValueChange = { name = it },
            placeholder = stringResource(R.string.display_name_hint),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
