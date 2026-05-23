# PandaLauncher — Ink Edition

**Jetpack Compose only** for launcher UI. **Terminal / LCD** look — monospace, flat panels, user-chosen color schemes. **No Material Design.**

## Start here

Read **`.cursor/skills/panda-design-system/SKILL.md`** before changing home, theme, or launcher navigation.

| Area | Files |
|------|--------|
| Entry | `MainActivity.kt` → `InkLauncherScreen` → `InkHomeScreen` |
| Theme | `InkTheme.kt`, `InkPalette.kt`, `InkColorPresets.kt`, `InkTypography.kt`, `AppearanceStore.kt` |
| Shape / press | `InkShape.kt` — **8.dp** corners, `inkClickable` |
| Appearance store | `PandaLauncherApplication.appearanceStore`, `requireAppearanceStore()` |
| Home | `ui/home/InkHomeScreen.kt`, `InkHeader`, `InkIconStack`, `InkNotificationList` |
| Launcher nav | `ui/launcher/InkLauncherScreen.kt`, `InkLauncherNavHost.kt`, `LauncherRoute.kt` |
| Icons | `ui/components/InkAppIcon.kt`, `ui/icons/InkIconLoader.kt` |
| Apps | `data/LaunchableAppCatalog.kt`, `AppRepository.kt` |
| Home slots | `data/HomeScreenAppStore.kt`, `ui/apps/InkHomeSlotAssignDialog.kt` |
| Appearance | `ui/settings/InkSettingsAppearanceScreen.kt` |
| Wallpaper | `wallpaper/InkSystemWallpaper.kt` (solid), `ui/home/InkHomeRibbon.kt`, `WallpaperStore.kt` |
| Onboarding | `ui/onboarding/InkOnboardingFlow.kt`, `InkOnboardingShell.kt`, `launcher/LauncherHomeRole.kt` |

Package: `com.apksherlock.pandalauncher`

## Rules

- **Compose only** for screens — no XML layouts for home, no View system, no `MaterialTheme` / Material3
- **Colors:** user-selected **scheme** (ink, canvas, backplate, accent) via Settings → appearance; use `InkThemeAccessor.palette`, not hard-coded hex
- **Typography:** monospace for UI; settings rows use `>` prompt prefix
- **Touch:** default icon/list sizes in `InkIconLoader` / `InkAppRow` (no forced 48dp on every control)
- **HOME key / home gesture:** must land on `LauncherRoute.Home` (`MainActivity.goHomeRequests` + `popBackStack`)
- App list: monochrome icons tinted with `palette.accent`; backplate uses `palette.backplate` / `backplatePressed`

## Compose skills

Use `.cursor/skills/compose-*` when working on state, performance, or tests.

## Do not add without ask

- Material dependency, `MaterialTheme`, `Scaffold`, Material buttons/cards
- XML-based home fragments
- Device wallpaper: solid `canvas` only (`InkSystemWallpaper`); **ribbon** is Compose on home (`InkHomeRibbon`), toggled in `WallpaperStore`; transparent home + `FLAG_SHOW_WALLPAPER`
- Theming system Recents / PackageInstaller dialogs
