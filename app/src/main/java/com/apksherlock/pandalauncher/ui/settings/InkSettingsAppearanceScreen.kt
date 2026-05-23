package com.apksherlock.pandalauncher.ui.settings

import android.content.ComponentName
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.model.LaunchableApp
import com.apksherlock.pandalauncher.ui.apps.InkAppGridCell
import com.apksherlock.pandalauncher.ui.components.InkDivider
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.home.InkAppRow
import com.apksherlock.pandalauncher.ui.theme.InkColorScheme
import com.apksherlock.pandalauncher.ui.theme.InkPalette
import com.apksherlock.pandalauncher.ui.theme.InkShape
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor
import com.apksherlock.pandalauncher.ui.theme.LocalInkPalette
import com.apksherlock.pandalauncher.ui.theme.LocalInkTextStyles
import com.apksherlock.pandalauncher.ui.theme.InkClickMetrics
import com.apksherlock.pandalauncher.ui.theme.inkClickable
import com.apksherlock.pandalauncher.ui.theme.inkFontFamilies
import com.apksherlock.pandalauncher.ui.theme.familyIdForSchemeId
import com.apksherlock.pandalauncher.ui.theme.findInkScheme
import com.apksherlock.pandalauncher.ui.theme.inkSchemesFor
import com.apksherlock.pandalauncher.ui.theme.schemeFamilyMatches
import com.apksherlock.pandalauncher.ui.theme.inkSurface
import com.apksherlock.pandalauncher.ui.theme.inkTextStyles
import com.apksherlock.pandalauncher.ui.theme.resolveInkPalette

