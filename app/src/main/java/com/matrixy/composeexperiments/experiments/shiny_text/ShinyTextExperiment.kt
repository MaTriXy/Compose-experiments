package com.matrixy.composeexperiments.experiments.shiny_text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matrixy.composeexperiments.data.ExperimentContent
import com.matrixy.composeexperiments.ui.components.ExperimentPlaceholder

/**
 * Shiny Text Experiment
 * 
 * Example using shimmer effects on text.
 * This recreates the SwiftUI shiny text experiment using Jetpack Compose.
 * 
 * Features to implement:
 * - Shimmer effect that moves across text
 * - Gradient overlay animation with smooth transitions
 * - Multiple text styles and sizes with shimmer
 * - Configurable shimmer speed and direction
 * - Metallic/chrome-like text appearance
 */
class ShinyTextExperimentContent : ExperimentContent {
    
    @Composable
    override fun Content(modifier: Modifier) {
        ExperimentPlaceholder(
            title = "Shiny Text",
            description = "Example using shimmer effects on text",
            modifier = modifier
        )
    }
}