package com.matrixy.composeexperiments.experiments.blob_animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.dp
import com.matrixy.composeexperiments.data.ExperimentContent
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

data class BlobPoint(
    var x: Float = 0f,
    var y: Float = 0f,
    var velocityX: Float = 0f,
    var velocityY: Float = 0f
)


class BlobAnimationExperimentContent : ExperimentContent {
    
    @Composable
    override fun Content(modifier: Modifier) {
        BlobAnimationScreen(modifier = modifier)
    }
}

@Composable
fun BlobAnimationScreen(modifier: Modifier = Modifier) {
    val numPoints = 200
    var dynamicRadius by remember { mutableStateOf(350f) }
    val points = remember { mutableStateListOf<BlobPoint>() }
    
    // Initialize points when we have screen dimensions
    LaunchedEffect(dynamicRadius) {
        if (points.isEmpty()) {
            points.clear()
            (0 until numPoints).forEach { i ->
                val angle = (2.0 * PI * i) / numPoints
                points.add(BlobPoint(
                    x = (cos(angle) * dynamicRadius).toFloat(),
                    y = (sin(angle) * dynamicRadius).toFloat(),
                    velocityX = Random.nextFloat() * 1f - 0.5f,
                    velocityY = Random.nextFloat() * 1f - 0.5f
                ))
            }
        }
    }
    
    var time by remember { mutableStateOf(0f) }
    var currentTouchPressure by remember { mutableStateOf(0f) }
    var colorShift by remember { mutableStateOf(0f) }
    var isTouching by remember { mutableStateOf(false) }
    var touchPoint by remember { mutableStateOf(Offset.Zero) }
    var blobCenter by remember { mutableStateOf(Offset.Zero) }
    
    val pressureColorAnimation by animateFloatAsState(
        targetValue = currentTouchPressure,
        animationSpec = tween(durationMillis = 50)
    )
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) 
            time += 0.016f
            
            if (isTouching) {
                colorShift += 0.02f
                if (colorShift > 2 * PI) colorShift = 0f
            }
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isTouching = true
                        currentTouchPressure = 1f
                        touchPoint = offset
                    },
                    onDrag = { change, _ -> 
                        val pressure = if (change.pressure > 0f) minOf(change.pressure * 2f, 2f) else 1f
                        currentTouchPressure = pressure
                        touchPoint = change.position
                    },
                    onDragEnd = {
                        isTouching = false
                        currentTouchPressure = 0f
                        colorShift = 0f
                    }
                )
            }
    ) {
        var centerX = size.width / 2
        var centerY = size.height / 2
        
        // Make blob move toward touch point
        if (isTouching && touchPoint != Offset.Zero) {
            val targetCenter = touchPoint
            val currentCenter = Offset(centerX, centerY)
            val lerpFactor = 0.02f // Smooth following
            
            centerX = currentCenter.x + (targetCenter.x - currentCenter.x) * lerpFactor
            centerY = currentCenter.y + (targetCenter.y - currentCenter.y) * lerpFactor
        }
        
        // Calculate safe radius based on screen dimensions (2.5x bigger)
        val maxRadius = minOf(size.width, size.height) * 0.875f
        val safeRadius = minOf(dynamicRadius, maxRadius)
        
        // Update dynamic radius if needed
        if (dynamicRadius > maxRadius) {
            dynamicRadius = maxRadius
        }
        
        if (points.isNotEmpty()) {
            updateBlobPhysics(
                points = points,
                time = time,
                radius = safeRadius,
                touchPoint = if (isTouching) touchPoint else null,
                blobCenterX = centerX,
                blobCenterY = centerY,
                touchPressure = currentTouchPressure
            )
            
            drawBlob(
                points = points,
                centerX = centerX,
                centerY = centerY,
                pressureIntensity = pressureColorAnimation,
                colorShift = colorShift,
                isTouching = isTouching
            )
        }
    }
}

