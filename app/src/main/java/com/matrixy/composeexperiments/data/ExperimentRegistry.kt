package com.matrixy.composeexperiments.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matrixy.composeexperiments.experiments.shapes.ShapesExperimentContent
import com.matrixy.composeexperiments.experiments.blob_animation.BlobAnimationExperimentContent
import com.matrixy.composeexperiments.experiments.bouncy_grid.BouncyGridExperimentContent
import com.matrixy.composeexperiments.experiments.calculator_metric.CalculatorMetricExperimentContent
import com.matrixy.composeexperiments.experiments.colorful_glow.ColorfulGlowExperimentContent
import com.matrixy.composeexperiments.experiments.drag_to_delete.DragToDeleteExperimentContent
import com.matrixy.composeexperiments.experiments.drag_transform.DragTransformExperimentContent
import com.matrixy.composeexperiments.experiments.particle_text.ParticleTextExperimentContent

enum class ExperimentCategory(val displayName: String) {
    VISUAL_EFFECTS("Visual Effects & Animation"),
    GESTURES("Gesture-Based Interactions"),
    DATA_VIZ("Data Visualization"),
    UI_CONCEPTS("Novel UI Concepts"),
    PARTICLE_SYSTEMS("Particle Systems"),
    INTERACTIVE("Interactive Elements")
}

enum class ExperimentDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

data class ExperimentMetadata(
    val id: String,
    val displayName: String,
    val description: String,
    val category: ExperimentCategory,
    val difficulty: ExperimentDifficulty,
    val tags: List<String>,
    val swiftUIReference: String? = null,
    val isImplemented: Boolean = true
)

interface ExperimentContent {
    @Composable
    fun Content(modifier: Modifier = Modifier)
}

data class Experiment(
    val metadata: ExperimentMetadata,
    val content: ExperimentContent
)

object ExperimentRegistry {
    
    private val experiments = mutableMapOf<String, Experiment>()
    
    fun register(experiment: Experiment) {
        experiments[experiment.metadata.id] = experiment
    }
    
    fun getExperiment(id: String): Experiment? = experiments[id]
    
    fun getAllExperiments(): List<Experiment> = experiments.values.toList()
    
    fun getExperimentsByCategory(category: ExperimentCategory): List<Experiment> {
        return experiments.values.filter { it.metadata.category == category }
    }
    
    fun searchExperiments(query: String): List<Experiment> {
        return experiments.values.filter { experiment ->
            experiment.metadata.displayName.contains(query, ignoreCase = true) ||
            experiment.metadata.description.contains(query, ignoreCase = true) ||
            experiment.metadata.tags.any { tag -> tag.contains(query, ignoreCase = true) }
        }
    }
    
    fun getImplementedExperiments(): List<Experiment> {
        return experiments.values.filter { it.metadata.isImplemented }
    }
    
    fun getPlaceholderExperiments(): List<Experiment> {
        return experiments.values.filter { !it.metadata.isImplemented }
    }
    
    // Initialize all experiments
    fun initialize() {
        registerAllExperiments()
    }
    
