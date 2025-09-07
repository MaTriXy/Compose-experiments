package com.matrixy.composeexperiments.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

object CanvasUtils {
    
    // Particle system utilities
    data class Particle(
        var position: Offset,
        var velocity: Offset,
        var size: Float,
        var color: Color,
        var life: Float = 1f,
        var maxLife: Float = 1f
    ) {
        val isAlive: Boolean get() = life > 0f
        
        fun update(deltaTime: Float) {
            position = Offset(
                position.x + velocity.x * deltaTime,
                position.y + velocity.y * deltaTime
            )
            life = max(0f, life - deltaTime)
        }
        
        fun updateWithGravity(deltaTime: Float, gravity: Offset = Offset(0f, 100f)) {
            velocity = Offset(
                velocity.x + gravity.x * deltaTime,
                velocity.y + gravity.y * deltaTime
            )
            update(deltaTime)
        }
        
        val alpha: Float get() = life / maxLife
    }
    
    class ParticleSystem(
        private val maxParticles: Int = 100
    ) {
        private val particles = mutableListOf<Particle>()
        
        fun addParticle(particle: Particle) {
            if (particles.size < maxParticles) {
                particles.add(particle)
            }
        }
        
        fun emit(
            position: Offset,
            count: Int,
            velocityRange: Pair<Float, Float> = Pair(-50f, 50f),
            sizeRange: Pair<Float, Float> = Pair(2f, 8f),
            colors: List<Color> = listOf(Color.White),
            lifeTime: Float = 2f
        ) {
            repeat(count) {
                val angle = Random.nextFloat() * 2f * PI
                val speed = Random.nextFloat() * (velocityRange.second - velocityRange.first) + velocityRange.first
                val velocity = Offset(
                    cos(angle).toFloat() * speed,
                    sin(angle).toFloat() * speed
                )
                
                addParticle(
                    Particle(
                        position = position,
                        velocity = velocity,
                        size = Random.nextFloat() * (sizeRange.second - sizeRange.first) + sizeRange.first,
                        color = colors.random(),
                        life = lifeTime,
                        maxLife = lifeTime
                    )
                )
            }
        }
        
        fun update(deltaTime: Float) {
            particles.removeAll { !it.isAlive }
            particles.forEach { it.updateWithGravity(deltaTime) }
        }
        
        fun draw(drawScope: DrawScope) {
            particles.forEach { particle ->
                drawScope.drawCircle(
                    color = particle.color.copy(alpha = particle.alpha),
                    radius = particle.size,
                    center = particle.position
                )
            }
        }
        
        val particleCount: Int get() = particles.size
    }
    
    // Shape drawing utilities
    fun DrawScope.drawSmoothPath(
        points: List<Offset>,
        color: Color,
        strokeWidth: Float = 2f,
        closed: Boolean = false
    ) {
        if (points.size < 2) return
        
        val path = Path()
        path.moveTo(points[0].x, points[0].y)
        
        for (i in 1 until points.size) {
            val current = points[i]
            val previous = points[i - 1]
            
            if (i == 1) {
                // First curve
                val next = if (i + 1 < points.size) points[i + 1] else current
                val controlPoint = Offset(
                    (previous.x + current.x) / 2f,
                    (previous.y + current.y) / 2f
                )
                path.quadraticBezierTo(
                    previous.x, previous.y,
                    controlPoint.x, controlPoint.y
                )
            } else {
                // Smooth curves
                val next = if (i + 1 < points.size) points[i + 1] else current
                val controlPoint1 = Offset(
                    previous.x + (current.x - points[i - 2].x) / 4f,
                    previous.y + (current.y - points[i - 2].y) / 4f
                )
                val controlPoint2 = Offset(
                    current.x - (next.x - previous.x) / 4f,
                    current.y - (next.y - previous.y) / 4f
                )
                
                path.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    current.x, current.y
                )
            }
        }
        
