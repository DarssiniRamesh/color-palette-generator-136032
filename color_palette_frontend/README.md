# Color Palette Generator (Android)
A minimalist, accessible Android app that generates palettes of 5 visually distinct HSL colors. Users can regenerate palettes, copy HSL/HEX codes, and switch light/dark themes. Built with the Gradle Declarative DSL (no Compose).

## Features
- Modular HSL palette generator producing 5 unique, design-friendly colors on launch and on demand
- Five stacked color tiles with rounded corners, subtle shadows, HSL and HEX values
- Copy-to-clipboard per color (HSL and HEX) with Snackbar feedback
- Smooth, subtle crossfade transitions; debounced generate button for robustness
- Light/Dark themes following "Ocean Professional" style
- Accessibility: 48dp touch targets, content descriptions, contrast-aware text color
- Unit tests for palette logic; UI tests for primary interactions

## Build and Run
- Build: `./gradlew build`
- Install on device/emulator: `./gradlew :app:installDebug`
- Launch: Search for "Color Palette Generator"

## Tests
- Unit tests: `./gradlew :app:test`
- Instrumented tests: `./gradlew :app:connectedAndroidTest`

## Architecture
- app/src/main/kotlin/org/example/app/colors
  - HslColor: HSL model with HSL->HEX conversion
  - PaletteGenerator: distinct palette creation (configurable thresholds)
- MainActivity: renders tiles, handles copy, animations, and theme toggle
- Layouts: activity_main.xml, view_color_tile.xml

## Accessibility and Security
- No unnecessary permissions; clipboard operations use plain text only
- Supports reduced motion (skips animations if disabled at system level)
- Adequate touch targets and readable typography

## Extensibility
- PaletteGenerator supports variable sizes and can be extended for other color models
- Theming supports further customization