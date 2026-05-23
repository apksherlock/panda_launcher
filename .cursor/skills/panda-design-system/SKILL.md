---
name: panda-design-system
description: PandaLauncher Ink Edition — Jetpack Compose, terminal/LCD aesthetic, user color schemes, no Material.
---

# PandaLauncher — Ink Edition

**Jetpack Compose only.** Home and launcher overlays must not use XML layouts, View system, Fragments, or Material Design (`com.google.android.material`, `MaterialTheme`, Material3 widgets).

## Aesthetic

**Terminal / calculator LCD** — flat, monospace, prompt-style copy (`> hi,`). Not Material, not editorial Playfair-on-paper (legacy fonts remain in `res/font/` but **UI typography is `FontFamily.Monospace` only**).

| Token | Role |
|-------|------|
| `ink` | Structural chrome: borders, ghosts, wallpaper edges |
| `accent` | Foreground: text, icons, wallpaper highlights |
| `canvas` | Screen / sheet background |
| `inkGhost` | Press highlight on rows (`ink` @ 12% alpha) |
| `backplate` | Icon bed (normal) |
| `backplatePressed` | Icon bed when pressed |

**User theming** — follows **system** light/dark (`isSystemInDarkTheme()`), but user picks a **color scheme** per mode. `AppearanceStore` stores scheme ids for light and dark; palettes from `resolveInkPalette(schemeId, darkTheme)` in `ui/theme/InkColorPresets.kt`. Each scheme sets surfaces (`canvas`, `backplate`) and foreground (`accent`) together.

- **10 light schemes**: ledger, ash, terminal, trace, solder, hematite, oxide, beacon, slate, azure.
- **8 dark schemes**: night, phosphor, matrix, blood, ice, ember, flare, cyan.
- Legacy separate icon-tint prefs migrate into the closest combined scheme via `migrateLegacyAppearance`.
- `InkTheme` must **not** `key()` on scheme/tint — that resets `NavController` and kicks the user off appearance.
- **Settings → appearance** (`InkSettingsAppearanceScreen`): live preview with mock `LaunchableApp` — real `InkAppGridCell` + `InkAppRow` under `AppearancePreviewThemeHost` (preview palette + text) + scheme row.

Never download fonts at runtime.

## Typography

Defined in `ui/theme/InkTypography.kt` — monospace; greeting/username ~20sp; list labels 16sp; **grid labels stay 10sp**; dialogs use `dialogTitle` / `dialogBody` / `dialogAction`. Home greeting: `InkBlinkingPrompt` (`>` hard-blinks) + `hi,` in `InkHeader`.

## Shape & interaction

- **Corner radius: `8.dp`** — `InkShape.cornerRadius` / `InkShape.corners`
- Surfaces: `Modifier.inkSurface(color)`
- Tappable: `Modifier.inkClickable { }` — `inkGhost` fill on press (default `InkClickMetrics.text` padding); dialog actions use `inkDialogAction`; full-bleed targets pass `InkClickMetrics.none`
- Long-press on grid: `inkCombinedClickable` on `InkAppGridCell`; press state drives icon **backplate** color only

## Icons

- `InkAppIcon` + `InkIconLoader` — grayscale/monochrome; tint from `palette.accent`
- Default sizes: **40dp** slot, **34dp** draw (`InkIconLoader.slotDp` / `drawDp`); grid uses `InkAppGridCell` sizes
- Install progress: ring on backplate using `iconTint`

## Layout

### Launcher shell

`MainActivity` → `InkTheme` → **`InkLauncherScreen`** (`ui/launcher/`) — `NavHost`, gestures, dialogs, catalog state.

| Route | Screen |
|-------|--------|
| `home` | `InkHomeScreen` |
| `all_apps` | `InkAllAppsScreen` + context menu |
| `shortcuts` | `InkShortcutsScreen` |
| `settings` | `InkSettingsScreen` |
| `settings_appearance` | `InkSettingsAppearanceScreen` |
| `settings_home_apps` / slot picker | home slot editor |

