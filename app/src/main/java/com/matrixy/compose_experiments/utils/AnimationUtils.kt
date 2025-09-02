package com.matrixy.compose_experiments.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object AnimationSpecs {
    val bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val smooth = tween<Float>(
        durationMillis = 300
    )
    
    val quick = tween<Float>(
        durationMillis = 150
    )
    
    val slow = tween<Float>(
        durationMillis = 800
    )
    
    val elastic = spring<Float>(
        dampingRatio = 0.3f,
        stiffness = 400f
    )
}

@Composable
fun animatedScale(
    targetValue: Float,
    animationSpec: AnimationSpec<Float> = AnimationSpecs.bouncy
): State<Float> {
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "scale"
    )
}

fun Modifier.bounceClickEffect(
    scaleDown: Float = 0.95f,
    animationSpec: AnimationSpec<Float> = AnimationSpecs.bouncy
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animatedScale(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = animationSpec
    )
    
    this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

fun Modifier.draggable(
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDrag: (Offset) -> Unit = {}
): Modifier = pointerInput(Unit) {
    detectDragGestures(
        onDragStart = onDragStart,
        onDragEnd = onDragEnd,
        onDrag = { change, _ -> onDrag(change.position) }
    )
}

// Physics utilities
object PhysicsUtils {
    fun calculateDistance(point1: Offset, point2: Offset): Float {
        val dx = point1.x - point2.x
        val dy = point1.y - point2.y
        return sqrt(dx * dx + dy * dy)
    }
    
    fun normalizeVector(vector: Offset): Offset {
        val length = sqrt(vector.x * vector.x + vector.y * vector.y)
        return if (length > 0) Offset(vector.x / length, vector.y / length) else Offset.Zero
    }
    
    fun applySpringForce(
        position: Offset,
        target: Offset,
        springStrength: Float = 0.1f,
        damping: Float = 0.9f,
        velocity: Offset
    ): Pair<Offset, Offset> {
        val force = Offset(
            (target.x - position.x) * springStrength,
            (target.y - position.y) * springStrength
        )
        
        val newVelocity = Offset(
            velocity.x * damping + force.x,
            velocity.y * damping + force.y
        )
        
        val newPosition = Offset(
            position.x + newVelocity.x,
            position.y + newVelocity.y
        )
        
        return newPosition to newVelocity
    }
    
    fun waveFunction(
        time: Float,
        amplitude: Float = 1f,
        frequency: Float = 1f,
        phase: Float = 0f
    ): Float {
        return amplitude * sin(2 * PI * frequency * time + phase).toFloat()
    }
    
    fun circularMotion(
        time: Float,
        radius: Float,
        speed: Float = 1f
    ): Offset {
        val angle = time * speed
        return Offset(
            x = radius * cos(angle).toFloat(),
            y = radius * sin(angle).toFloat()
        )
    }
}

// Color animation utilities
object ColorUtils {
    fun hslToRgb(h: Float, s: Float, l: Float): Triple<Float, Float, Float> {
        val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
        val x = c * (1f - kotlin.math.abs(((h / 60f) % 2f) - 1f))
        val m = l - c / 2f
        
        val (r1, g1, b1) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        
        return Triple(r1 + m, g1 + m, b1 + m)
    }
    
    fun interpolateHue(hue1: Float, hue2: Float, progress: Float): Float {
        val diff = hue2 - hue1
        val shortestDiff = when {
            diff > 180f -> diff - 360f
            diff < -180f -> diff + 360f
            else -> diff
        }
        return (hue1 + shortestDiff * progress + 360f) % 360f
    }
}

// Gesture utilities
class GestureState {
    var isDragging by mutableStateOf(false)
    var dragOffset by mutableStateOf(Offset.Zero)
    var velocity by mutableStateOf(Offset.Zero)
    var lastPosition by mutableStateOf(Offset.Zero)
    
    fun reset() {
        isDragging = false
        dragOffset = Offset.Zero
        velocity = Offset.Zero
        lastPosition = Offset.Zero
    }
}

@Composable
fun rememberGestureState(): GestureState = remember { GestureState() }

// Dimension utilities
@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}

@Composable
fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}