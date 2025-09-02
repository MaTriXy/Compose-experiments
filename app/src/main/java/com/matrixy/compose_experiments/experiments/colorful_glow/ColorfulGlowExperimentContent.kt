package com.matrixy.compose_experiments.experiments.colorful_glow

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.matrixy.compose_experiments.data.ExperimentContent
import kotlinx.coroutines.delay
import kotlin.math.max

data class GlowPoint(
    val position: Offset,
    val creationTime: Long,
    val id: Int = position.hashCode()
)

class ColorfulGlowExperimentContent : ExperimentContent {
    
    @Composable
    override fun Content(modifier: Modifier) {
        val points = remember { mutableStateListOf<GlowPoint>() }
        var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
        
        // Update time continuously for animation
        LaunchedEffect(Unit) {
            while (true) {
                currentTime = System.currentTimeMillis()
                delay(16) // ~60 FPS
            }
        }
        
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .graphicsLayer {
                    // Apply more subtle blur effect to the entire canvas
                    renderEffect = BlurEffect(radiusX = 50f, radiusY = 50f)
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            points.add(GlowPoint(offset, currentTime))
                            // Limit to 50 points for better performance with larger glows
                            if (points.size > 50) {
                                points.removeAt(0)
                            }
                        },
                        onDrag = { change, _ ->
                            points.add(GlowPoint(change.position, currentTime))
                            // Limit to 50 points for better performance with larger glows
                            if (points.size > 50) {
                                points.removeAt(0)
                            }
                        }
                    )
                }
        ) {
            drawGlowPoints(points, currentTime)
        }
    }
}

private fun DrawScope.drawGlowPoints(points: List<GlowPoint>, currentTime: Long) {
    points.forEach { point ->
        val age = (currentTime - point.creationTime) / 1000f // Convert to seconds
        
        if (age < 5f) { // Draw points for 5 seconds instead of 3
            // Create color based on age (hue cycling)
            val hue = (age % 1f) * 360f
            val color = Color.hsv(hue, 0.8f, 1f) // Reduced saturation for more subtle colors
            
            // Calculate opacity (fade out over 5 seconds)
            val opacity = max(0f, 1f - age / 5f) * 0.4f // Reduced max opacity for subtlety
            
            // Calculate larger radius for broader glow area
            val baseRadius = 120f // Much larger base radius
            val expandRadius = age * 20f // Expand as it ages
            val radius = baseRadius + expandRadius
            
            // Draw multiple layers for smoother glow effect
            for (i in 0..2) {
                val layerOpacity = opacity * (3 - i) / 3f * 0.5f
                val layerRadius = radius * (1f + i * 0.3f)
                
                drawCircle(
                    color = color.copy(alpha = layerOpacity),
                    radius = layerRadius,
                    center = point.position
                )
            }
        }
    }
    
    // Remove old points
    val cutoffTime = currentTime - 5000 // 5 seconds ago
    val iterator = (points as MutableList).iterator()
    while (iterator.hasNext()) {
        if (iterator.next().creationTime < cutoffTime) {
            iterator.remove()
        }
    }
}