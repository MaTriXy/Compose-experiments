package com.matrixy.composeexperiments.experiments.bouncy_grid

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.matrixy.composeexperiments.data.ExperimentContent
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

data class LineSegment(
    val start: Offset,
    val end: Offset
)

data class DentEffect(
    val thicknessScale: Float = 1.0f,
    val displacement: Float = 0.0f
)

@Composable
fun AnimatedLineView(
    segment: LineSegment,
    touchLocation: Offset?,
    releaseArea: Offset?,
    releaseDuration: Long?,
    maxEffectRadius: Float,
    baseLineWidth: Dp = 2.dp
) {
    val density = LocalDensity.current
    val baseLineWidthPx = with(density) { baseLineWidth.toPx() }
    
    var opacity by remember { mutableFloatStateOf(Random.nextFloat() * 0.7f + 0.3f) }
    
    val animatedOpacity by animateFloatAsState(
        targetValue = opacity,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (800 + Random.nextInt(400)),
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "opacity"
    )
    
    LaunchedEffect(Unit) {
        while (true) {
            delay((800 + Random.nextInt(400)).toLong())
            opacity = Random.nextFloat() * 0.5f + 0.3f
        }
    }
    
    fun distanceBetween(p1: Offset, p2: Offset): Float {
        return sqrt((p2.x - p1.x).pow(2) + (p2.y - p1.y).pow(2))
    }
    
    fun closestPointOnLineSegment(point: Offset, start: Offset, end: Offset): Offset {
        val dx = end.x - start.x
        val dy = end.y - start.y
        
        if (dx == 0f && dy == 0f) return start
        
        val lineLengthSquared = dx * dx + dy * dy
        var t = ((point.x - start.x) * dx + (point.y - start.y) * dy) / lineLengthSquared
        t = t.coerceIn(0f, 1f)
        
        return Offset(start.x + t * dx, start.y + t * dy)
    }
    
    fun computeEffect(): DentEffect {
        touchLocation?.let { touch ->
            val closestPoint = closestPointOnLineSegment(touch, segment.start, segment.end)
            val distanceToLine = distanceBetween(touch, closestPoint)
            
            val minThicknessScale = 0.4f
            val maxDisplacement = 70f
            
            if (distanceToLine < maxEffectRadius) {
                val normalizedDistance = distanceToLine / maxEffectRadius
                val currentThicknessScale = minThicknessScale + normalizedDistance * (1f - minThicknessScale)
                val currentDisplacement = maxDisplacement * (1f - normalizedDistance)
                
                return DentEffect(currentThicknessScale, -currentDisplacement)
            }
        }
        
        if (releaseArea != null && releaseDuration != null) {
            val closestPoint = closestPointOnLineSegment(releaseArea, segment.start, segment.end)
            val distanceToLine = distanceBetween(releaseArea, closestPoint)
            
            if (distanceToLine < maxEffectRadius) {
                val currentTime = System.currentTimeMillis()
                val timeSinceRelease = (currentTime - releaseDuration) / 1000.0
                val initialDisplacement = 70f * (1f - min(1f, distanceToLine / maxEffectRadius))
                val distanceFactor = 1f - (distanceToLine / maxEffectRadius)
                
                val returnDuration = 0.15
                if (timeSinceRelease <= returnDuration) {
                    val returnProgress = timeSinceRelease / returnDuration
                    val easedProgress = sin(returnProgress * PI / 2).toFloat()
                    val displacement = -initialDisplacement * (1f - easedProgress)
                    
                    return DentEffect(
                        thicknessScale = 0.4f + 0.6f * easedProgress,
                        displacement = displacement * distanceFactor
                    )
                }
                
                val oscillationDuration = 0.8
                val oscillationStartTime = returnDuration
                val oscillationEndTime = oscillationStartTime + oscillationDuration
                
                if (timeSinceRelease > oscillationStartTime && timeSinceRelease <= oscillationEndTime) {
                    val oscillationTime = (timeSinceRelease - oscillationStartTime) / oscillationDuration
                    
                    val amplitude = 0.6f * initialDisplacement
                    val frequency = 22.0
                    val decay = 3.5
                    
                    val initialSnapFactor = max(0.0, 0.7 - oscillationTime * 3).toFloat()
                    val oscillation = sin(oscillationTime * frequency) * exp(-oscillationTime * decay)
                    val combinedEffect = (oscillation + initialSnapFactor).toFloat()
                    val displacement = amplitude * combinedEffect
                    
                    return DentEffect(
                        thicknessScale = 1f + abs(combinedEffect) * 0.2f,
                        displacement = displacement * distanceFactor
                    )
                }
            }
        }
        
        return DentEffect()
    }
    
    fun DrawScope.drawLine(effect: DentEffect) {
        val path = Path()
        path.moveTo(segment.start.x, segment.start.y)
        
        if (effect.displacement != 0f) {
            val midX = (segment.start.x + segment.end.x) / 2f
            val midY = (segment.start.y + segment.end.y) / 2f
            
            val dX = segment.end.x - segment.start.x
            val dY = segment.end.y - segment.start.y
            val length = sqrt(dX * dX + dY * dY)
            
            if (length > 0) {
                val normPerpX = -dY / length
                val normPerpY = dX / length
                
                val controlPoint = Offset(
                    midX + effect.displacement * normPerpX,
                    midY + effect.displacement * normPerpY
                )
                path.quadraticTo(
                    controlPoint.x, controlPoint.y,
                    segment.end.x, segment.end.y
                )
            } else {
                path.lineTo(segment.end.x, segment.end.y)
            }
        } else {
            path.lineTo(segment.end.x, segment.end.y)
        }
        
        drawPath(
            path = path,
            color = Color.White.copy(alpha = animatedOpacity),
            style = Stroke(
                width = baseLineWidthPx * effect.thicknessScale,
                cap = StrokeCap.Round
            )
        )
    }
    
    val effect = computeEffect()
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(effect)
    }
}

