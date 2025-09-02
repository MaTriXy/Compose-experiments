package com.matrixy.compose_experiments.experiments.bob

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matrixy.compose_experiments.data.ExperimentContent
import com.matrixy.compose_experiments.ui.components.ExperimentPlaceholder

/**
 * Bob Experiment
 * 
 * A prototype that shows an interaction on how AI can summarize text.
 * This recreates the SwiftUI bob experiment using Jetpack Compose.
 * 
 * Features to implement:
 * - Floating AI assistant interface that follows text
 * - Smooth expansion animation when activated
 * - Text summarization interface with smooth transitions
 * - Interactive floating button with contextual positioning
 * - Smooth collapse and movement animations
 */
class BobExperimentContent : ExperimentContent {
    
    @Composable
    override fun Content(modifier: Modifier) {
        ExperimentPlaceholder(
            title = "Bob",
            description = "A prototype that shows an interaction on how AI can summarize text",
            modifier = modifier
        )
    }
}