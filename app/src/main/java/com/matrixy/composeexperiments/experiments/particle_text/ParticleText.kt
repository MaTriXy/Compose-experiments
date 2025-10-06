package com.matrixy.composeexperiments.experiments.particle_text

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

/**
 * Particle data class representing a single particle in the text
 */
data class Particle(
    val id: Int,
    val targetPosition: Offset,
    var currentPosition: Offset = targetPosition,
    var velocity: Offset = Offset.Zero,
    val animationDelay: Long = 0L
)

/**
 * Configuration for particle text appearance and behavior
 */
data class ParticleTextConfig(
    val particleSize: Float = 2f,
    val particleSpacing: Float = 3f,
    val particleColor: Color = Color.White.copy(alpha = 0.8f),
    val animationSpeed: Float = 0.1f, // 0.0 to 1.0, higher = faster
    val explosionForce: Float = 1000f,
    val explosionRadius: Float = 30f,
    val fontSize: Float = 80f,
    val fontWeight: Int = Typeface.BOLD,
    val initialDelay: Long = 0L, // Delay before particles start assembling
    val enableTouchExplosion: Boolean = true
)

/**
 * A reusable particle text component that renders text as animated particles
 *
 * @param text The text to render as particles
 * @param modifier Modifier for the canvas
 * @param config Configuration for particle appearance and behavior
 * @param onAnimationComplete Callback when particles finish assembling (first time only)
 */
@Composable
fun ParticleText(
    text: String,
    modifier: Modifier = Modifier,
    config: ParticleTextConfig = ParticleTextConfig(),
    onAnimationComplete: (() -> Unit)? = null
) {
    var particles by remember(text) { mutableStateOf<List<Particle>>(emptyList()) }
    var isInitialized by remember(text) { mutableStateOf(false) }
    var isAnimatingToTarget by remember(text) { mutableStateOf(false) }
    var isExploding by remember { mutableStateOf(false) }
    var hasCompletedOnce by remember(text) { mutableStateOf(false) }

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(text) {
        if (config.initialDelay > 0) {
            delay(config.initialDelay)
        }
        isInitialized = true
    }

    // Animation loop for particles
    LaunchedEffect(isAnimatingToTarget, isExploding, text) {
        while (isActive && (isAnimatingToTarget || isExploding)) {
            delay(16) // ~60fps

            if (isAnimatingToTarget && !isExploding) {
                particles = particles.map { particle ->
                    val dx = particle.targetPosition.x - particle.currentPosition.x
                    val dy = particle.targetPosition.y - particle.currentPosition.y
                    val distance = sqrt(dx * dx + dy * dy)

                    if (distance < 1f) {
                        particle.copy(currentPosition = particle.targetPosition)
                    } else {
                        // Ease out movement with configurable speed
                        val newX = particle.currentPosition.x + dx * config.animationSpeed
                        val newY = particle.currentPosition.y + dy * config.animationSpeed
                        particle.copy(currentPosition = Offset(newX, newY))
                    }
                }

                // Check if all particles reached target
                if (particles.all {
                    (it.currentPosition.x - it.targetPosition.x).absoluteValue < 1f &&
                    (it.currentPosition.y - it.targetPosition.y).absoluteValue < 1f
                }) {
                    isAnimatingToTarget = false

                    // Trigger callback only once
                    if (!hasCompletedOnce) {
                        hasCompletedOnce = true
                        onAnimationComplete?.invoke()
                    }
                }
            }
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(config.enableTouchExplosion, text) {
                if (config.enableTouchExplosion) {
                    detectTapGestures { offset ->
                        if (particles.isNotEmpty() && !isExploding) {
                            // Check if tap is near particles
                            val isTouchingText = particles.any { particle ->
                                val distance = sqrt(
                                    (particle.currentPosition.x - offset.x).pow(2) +
                                    (particle.currentPosition.y - offset.y).pow(2)
                                )
                                distance < config.explosionRadius
                            }

                            if (isTouchingText) {
                                isExploding = true

                                // Explode particles
                                particles = particles.map { particle ->
                                    val dx = particle.currentPosition.x - offset.x
                                    val dy = particle.currentPosition.y - offset.y
                                    val distance = sqrt(dx * dx + dy * dy)
                                    val angle = atan2(dy, dx)

                                    val force = max(0f, config.explosionForce - distance) / max(distance, 1f)
                                    val explodeX = particle.currentPosition.x + cos(angle) * force
                                    val explodeY = particle.currentPosition.y + sin(angle) * force

                                    particle.copy(
                                        currentPosition = Offset(explodeX, explodeY)
                                    )
                                }

                                // Schedule reassembly
                                scope.launch {
                                    delay(300)
                                    isExploding = false
                                    isAnimatingToTarget = true
                                }
                            }
                        }
                    }
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (isInitialized && particles.isEmpty() && text.isNotEmpty()) {
            // Generate particles from text path
            particles = generateParticlesFromText(
                text = text,
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                density = density.density,
                config = config
            )
            isAnimatingToTarget = true
        }

        // Draw particles
        particles.forEach { particle ->
            drawCircle(
                color = config.particleColor,
                radius = config.particleSize,
                center = particle.currentPosition
            )
        }
    }
}

private fun generateParticlesFromText(
    text: String,
    canvasWidth: Float,
    canvasHeight: Float,
    density: Float,
    config: ParticleTextConfig
): List<Particle> {
    val particles = mutableListOf<Particle>()
    val paint = Paint().apply {
        textSize = config.fontSize * density
        typeface = Typeface.create(Typeface.DEFAULT, config.fontWeight)
        style = Paint.Style.FILL
    }

    val path = android.graphics.Path()
    paint.getTextPath(text, 0, text.length, 0f, 0f, path)

    // Get text bounds
    val bounds = android.graphics.RectF()
    path.computeBounds(bounds, true)

    // Center the text
    val xOffset = (canvasWidth - bounds.width()) / 2 - bounds.left
    val yOffset = (canvasHeight - bounds.height()) / 2 - bounds.top

    var particleId = 0

    // Create region for path testing
    val pathRegion = android.graphics.Region()
    pathRegion.setPath(
        path,
        android.graphics.Region(
            bounds.left.toInt(),
            bounds.top.toInt(),
            bounds.right.toInt(),
            bounds.bottom.toInt()
        )
    )

    // Sample points along the path
    val spacing = config.particleSpacing.toInt().coerceAtLeast(1)
    for (x in (bounds.left.toInt()..bounds.right.toInt() step spacing)) {
        for (y in (bounds.top.toInt()..bounds.bottom.toInt() step spacing)) {
            if (pathRegion.contains(x, y)) {
                val targetPos = Offset(x + xOffset, y + yOffset)
                val startPos = Offset(
                    Random.nextFloat() * canvasWidth,
                    Random.nextFloat() * canvasHeight
                )

                particles.add(
                    Particle(
                        id = particleId++,
                        targetPosition = targetPos,
                        currentPosition = startPos,
                        animationDelay = Random.nextLong(0, 1500)
                    )
                )
            }
        }
    }

    return particles
}