/**
 * Bouncy Grid Experiment
 * 
 * A grid of animated lines dynamically bends and bounces in response to gestures.
 * This recreates the SwiftUI bouncy grid experiment using Jetpack Compose.
 */
class BouncyGridExperimentContent : ExperimentContent {
    
    @Composable
    override fun Content(modifier: Modifier) {
        var touchLocation by remember { mutableStateOf<Offset?>(null) }
        var releaseArea by remember { mutableStateOf<Offset?>(null) }
        var releaseDuration by remember { mutableStateOf<Long?>(null) }
        
        val numHorizontalLines = 30
        val numVerticalLines = 15
        val maxEffectRadius = 120f
        val resetAfterRelease = 1000L
        
        LaunchedEffect(releaseDuration) {
            releaseDuration?.let { releaseTime ->
                delay(resetAfterRelease)
                if (releaseDuration == releaseTime) {
                    releaseArea = null
                    releaseDuration = null
                }
            }
        }
        
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .clipToBounds()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            touchLocation = offset
                            releaseArea = null
                            releaseDuration = null
                        },
                        onDrag = { change, _ ->
                            touchLocation = change.position
                        },
                        onDragEnd = {
                            touchLocation?.let {
                                releaseArea = it
                                releaseDuration = System.currentTimeMillis()
                            }
                            touchLocation = null
                        }
                    )
                }
        ) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            
            val verticalSpacing = if (numHorizontalLines > 1) height / (numHorizontalLines - 1) else height
            val horizontalSpacing = if (numVerticalLines > 1) width / (numVerticalLines - 1) else width
            
            for (rowIndex in 0 until numHorizontalLines) {
                val yPos = if (numHorizontalLines > 1) rowIndex * verticalSpacing else height / 2f
                val segment = LineSegment(
                    start = Offset(0f, yPos),
                    end = Offset(width, yPos)
                )
                
                key("h$rowIndex") {
                    AnimatedLineView(
                        segment = segment,
                        touchLocation = touchLocation,
                        releaseArea = releaseArea,
                        releaseDuration = releaseDuration,
                        maxEffectRadius = maxEffectRadius
                    )
                }
            }
            
            for (colIndex in 0 until numVerticalLines) {
                val xPos = if (numVerticalLines > 1) colIndex * horizontalSpacing else width / 2f
                val segment = LineSegment(
                    start = Offset(xPos, 0f),
                    end = Offset(xPos, height)
                )
                
                key("v$colIndex") {
                    AnimatedLineView(
                        segment = segment,
                        touchLocation = touchLocation,
                        releaseArea = releaseArea,
                        releaseDuration = releaseDuration,
                        maxEffectRadius = maxEffectRadius
                    )
                }
            }
        }
    }
}