**Home button / home gesture:** `MainActivity.onNewIntent` (MAIN+HOME) emits `goHomeRequests`; `InkLauncherScreen` `popBackStack(Home)` and clears overlays. Always return to home, not stay on settings/all apps.

Swipes from home (when not onboarding): up → all apps, down → settings, left → shortcuts, right → favorite guest.

### Home screen (`InkHomeScreen`)

`BoxWithConstraints` — transparent home; device wallpaper shows through (`FLAG_SHOW_WALLPAPER`):

1. **Top:** `InkHeader` — `> hi,` + username + date
2. **Bottom start:** `InkNotificationList`
3. **Bottom end:** `InkIconStack` — up to **5** apps from `HomeScreenAppStore`, not scrollable, sizes adapt to height

**Wallpaper** — `InkSystemWallpaper` sets solid `canvas` on device. **Ribbon** toggle draws `InkHomeRibbon` in Compose on home only. Settings per light/dark in `WallpaperStore`.

### Home screen apps

- `HomeScreenAppStore` — 5 DataStore slots; `assignToSlot`, `normalizeSlots`, settings editor
- Long-press in all apps → **add to home** opens `InkHomeSlotAssignDialog` (pick slot 1–5; shows current occupant + replace hint)

## State & data

| Concern | Type / file |
|---------|-------------|
| App list | `LaunchableAppCatalog` → `StateFlow`, install polling |
| Apps CRUD | `AppRepository` |
| Appearance | `AppearanceStore` |
| Home slots | `HomeScreenAppStore` |
| Profile / favorite | `LauncherProfileStore`, `FavoriteAppStore` |

Prefer **DataStore** (`launcherPreferencesDataStore`), not SharedPreferences.

## Primitives

- `InkText` — `BasicText` only
- `InkDivider` — 0.5dp `inkGhost`
- `InkSearchField`, `InkDialogContainer` / `inkDialogAction`, `InkDisplayNameDialog`, etc.
- No `Card`, `Scaffold`, Material ripple

## Files (primary)

```
MainActivity.kt                    # HOME intent → goHomeRequests
ui/launcher/InkLauncherScreen.kt
ui/launcher/InkLauncherNavHost.kt
ui/home/InkHomeScreen.kt
ui/home/InkHeader.kt, InkIconStack.kt, InkNotificationList.kt
ui/theme/InkTheme.kt, InkPalette.kt, InkColorPresets.kt, InkTypography.kt, InkShape.kt
ui/components/InkAppIcon.kt
ui/icons/InkIconLoader.kt
ui/settings/InkSettingsScreen.kt, InkSettingsAppearanceScreen.kt
data/AppRepository.kt, LaunchableAppCatalog.kt, AppearanceStore.kt, HomeScreenAppStore.kt
```

## System bars

`enableInkEdgeToEdge()` — transparent scrims (no black status-bar overlay). `InkSystemBars` — icon color from **`palette.canvas.luminance()`**, contrast enforcement off.

## Anti-patterns

- XML home layouts, View system home, Material3 components
- Hard-coding `#1A1A1A` / `#F0EDE8` in composables — use `InkThemeAccessor.palette`
- `rememberInkPalette()` tied to system theme only — use schemes + `AppearanceStore`
- Assuming uninstall/recents/overview can be themed (system PackageInstaller / SystemUI)
- Extra gradients, elevation, Material ripples

## When adding features

- New screens: `@Composable` inside `InkTheme`, `InkThemeAccessor.palette` / `.text`
- New colors: extend `InkColorScheme` presets or document why a one-off is needed
- Touch targets: respect `inkClickable` 48dp minimum; enlarge icons in grids/lists if cramped
- Navigation from home: add route to `LauncherRoute` + `InkLauncherNavHost`; register HOME pop behavior if modal
