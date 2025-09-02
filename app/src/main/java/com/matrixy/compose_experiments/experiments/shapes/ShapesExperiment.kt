package com.matrixy.compose_experiments.experiments.shapes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import com.matrixy.compose_experiments.data.ExperimentContent
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ShapeType {
    CIRCLE, SQUARE, TRIANGLE, PENTAGON, HEXAGON, STAR
}

data class RandomShape(
    val position: Offset,
    val type: ShapeType,
    val size: Float,
    val color: Color,
    val rotation: Float = 0f
)

class ShapesExperimentContent : ExperimentContent {
    
    @Composable
    override fun Content(modifier: Modifier) {
        var shapes by remember { mutableStateOf(listOf<RandomShape>()) }
        
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        // Generate random shape at drag position
                        val newShape = RandomShape(
                            position = change.position,
                            type = ShapeType.values().random(),
                            size = Random.nextFloat() * 50f + 20f,
                            color = Color(
                                red = Random.nextFloat(),
                                green = Random.nextFloat(),
                                blue = Random.nextFloat(),
                                alpha = 0.8f
                            ),
                            rotation = Random.nextFloat() * 360f
                        )
                        shapes = shapes + newShape
                        
                        // Limit number of shapes to prevent performance issues
                        if (shapes.size > 100) {
                            shapes = shapes.drop(shapes.size - 100)
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                shapes.forEach { shape ->
                    drawRandomShape(shape)
                }
            }
        }
    }
    
    private fun DrawScope.drawRandomShape(shape: RandomShape) {
        rotate(shape.rotation, shape.position) {
            when (shape.type) {
                ShapeType.CIRCLE -> {
                    drawCircle(
                        color = shape.color,
                        radius = shape.size,
                        center = shape.position
                    )
                }
                ShapeType.SQUARE -> {
                    drawRect(
                        color = shape.color,
                        topLeft = Offset(
                            shape.position.x - shape.size,
                            shape.position.y - shape.size
                        ),
                        size = Size(shape.size * 2, shape.size * 2)
                    )
                }
                ShapeType.TRIANGLE -> {
                    drawTriangle(shape.position, shape.size, shape.color)
                }
                ShapeType.PENTAGON -> {
                    drawPolygon(shape.position, shape.size, 5, shape.color)
                }
                ShapeType.HEXAGON -> {
                    drawPolygon(shape.position, shape.size, 6, shape.color)
                }
                ShapeType.STAR -> {
                    drawStar(shape.position, shape.size, shape.color)
                }
            }
        }
    }
    
    private fun DrawScope.drawTriangle(center: Offset, size: Float, color: Color) {
        val path = Path()
        val height = size * 1.5f
        
        path.moveTo(center.x, center.y - height / 2)
        path.lineTo(center.x - size, center.y + height / 2)
        path.lineTo(center.x + size, center.y + height / 2)
        path.close()
        
        drawPath(path, color)
    }
    
    private fun DrawScope.drawPolygon(center: Offset, size: Float, sides: Int, color: Color) {
        val path = Path()
        val angleStep = 2 * kotlin.math.PI / sides
        
        for (i in 0 until sides) {
            val angle = i * angleStep
            val x = center.x + size * cos(angle).toFloat()
            val y = center.y + size * sin(angle).toFloat()
            
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        
        drawPath(path, color)
    }
    
    private fun DrawScope.drawStar(center: Offset, size: Float, color: Color) {
        val path = Path()
        val outerRadius = size
        val innerRadius = size * 0.4f
        val angleStep = kotlin.math.PI / 5
        
        for (i in 0 until 10) {
            val angle = i * angleStep
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        
        drawPath(path, color)
    }
}