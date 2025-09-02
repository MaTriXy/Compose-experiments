package com.matrixy.compose_experiments.experiments.particle_text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matrixy.compose_experiments.data.ExperimentContent
import com.matrixy.compose_experiments.ui.components.ExperimentPlaceholder

/**
 * Particle Text Experiment
 * 
 * A text built with particles that you can interact with and an animated gradient button.
 * This recreates the SwiftUI particle text experiment using Jetpack Compose.
 * 
 * Features to implement:
 * - Text rendered as individual particles arranged in letter shapes
 * - Interactive particle disturbance on touch/hover
 * - Particles that reform into text after disturbance
 * - Animated gradient button with particle effects
 * - Smooth particle physics and text reformation
 */
class ParticleTextExperimentContent : ExperimentContent {
    
    @Composable
    override fun Content(modifier: Modifier) {
        ExperimentPlaceholder(
            title = "Particle Text",
            description = "A text built with particles that you can interact with and an animated gradient button",
            modifier = modifier
        )
    }
}