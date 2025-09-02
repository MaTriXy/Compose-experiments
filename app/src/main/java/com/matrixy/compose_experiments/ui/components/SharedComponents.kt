package com.matrixy.compose_experiments.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.matrixy.compose_experiments.utils.CanvasUtils
import com.matrixy.compose_experiments.utils.CanvasUtils.drawWave
import com.matrixy.compose_experiments.utils.CanvasUtils.drawGradientCircle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Canvas(
        modifier = modifier.size(size)
    ) {
        val radius = size.toPx() / 2f
        val strokeWidth = radius / 8f
        
        for (i in 0..11) {
            val angle = (i * 30f + rotation) * PI / 180f
            val startRadius = radius * 0.6f
            val endRadius = radius * 0.9f
            val alpha = (12 - i) / 12f
            
            drawLine(
                color = color.copy(alpha = alpha),
                start = Offset(
                    center.x + cos(angle).toFloat() * startRadius,
                    center.y + sin(angle).toFloat() * startRadius
                ),
                end = Offset(
                    center.x + cos(angle).toFloat() * endRadius,
                    center.y + sin(angle).toFloat() * endRadius
                ),
                strokeWidth = strokeWidth
            )
        }
    }
}

@Composable
fun GradientBackground(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = colors)
            )
    ) {
        content()
    }
}

@Composable
fun PulsingCircle(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 100.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Canvas(
        modifier = modifier.size(size)
    ) {
        val radius = (size.toPx() / 2f) * scale
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = radius
        )
    }
}

@Composable
fun WaveAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    amplitude: Float = 50f,
    frequency: Float = 2f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        drawWave(
            amplitude = amplitude,
            frequency = frequency,
            phase = phase,
            color = color,
            strokeWidth = 4f
        )
    }
}

@Composable
fun ParticleField(
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary
    )
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        repeat(particleCount) { index ->
            val normalizedIndex = index / particleCount.toFloat()
            val angle = normalizedIndex * 2f * PI + time * PI
            val radius = size.minDimension / 4f + 
                         (size.minDimension / 8f) * sin(time * 4f * PI + normalizedIndex * 10f).toFloat()
            
            val x = center.x + cos(angle).toFloat() * radius
            val y = center.y + sin(angle).toFloat() * radius
            
            val particleSize = 2f + 4f * sin(time * 6f * PI + normalizedIndex * 8f).toFloat()
            val color = colors[(index * colors.size / particleCount) % colors.size]
            
            drawCircle(
                color = color.copy(alpha = 0.7f),
                radius = particleSize,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun GlowingOrb(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val intensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "intensity"
    )
    
    Canvas(
        modifier = modifier.size(size)
    ) {
        val radius = size.toPx() / 2f
        val glowRadius = radius * (1f + intensity * 0.5f)
        
        // Outer glow
        drawGradientCircle(
            center = center,
            radius = glowRadius,
            colors = listOf(
                color.copy(alpha = 0f),
                color.copy(alpha = intensity * 0.3f),
                color.copy(alpha = 0f)
            )
        )
        
        // Inner core
        drawCircle(
            color = color.copy(alpha = intensity),
            radius = radius * 0.6f
        )
    }
}

@Composable
fun ExperimentPlaceholder(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadingSpinner(
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Coming Soon!",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}