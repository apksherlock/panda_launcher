# Panda Launcher (WIP)

A minimal Android home launcher with a vintage **LCD / Game Boy calculator** look. Built with **Jetpack Compose** and a custom two-color design system — no Material Design components.

## Features

- **Home screen** — greeting + date, notification summary (top 3), and a compact stack of five random apps
- **All-apps drawer** — swipe up from home; search, adaptive grid, swipe down or Back to close
- **Notifications** — optional listener access; tap a row to open the notification
- **Live wallpaper** — ambient LCD wave band (GLES 2.0), themed to match light/dark ink palette; home UI stays transparent when the wallpaper is active
- **Debug tools** (debug builds only) — separate launcher entry with test notifications, wallpaper picker shortcut, and open-home action

## Design

| Mode | Background (“milk”) | Foreground (“ink”) |
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

## Live wallpaper (optional)

The home screen shows the live wallpaper through a transparent window when **Ink wave** is set.

1. Install a **debug** build (or set the wallpaper from system settings).
2. **Debug:** open **Panda debug** → **Set Ink wave wallpaper** → confirm.
3. **System:** long-press home → Wallpaper → Live wallpapers → **Ink wave**.

Without the live wallpaper, home falls back to a solid milk / dark background.

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
│   └── wallpaper/                   # GLES live wallpaper + helpers
├── assets/shaders/                  # GLSL ES 2.0 fragment/vertex shaders
└── res/

app/src/debug/                       # Debug-only menu activity
```

## Stack

- Kotlin, Jetpack Compose (no Material3 UI dependency)
- Coroutines, Lifecycle, DataStore (preferences ready for future use)
- OpenGL ES 2.0 `WallpaperService` for the ink wave background

## Development notes

- Package: `com.apksherlock.pandalauncher`
- Compose patterns and agent guidance: see [`AGENTS.md`](AGENTS.md) and [`.cursor/skills/`](.cursor/skills/) (from [chrisbanes/skills](https://github.com/chrisbanes/skills))
- Screen wiring follows a light **state-holder / UI split** (`InkHomeScreen` → `InkHomeContent`, etc.); ViewModels can be added when persistence and richer screen state land

## License

[GNU GPL v3.0](LICENSE)
