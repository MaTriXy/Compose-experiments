# Compose Experiments

This repository contains Jetpack Compose implementations of SwiftUI experiments. Each experiment demonstrates modern Android UI development techniques with animations and user interactions.

## Project Structure

- `SwiftUI-experiments/` - Reference implementations in SwiftUI (read-only for inspiration)
- `app/src/main/java/com/matrixy/compose_experiments/` - Main Compose implementations
  - `experiments/` - Individual experiment implementations
  - `navigation/` - App navigation and routing
  - `data/` - Experiment registry and data models
  - `screens/` - Additional app screens (licenses, etc.)
  - `ui/theme/` - Theme and styling
  - `utils/` - Utility functions and helpers

## Setup

1. Clone the repository
2. Open in Android Studio
3. Build and run on device or emulator

## Development Commands

```bash
# Build the project
./gradlew build

# Run on device/emulator
./gradlew installDebug

# Run tests
./gradlew test

# Run UI tests
./gradlew connectedAndroidTest
```

## Implemented Experiments (8 Total) ✅

### Visual Effects & Animation
- **✅ Blob Animation** (Advanced): Interactive liquid metal animation with pressure-sensitive touch, dynamic color gradients, and ripple effects
- **✅ Colorful Glow** (Intermediate): Drag around to see a colorful glow that changes in shape and color
- **✅ Shapes** (Beginner): Drag around to see random shapes generated that vary in shape type, color and size

### Gesture-Based Interactions
- **✅ Drag Transform** (Intermediate): A prototype that shows an interaction on how to re-arrange your iOS navigation
- **✅ Drag to Delete** (Beginner): A prototype that shows a drag to delete interaction

### Interactive Elements
- **✅ Bouncy Grid** (Intermediate): A grid of animated lines dynamically bends and bounces in response to gestures

### Data Visualization
- **✅ Calculator Metric** (Beginner): A prototype that converts numbers to the metric system

### Particle Systems
- **✅ Particle Text** (Advanced): Animated particles that assemble into text with touch explosion effects and an animated gradient button
  - Reusable `ParticleText` composable with full customization
  - Configurable particle size, spacing, color, and animation speed
  - Touch-to-explode interaction with force-based physics
  - Automatic particle reassembly after explosion

## Features

- **Interactive navigation** with search and category filtering
- **Material 3 design** with proper theming
- **Edge-to-edge UI** with proper system bar handling

## Contributing

When adding new experiments:

1. Create a new package under `experiments/`
2. Implement the main composable function
3. Add navigation route in `ComposeExperimentsApp.kt`
4. Update the experiments list

## Tech Stack

- **Jetpack Compose**: Modern Android UI toolkit
- **Kotlin**: Programming language
- **Material 3**: Design system
- **Navigation Compose**: In-app navigation
- **Canvas API**: Custom drawing and graphics
- **Animation APIs**: Smooth animations and transitions

## Contributing

When adding new experiments:

1. Create a new package under `experiments/`
2. Implement the experiment content class
3. Register in `ExperimentRegistry.kt`
4. Update documentation

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Third-Party Assets

This project includes third-party assets. See [NOTICE](NOTICE) for important disclaimers and the in-app credits section for full attributions.