    private fun registerAllExperiments() {
        // Visual Effects & Animation
        register(createExperiment(
            id = "blob_animation",
            displayName = "Blob Animation",
            description = "Interactive liquid metal animation with pressure-sensitive touch, dynamic color gradients, and ripple effects",
            category = ExperimentCategory.VISUAL_EFFECTS,
            difficulty = ExperimentDifficulty.ADVANCED,
            tags = listOf("animation", "gesture", "canvas", "liquid", "metal", "ripple", "pressure", "gradient"),
            swiftUIReference = "blob animation"
        ))
        
        register(createExperiment(
            id = "colorful_glow",
            displayName = "Colorful Glow",
            description = "Drag around to see a colorful glow that changes in shape and color",
            category = ExperimentCategory.VISUAL_EFFECTS,
            difficulty = ExperimentDifficulty.INTERMEDIATE,
            tags = listOf("glow", "color", "drag", "shape", "blur"),
            swiftUIReference = "colorful glow"
        ))
        
        register(createExperiment(
            id = "shapes",
            displayName = "Shapes",
            description = "Drag around to see random shapes generated that vary in shape type, color and size",
            category = ExperimentCategory.VISUAL_EFFECTS,
            difficulty = ExperimentDifficulty.BEGINNER,
            tags = listOf("shapes", "random", "drag", "color", "size", "generation"),
            swiftUIReference = "shapes"
        ))
        
        // Gesture-Based Interactions
        register(createExperiment(
            id = "drag_transform",
            displayName = "Drag Transform",
            description = "A prototype that shows an interaction on how to re-arrange your iOS navigation",
            category = ExperimentCategory.GESTURES,
            difficulty = ExperimentDifficulty.INTERMEDIATE,
            tags = listOf("drag", "navigation", "transform", "rearrange"),
            swiftUIReference = "drag transform"
        ))
        
        register(createExperiment(
            id = "drag_to_delete",
            displayName = "Drag to Delete",
            description = "A prototype that shows a drag to delete interaction",
            category = ExperimentCategory.GESTURES,
            difficulty = ExperimentDifficulty.BEGINNER,
            tags = listOf("drag", "delete", "swipe", "gesture"),
            swiftUIReference = "drag to delete"
        ))
        
        // Interactive Elements
        register(createExperiment(
            id = "bouncy_grid",
            displayName = "Bouncy Grid",
            description = "A grid of animated lines dynamically bends and bounces in response to gestures",
            category = ExperimentCategory.INTERACTIVE,
            difficulty = ExperimentDifficulty.INTERMEDIATE,
            tags = listOf("grid", "bouncy", "lines", "gesture", "responsive"),
            swiftUIReference = "bouncy grid"
        ))
        
        // Data Visualization
        register(createExperiment(
            id = "calculator_metric",
            displayName = "Calculator Metric",
            description = "A prototype that converts numbers to the metric system",
            category = ExperimentCategory.DATA_VIZ,
            difficulty = ExperimentDifficulty.BEGINNER,
            tags = listOf("calculator", "metric", "conversion", "numbers"),
            swiftUIReference = "calculator metric"
        ))

        // Particle Systems
        register(createExperiment(
            id = "particle_text",
            displayName = "Particle Text",
            description = "Animated particles that assemble into text with touch explosion effects and an animated gradient button",
            category = ExperimentCategory.PARTICLE_SYSTEMS,
            difficulty = ExperimentDifficulty.ADVANCED,
            tags = listOf("particles", "text", "animation", "explosion", "canvas", "gradient"),
            swiftUIReference = "particle text"
        ))
    }
    
    private fun createExperiment(
        id: String,
        displayName: String,
        description: String,
        category: ExperimentCategory,
        difficulty: ExperimentDifficulty,
        tags: List<String>,
        swiftUIReference: String? = null
    ): Experiment {
        val metadata = ExperimentMetadata(
            id = id,
            displayName = displayName,
            description = description,
            category = category,
            difficulty = difficulty,
            tags = tags,
            swiftUIReference = swiftUIReference,
            isImplemented = true
        )
        
        val content = when (id) {
            "shapes" -> ShapesExperimentContent()
            "blob_animation" -> BlobAnimationExperimentContent()
            "bouncy_grid" -> BouncyGridExperimentContent()
            "calculator_metric" -> CalculatorMetricExperimentContent()
            "colorful_glow" -> ColorfulGlowExperimentContent()
            "drag_to_delete" -> DragToDeleteExperimentContent()
            "drag_transform" -> DragTransformExperimentContent()
            "particle_text" -> ParticleTextExperimentContent()
            else -> error("Unknown experiment: $id")
        }
        
        return Experiment(
            metadata = metadata,
            content = content
        )
    }
}