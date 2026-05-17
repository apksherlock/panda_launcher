# PandaLauncher — Ink Edition

**Jetpack Compose only** for home UI. Kindle-style ink on paper — two colors, flips in dark mode. **No Material Design.**

## Start here

Read **`.cursor/skills/panda-design-system/SKILL.md`** before changing home.

| Area | Files |
|------|--------|
| Entry | `MainActivity.kt` → `InkHomeScreen` |
| Theme | `ui/theme/InkPalette.kt`, `InkTheme.kt`, `InkTypography.kt` |
| Home | `ui/home/InkHomeScreen.kt`, `InkHeader`, `InkClockPill`, `InkAppRow` |
| Shape / press | `ui/theme/InkShape.kt` — **8.dp** corners, `inkClickable` |
| Primitives | `ui/components/InkText.kt`, `InkDivider.kt` |
| Apps | `data/AppRepository.kt` |

Package: `com.apksherlock.pandalauncher`

## Rules

- **Compose only** for screens — no XML layouts for home, no View system, no `MaterialTheme` / Material3
- Colors: `ink` `#1A1A1A`, `milk` `#F0EDE8`, `ink-ghost` 8% ink — swap in dark mode via `rememberInkPalette()`
- Fonts bundled in `res/font/` (Playfair Display, Dancing Script)
- App list: `LazyColumn`, monochrome icons (`ColorFilter.tint(ink)`)

## Compose skills

Use `.cursor/skills/compose-*` when working on state, performance, or tests.

## Do not add without ask

- Material dependency, `MaterialTheme`, `Scaffold`, Material buttons/cards
- XML-based home fragments
- Shader (removed until re-requested)
