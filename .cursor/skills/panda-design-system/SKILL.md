---
name: panda-design-system
description: PandaLauncher Ink Edition — Jetpack Compose, Kindle ink/milk, no Material, bundled fonts.
---

# PandaLauncher — Ink Edition

**Jetpack Compose only.** Home must not use XML layouts, View system, Fragments, or Material Design (`com.google.android.material`, `MaterialTheme`, Material3 widgets).

## Aesthetic

Printed editorial page — Kindle / newspaper. Flat. Two colors only (+ ghost divider).

| Token | Light | Dark (flipped) |
|-------|--------|----------------|
| ink | `#1A1A1A` | becomes background |
| milk | `#F0EDE8` | becomes foreground |
| ink-ghost | ink @ 8% | milk @ 8% |

`rememberInkPalette()` in `ui/theme/InkPalette.kt` — use `InkTheme { }` wrapper.

## Typography (bundled `res/font/`)

| Role | Font file | Usage |
|------|-----------|--------|
| Greeting | Playfair Italic | "hi, i'm" 28sp |
| Username | Dancing Script | 22sp |
| Clock pill | Playfair Bold | milk on ink |
| Date / battery / weather | System sans | caps / 9sp |
| App names | Playfair Bold | 18sp list |

Never download fonts at runtime.

## Shape & interaction

- **Corner radius: `8.dp`** — `InkShape.cornerRadius` / `InkShape.corners` for every surface, pill, and press highlight
- Surfaces: `Modifier.inkSurface(color)`
- Tappable rows/controls: `Modifier.inkClickable { }` — `inkGhost` fill on press, **no** Material ripple

## Layout (one home screen)

`InkHomeScreen` — single full-width `Column`:

1. **Left (milk):** `InkHeader` — greeting + date only
2. **Right (ink):** `InkBookmarkRibbon` — shader bookmark top, empty gap, solid ink tail
3. **Bottom:** `InkIconStack` — 5 icons only, vertical, **not scrollable**, sizes adapt to screen height

- Icons sit in the ribbon gap (above tail), end-aligned toward ribbon
- Only the ribbon uses AGSL shader (`ShaderRibbonView`); main canvas stays flat milk
- Ribbon color is **opposite** of left (`palette.ink` on `palette.milk`)

## Primitives

- `InkText` — `BasicText` only (not Material `Text`)
- `InkDivider` — 0.5dp `inkGhost`
- No `Card`, `ElevatedButton`, `Scaffold`, ripple from Material

## Files

```
MainActivity.kt              # ComponentActivity + setContent
ui/theme/InkTheme.kt
ui/theme/InkPalette.kt
ui/theme/InkTypography.kt
ui/home/InkHomeScreen.kt
ui/home/InkHeader.kt
ui/home/InkClockPill.kt
ui/theme/InkShape.kt
ui/home/InkAppRow.kt
ui/components/InkText.kt
data/AppRepository.kt
```

## Anti-patterns

- XML `activity_main` / `fragment_home` for UI
- `AppCompatActivity` + view binding for home
- Material3 / M2 theme or components
- Extra colors, gradients, elevation, rounded icon masks
- `androidx.compose.material3.*` unless user explicitly allows

## When adding features

- New screens: `@Composable` + `InkTheme`
- Persistence: DataStore (not SharedPreferences)
- System bars: tune in `MainActivity` / `SideEffect`, light icons on milk / dark on ink
