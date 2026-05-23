# Panda Launcher (WIP / Vibe Coded)

Most importantly let's see how far can you go with AI only.

A minimal Android home launcher with a vintage **LCD / Game Boy calculator** look. Built with **Jetpack Compose** and a custom two-color design system — no Material Design components.

## Features

- **Home screen** — greeting + date, notification summary (top 3), and a compact stack of five random apps
- **All-apps drawer** — swipe up from home; search, adaptive grid, swipe down or Back to close
- **Notifications** — optional listener access; tap a row to open the notification
- **Wallpaper** — seven static gradient styles (Compose `Brush`) or **none** to show the device wallpaper; **Settings → Wallpaper**
- **Debug tools** (debug builds only) — separate launcher entry with test notifications and open-home action

## Design

| Mode | Background (canvas) | Foreground (accent) |
|------|---------------------|---------------------|
| Light | `#D2D6C6` | `#1A2218` |
| Dark | `#121612` | `#9AB092` |

Monospace typography, 8dp corner radius, `InkText` / `inkClickable` primitives instead of Material widgets.

## Requirements

- Android **12+** (API 31+)
- Android Studio Ladybug or newer (or JDK 11+ and the Gradle wrapper)

## Build & install

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Open the project in Android Studio and run the **app** configuration.

## Use as your launcher

1. Install the app.
2. Press Home — when prompted, choose **Panda Launcher** and **Always**.
3. Or: **Settings → Apps → Default apps → Home app**.

## Wallpaper

Under **Settings → Wallpaper**, Panda sets a **solid** device wallpaper from your scheme. Toggle **ribbon** for a right-edge strip on the home screen (Compose, not stretched in the bitmap).

## Notification access

To show real notifications on the home screen:

**Settings → Apps → Special app access → Notification access** → enable **Panda Launcher**.

Until then, the home screen offers a link to open that settings page.

## Project layout

```
app/src/main/
├── java/com/apksherlock/pandalauncher/
│   ├── MainActivity.kt              # HOME launcher entry
│   ├── data/                        # App & notification repositories
│   ├── notifications/               # NotificationListenerService
│   ├── ui/
│   │   ├── home/                    # Home screen UI
│   │   ├── launcher/                # Home + all-apps orchestration
│   │   ├── apps/                    # All-apps sheet & grid
│   │   ├── components/              # InkText, search field, icons
│   │   └── theme/                   # Palette, typography, shapes
│   ├── ui/wallpaper/                # InkWallpaperStylePreview
│   ├── wallpaper/InkSystemWallpaper.kt  # solid canvas bitmap
│   ├── ui/home/InkHomeRibbon.kt         # optional ribbon overlay
│   ├── ui/onboarding/               # InkOnboardingFlow (5-step first run)
│   └── wallpaper/                   # Style IDs + presets
└── res/

app/src/debug/                       # Debug-only menu activity
```

## Stack

- Kotlin, Jetpack Compose (no Material3 UI dependency)
- Coroutines, Lifecycle, DataStore (preferences ready for future use)
- Compose `Brush` gradients for home wallpaper; optional transparency for device wallpaper

## Development notes

- Package: `com.apksherlock.pandalauncher`
- Compose patterns and agent guidance: see [`AGENTS.md`](AGENTS.md) and [`.cursor/skills/`](.cursor/skills/) (from [chrisbanes/skills](https://github.com/chrisbanes/skills))
- Screen wiring follows a light **state-holder / UI split** (`InkHomeScreen` → `InkHomeContent`, etc.); ViewModels can be added when persistence and richer screen state land

## License

[GNU GPL v3.0](LICENSE)