fun updateBlobPhysics(
    points: MutableList<BlobPoint>,
    time: Float,
    radius: Float,
    touchPoint: Offset?,
    blobCenterX: Float,
    blobCenterY: Float,
    touchPressure: Float
) {
    val springStrength = 0.08f
    val damping = 0.95f
    val autonomousStrength = 0.3f
    
    for (i in points.indices) {
        val point = points[i]
        
        val noiseX = sin(time * -2f + i * 0.1f) * autonomousStrength
        val noiseY = cos(time * -2f + i * 0.1f) * autonomousStrength
        
        val angle = (2.0 * PI * i) / points.size
        val restX = (cos(angle) * radius).toFloat()
        val restY = (sin(angle) * radius).toFloat()
        
        var fx = (restX - point.x) * springStrength + noiseX
        var fy = (restY - point.y) * springStrength + noiseY
        
        // Apply touch force - points closer to touch get pushed away more
        touchPoint?.let { touch ->
            val touchForceRadius = 150f // How far the touch effect reaches
            val touchX = touch.x - blobCenterX
            val touchY = touch.y - blobCenterY
            
            val dx = point.x - touchX
            val dy = point.y - touchY
            val distance = sqrt(dx * dx + dy * dy)
            
            if (distance < touchForceRadius) {
                // Stronger force when closer to touch, influenced by pressure
                val forceStrength = (1f - distance / touchForceRadius) * touchPressure * 8f
                val pushDirection = if (distance > 0.1f) {
                    Pair(dx / distance, dy / distance) // Push away from touch
                } else {
                    Pair(1f, 0f) // Default direction if too close
                }
                
                fx += pushDirection.first * forceStrength
                fy += pushDirection.second * forceStrength
            }
        }
        
        point.velocityX = point.velocityX * damping + fx
        point.velocityY = point.velocityY * damping + fy
        point.x += point.velocityX
        point.y += point.velocityY
    }
}

fun DrawScope.drawBlob(
    points: List<BlobPoint>,
    centerX: Float,
    centerY: Float,
    pressureIntensity: Float,
    colorShift: Float,
    isTouching: Boolean
) {
    val path = Path()
    
    if (points.isEmpty()) return
    
    val firstPoint = points[0]
    path.moveTo(firstPoint.x + centerX, firstPoint.y + centerY)
    
    for (i in points.indices) {
        val j = (i + 1) % points.size
        val k = (i + 2) % points.size
        
        val p2 = points[j]
        val p3 = points[k]
        
        val cp2X = (p2.x + p3.x) / 2f + centerX
        val cp2Y = (p2.y + p3.y) / 2f + centerY
        
        path.quadraticTo(
            p2.x + centerX, p2.y + centerY,
            cp2X, cp2Y
        )
    }
    
    path.close()
    
    // White blob by default, colorful when touching
    val r: Float
    val g: Float
    val b: Float
    
    if (isTouching) {
        val hue = colorShift
        val intensity = 0.7f + pressureIntensity * 0.3f
        r = (sin(hue) * 0.5f + 0.5f) * intensity
        g = (sin(hue + 2.094f) * 0.5f + 0.5f) * intensity
        b = (sin(hue + 4.188f) * 0.5f + 0.5f) * intensity
    } else {
        // White blob when not touching
        r = 1f
        g = 1f
        b = 1f
    }
    
    drawIntoCanvas { canvas ->
        val glowAlpha = 0.3f + pressureIntensity * 0.2f
        
        val shadowPaint = Paint().apply {
            color = Color(r * 1.2f, g * 1.2f, b * 1.2f, 0.5f + pressureIntensity * 0.3f)
            isAntiAlias = true
        }
        
        val gradientCenter = Offset(centerX, centerY)
        val gradientRadius = 200f + pressureIntensity * 100f
        
        val gradientShader = RadialGradientShader(
            center = gradientCenter,
            radius = gradientRadius,
            colors = listOf(
                Color(r, g, b, 1f),
                Color(r * 0.8f, g * 0.8f, b * 0.8f, 0.9f),
                Color(r * 0.6f, g * 0.6f, b * 0.6f, 0.7f)
            ),
            colorStops = listOf(0f, 0.7f, 1f)
        )
        
        val paint = Paint().apply {
            shader = gradientShader
            isAntiAlias = true
        }
        
        for (i in 0..2) {
            val blurAlpha = glowAlpha * (3 - i) / 3f * 0.6f
            val blurPaint = Paint().apply {
                color = Color(r, g, b, blurAlpha)
                isAntiAlias = true
            }
            canvas.save()
            canvas.scale(1f + i * 0.02f, 1f + i * 0.02f, centerX, centerY)
            canvas.drawPath(path, blurPaint)
            canvas.restore()
        }
        
        canvas.drawPath(path, shadowPaint)
        canvas.drawPath(path, paint)
    }
}