        if (closed) {
            path.close()
            drawPath(
                path = path,
                color = color,
                style = Fill
            )
        } else {
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokeWidth)
            )
        }
    }
    
    fun DrawScope.drawBlob(
        center: Offset,
        radius: Float,
        points: List<Offset>,
        color: Color,
        blur: Float = 0f
    ) {
        val path = Path()
        if (points.isEmpty()) return
        
        val adjustedPoints = points.map { point ->
            Offset(
                center.x + point.x * radius,
                center.y + point.y * radius
            )
        }
        
        drawSmoothPath(adjustedPoints, color, closed = true)
    }
    
    fun DrawScope.drawGradientCircle(
        center: Offset,
        radius: Float,
        colors: List<Color>,
        blendMode: BlendMode = BlendMode.SrcOver
    ) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = colors,
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center,
            blendMode = blendMode
        )
    }
    
    fun DrawScope.drawNoisyCircle(
        center: Offset,
        baseRadius: Float,
        noiseAmount: Float,
        segments: Int = 32,
        color: Color,
        time: Float = 0f
    ) {
        val points = mutableListOf<Offset>()
        
        for (i in 0 until segments) {
            val angle = (i * 2 * PI / segments).toFloat()
            val noise = sin(angle * 5 + time).toFloat() * noiseAmount
            val radius = baseRadius + noise
            
            points.add(
                Offset(
                    center.x + cos(angle) * radius,
                    center.y + sin(angle) * radius
                )
            )
        }
        
        drawSmoothPath(points, color, closed = true)
    }
    
    // Grid and pattern utilities
    fun DrawScope.drawGrid(
        cellSize: Float,
        color: Color = Color.Gray.copy(alpha = 0.3f),
        strokeWidth: Float = 1f
    ) {
        val width = size.width
        val height = size.height
        
        // Vertical lines
        var x = 0f
        while (x <= width) {
            drawLine(
                color = color,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = strokeWidth
            )
            x += cellSize
        }
        
        // Horizontal lines
        var y = 0f
        while (y <= height) {
            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = strokeWidth
            )
            y += cellSize
        }
    }
    
    fun DrawScope.drawHexagonGrid(
        hexSize: Float,
        color: Color = Color.Gray.copy(alpha = 0.3f),
        strokeWidth: Float = 1f
    ) {
        val hexWidth = hexSize * 2f
        val hexHeight = hexSize * 1.732f // sqrt(3)
        
        val cols = (size.width / (hexWidth * 0.75f)).toInt() + 2
        val rows = (size.height / hexHeight).toInt() + 2
        
        for (row in 0..rows) {
            for (col in 0..cols) {
                val x = col * hexWidth * 0.75f
                val y = row * hexHeight + (if (col % 2 == 1) hexHeight / 2f else 0f)
                
                drawHexagon(
                    center = Offset(x, y),
                    size = hexSize,
                    color = color,
                    strokeWidth = strokeWidth
                )
            }
        }
    }
    
    private fun DrawScope.drawHexagon(
        center: Offset,
        size: Float,
        color: Color,
        strokeWidth: Float = 2f
    ) {
        val path = Path()
        val angles = (0..5).map { it * PI / 3 }
        
        angles.forEachIndexed { index, angle ->
            val x = center.x + size * cos(angle).toFloat()
            val y = center.y + size * sin(angle).toFloat()
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
    
    // Wave utilities
    fun generateWavePoints(
        width: Float,
        centerY: Float,
        amplitude: Float,
        frequency: Float,
        phase: Float,
        segments: Int = 100
    ): List<Offset> {
        return (0..segments).map { i ->
            val x = (i * width) / segments
            val y = centerY + amplitude * sin(2 * PI * frequency * (x / width) + phase).toFloat()
            Offset(x, y)
        }
    }
    
    fun DrawScope.drawWave(
        amplitude: Float,
        frequency: Float,
        phase: Float,
        color: Color,
        strokeWidth: Float = 2f,
        centerY: Float = size.height / 2f
    ) {
        val points = generateWavePoints(
            width = size.width,
            centerY = centerY,
            amplitude = amplitude,
            frequency = frequency,
            phase = phase
        )
        
        drawSmoothPath(points, color, strokeWidth)
    }
    
    // Ripple effect utilities
    data class Ripple(
        val center: Offset,
        var radius: Float,
        val maxRadius: Float,
        var alpha: Float,
        val color: Color
    ) {
        val isComplete: Boolean get() = radius >= maxRadius
        
        fun update(deltaTime: Float, speed: Float = 200f) {
            radius = min(maxRadius, radius + speed * deltaTime)
            alpha = max(0f, 1f - (radius / maxRadius))
        }
    }
    
    class RippleSystem {
        private val ripples = mutableListOf<Ripple>()
        
        fun addRipple(center: Offset, maxRadius: Float, color: Color = Color.White) {
            ripples.add(
                Ripple(
                    center = center,
                    radius = 0f,
                    maxRadius = maxRadius,
                    alpha = 1f,
                    color = color
                )
            )
        }
        
        fun update(deltaTime: Float) {
            ripples.removeAll { it.isComplete }
            ripples.forEach { it.update(deltaTime) }
        }
        
        fun draw(drawScope: DrawScope) {
            ripples.forEach { ripple ->
                drawScope.drawCircle(
                    color = ripple.color.copy(alpha = ripple.alpha),
                    radius = ripple.radius,
                    center = ripple.center,
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}