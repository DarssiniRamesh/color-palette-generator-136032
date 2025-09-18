# Ocean Professional Color Palette Generator (Android)

A minimalist Android app built with Gradle 9 Declarative DSL (no Jetpack Compose) that generates a random palette of 5 colors using HSL, displays them as vertically stacked tiles, and allows copying each color code.

## Features
- Generate random palette of 5 colors using random HSL values
- Display 5 large stacked color tiles with rounded corners
- Show HEX and HSL for each tile
- Copy color code to clipboard per tile
- Floating action button to trigger palette generation
- Clean, minimalist "Ocean Professional" theme

## Build and Run

Build the project:
```shell
./gradlew build
```

Install the debug APK on a connected device/emulator:
```shell
./gradlew :app:installDebug
```

Launch the app named "Ocean Professional Palette" and tap the refresh button (bottom-right) to generate new palettes. Tap the copy icon on any tile to copy its HEX code.