@Composable
fun InkSettingsAppearanceScreen(
    selectedSchemeId: String,
    onSchemeSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    swipeThresholdPx: Float,
    modifier: Modifier = Modifier,
) {
    val palette = InkThemeAccessor.palette
    val text = InkThemeAccessor.text
    val context = LocalContext.current
    val darkTheme = isSystemInDarkTheme()
    val schemes = remember(darkTheme) { inkSchemesFor(darkTheme) }
    val previewIcon = remember(context) {
        context.packageManager.getApplicationIcon(context.applicationInfo)
    }
    val mockAppLabel = stringResource(R.string.settings_appearance_mock_app_label)
    val mockApp = remember(previewIcon, mockAppLabel, context.packageName) {
        LaunchableApp(
            packageName = context.packageName,
            label = mockAppLabel,
            componentName = ComponentName(context.packageName, "${context.packageName}.MainActivity"),
            icon = previewIcon,
        )
    }
    val selectedFamilyId = remember(selectedSchemeId) { familyIdForSchemeId(selectedSchemeId) }
    var schemeId by remember(darkTheme, selectedFamilyId) {
        mutableStateOf(
            schemes.find { schemeFamilyMatches(it.id, selectedFamilyId) }?.id
                ?: selectedSchemeId,
        )
    }
    LaunchedEffect(selectedSchemeId, darkTheme, schemes) {
        schemeId = schemes.find { schemeFamilyMatches(it.id, familyIdForSchemeId(selectedSchemeId)) }?.id
            ?: selectedSchemeId
    }

    val previewScheme = remember(schemeId, darkTheme) { findInkScheme(schemeId, darkTheme) }
    val previewPalette = remember(schemeId, darkTheme) {
        resolveInkPalette(schemeId, darkTheme)
    }
    BackHandler(onBack = onNavigateBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .inkSurface(color = palette.canvas)
            .detectSwipeUpToNavigateBack(swipeThresholdPx, onNavigateBack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        InkText(
            text = stringResource(R.string.settings_appearance_title),
            style = text.greeting,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        )
        InkText(
            text = stringResource(R.string.settings_appearance_subtitle),
            style = text.notificationEmpty,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        InkText(
            text = stringResource(R.string.settings_appearance_preview),
            style = text.dateCaps,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        InkAppearanceAppItemPreview(
            scheme = previewScheme,
            palette = previewPalette,
            mockApp = mockApp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        )

        InkText(
            text = stringResource(R.string.settings_appearance_schemes),
            style = text.dateCaps,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        InkText(
            text = stringResource(R.string.settings_appearance_schemes_hint),
            style = text.notificationEmpty,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(end = 8.dp),
            modifier = Modifier.padding(bottom = 24.dp),
        ) {
            items(schemes, key = { it.id }) { scheme ->
                InkSchemeSwatch(
                    scheme = scheme,
                    selected = schemeFamilyMatches(scheme.id, familyIdForSchemeId(schemeId)),
                    onClick = {
                        schemeId = scheme.id
                        onSchemeSelected(scheme.id)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InkAppearanceAppItemPreview(
    scheme: InkColorScheme,
    palette: InkPalette,
    mockApp: LaunchableApp,
    modifier: Modifier = Modifier,
) {
    val caption = InkThemeAccessor.text

    Column(
        modifier = modifier
            .inkSurface(color = scheme.canvas, shape = InkShape.corners)
            .border(1.dp, scheme.ink.copy(alpha = 0.2f), InkShape.corners)
            .padding(16.dp),
    ) {
        InkText(
            text = stringResource(R.string.settings_appearance_preview_app),
            style = caption.dateCaps,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        AppearancePreviewThemeHost(palette = palette) {
            Column {
                InkText(
                    text = stringResource(R.string.settings_appearance_preview_app_grid),
                    style = caption.notificationEmpty,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    InkAppGridCell(
                        app = mockApp,
                        onClick = {},
                        onLongClick = {},
                        modifier = Modifier.width(96.dp),
                    )
                }
                InkDivider()
                Spacer(Modifier.height(8.dp))
                InkText(
                    text = stringResource(R.string.settings_appearance_preview_app_row),
                    style = caption.notificationEmpty,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                InkAppRow(
                    app = mockApp,
                    showDivider = false,
                    onClick = {},
                )
            }
        }
    }
}

@Composable
internal fun AppearancePreviewThemeHost(
    palette: InkPalette,
    content: @Composable () -> Unit,
) {
    val fonts = inkFontFamilies()
    val text = inkTextStyles(fonts, palette)
    CompositionLocalProvider(
        LocalInkPalette provides palette,
        LocalInkTextStyles provides text,
    ) {
        content()
    }
}

@Composable
private fun InkSchemeSwatch(
    scheme: InkColorScheme,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text
    val borderColor = if (selected) {
        InkThemeAccessor.palette.accent
    } else {
        scheme.ink.copy(alpha = 0.15f)
    }

    Column(
        modifier = modifier
            .size(width = 88.dp, height = 108.dp)
            .border(2.dp, borderColor, InkShape.corners)
            .inkClickable(onClick = onClick, contentPadding = InkClickMetrics.none)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .inkSurface(color = scheme.canvas, shape = InkShape.corners),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(28.dp)
                    .inkSurface(color = scheme.backplate, shape = InkShape.corners),
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 18.dp, height = 10.dp)
                        .inkSurface(color = scheme.accent, shape = InkShape.corners),
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .inkSurface(color = scheme.accent, shape = InkShape.corners),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        InkText(
            text = scheme.label,
            style = text.notificationEmpty,
            maxLines = 1,
        )
    }
}

private fun Modifier.detectSwipeUpToNavigateBack(
    thresholdPx: Float,
    onNavigateBack: () -> Unit,
): Modifier = pointerInput(thresholdPx) {
    var accumulated = 0f
    detectVerticalDragGestures(
        onDragStart = { accumulated = 0f },
        onDragEnd = {
            if (accumulated <= -thresholdPx) {
                onNavigateBack()
            }
            accumulated = 0f
        },
        onVerticalDrag = { _, dragAmount ->
            accumulated += dragAmount
        },
    )
}
