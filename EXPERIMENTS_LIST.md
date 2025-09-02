# Jetpack Compose Experiments List

This document lists all the implemented Jetpack Compose experiments. Each experiment demonstrates modern Android UI development techniques, animations, and user interactions.

## Implementation Status

**7 Experiments Fully Implemented** ✅

All experiments listed below are fully functional and tested.

---

## Visual Effects & Animation

### 1. Blob Animation ✅
**Difficulty:** Advanced  
**Description:** Interactive liquid metal blob with realistic physics and pressure-sensitive touch  
**Key Compose Techniques:**
- Canvas API for custom graphics with 200 physics-based points
- Advanced gesture detection with pressure sensitivity
- Real-time shape morphing with localized deformation
- Spring physics system with touch-responsive forces
- Dynamic color gradients activated by touch/drag
- Screen-responsive sizing with safe bounds checking
**Implementation Notes:**
- Complete physics simulation with spring dynamics and damping
- Touch forces push nearby blob points away realistically
- Blob follows drag gestures with smooth interpolation
- White by default, colorful during interaction with HSV cycling
- Auto-scales to 87.5% of screen dimension for optimal size

### 2. Colorful Glow ✅
**Difficulty:** Intermediate  
**Description:** Subtle large-area glow effects that follow drag gestures with soft color transitions  
**Key Compose Techniques:**
- Canvas API for multi-layered circle rendering
- BlurEffect with graphicsLayer for atmospheric glow
- detectDragGestures for continuous touch tracking
- HSV color cycling with reduced saturation for subtlety
- Multi-layer rendering for smooth color blending
- Time-based fade animations over 5 seconds
**Implementation Notes:**
- Large glow areas (120f+ radius) that expand as they age
- Reduced opacity (0.4f max) and saturation (0.8f) for subtle effects
- 50-point limit for performance with larger glow areas
- Triple-layer rendering for each point creates smooth blending
- Black background with 50f blur radius for dreamy atmosphere

### 3. Shapes ✅
**Difficulty:** Beginner  
**Description:** Drag around to see random shapes generated that vary in shape type, color and size  
**Key Compose Techniques:**
- Canvas API for custom shape drawing
- detectDragGestures for continuous shape generation
- Random color generation with transparency
- Multiple shape types (circle, square, triangle, pentagon, hexagon, star)
- Performance optimization with shape limit (100 shapes max)

---

## Gesture-Based Interactions

### 4. Drag Transform ✅
**Difficulty:** Intermediate  
**Description:** A prototype that shows an interaction on how to re-arrange your iOS navigation  
**Key Compose Techniques:**
- Draggable dock with app icon rearrangement
- Long press to enter edit mode with scaling animations
- Smooth drag-and-drop with position tracking
- Auto-sorting with spring animations
- State management for multiple draggable items
**Implementation Notes:**
- Features Gmail, Maps, Photos, and other Google app icons
- Edit mode with wiggle animations and delete buttons
- Dock expands during drag operations
- Icons auto-arrange when dropped

### 5. Drag to Delete ✅
**Difficulty:** Beginner  
**Description:** Multiple draggable app icons with proximity-based deletion and smart auto-replenishment  
**Key Compose Techniques:**
- detectDragGestures for smooth icon movement across entire screen
- Animated proximity scaling (icons shrink, trash grows as they get closer)
- Multi-icon state management with individual position/size tracking
- Smart auto-replenishment system (maintains minimum 2 icons)
- Stable key-based composition to prevent unnecessary recomposition
- Spring animations for size changes and deletion sequences
**Implementation Notes:**
- Starts with 1-3 randomly positioned app icons in upper screen area
- Each icon can be dragged independently without affecting others
- Large, accessible trash can (120px→180px) with clear visual feedback
- Individual deletion - only the dropped icon gets deleted, others stay stable
- Automatic new icon generation when count drops below 2 icons
- No flicker during deletion - remaining icons stay perfectly stable

---

## Interactive Elements

### 6. Bouncy Grid ✅
**Difficulty:** Intermediate  
**Description:** A grid of animated lines that dynamically bends and bounces in response to gestures  
**Key Compose Techniques:**
- Canvas API for drawing grid lines with quadratic bezier curves
- detectDragGestures for real-time touch tracking
- Mathematical calculations for closest point detection and distance
- Physics-based bouncy animations with oscillation and decay
- Real-time line deformation with thickness scaling
- TimelineView equivalent using Canvas recomposition
**Implementation Notes:**
- 30 horizontal and 15 vertical white lines on black background
- Lines bend away from touch using perpendicular displacement
- Spring-back animation with 0.15s return + 0.8s oscillation phases
- Random opacity animations for visual appeal
- Smooth gesture handling with continuous touch tracking

---

## Data Visualization

### 7. Calculator Metric ✅
**Difficulty:** Beginner  
**Description:** Unit conversion interface with temperature, weight, and distance conversions  
**Key Compose Techniques:**
- State management for real-time calculations
- TextField with number keyboard input
- Long press gesture to toggle conversion directions
- Material 3 Card components with proper theming
- Animation on input changes with smooth transitions
**Implementation Notes:**
- Bidirectional conversions (°F↔°C, lbs↔kg, miles↔km, feet↔m, inches↔cm)
- Green accent color matching SwiftUI version (0xFF00E075)
- Empty input handling shows 0 instead of attempting conversions
- Tap anywhere to dismiss keyboard functionality
- Clean Material Design with proper spacing and typography

---

## Development Notes

### Testing Strategy
- Unit tests for calculation logic (Calculator Metric)
- UI tests for gesture interactions
- Performance tests for animation-heavy experiments
- Screenshot tests for visual regression

### Performance Considerations
- Use `remember` for expensive calculations
- Implement proper lifecycle handling for animations
- Optimize Canvas operations for particle systems
- Consider using `derivedStateOf` for computed values

---

## Getting Started

To run any experiment:

1. **Launch the app** - Opens to experiment selection screen
2. **Browse or search** - Use categories or search to find experiments
3. **Tap to launch** - Each experiment runs in fullscreen
4. **Interact and explore** - Each has unique gestures and interactions
5. **Use back gesture** - Return to the main menu

Each experiment is implemented as a separate composable screen with proper navigation integration.