package com.matrixy.composeexperiments.experiments.drag_transform

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.matrixy.composeexperiments.R
import com.matrixy.composeexperiments.data.ExperimentContent
import kotlin.math.roundToInt

// Dock dimensions - easily configurable at the top
private const val DOCK_WIDTH = 750f  // Width that properly fits 4 icons
private const val DOCK_HEIGHT = 160f  // Height for the dock
private const val DOCK_WIDTH_SIDE = 160f  // Width when docked on sides
private const val DOCK_HEIGHT_SIDE = 750f  // Height when docked on sides
private const val DRAGGING_SIZE = 160f  // Size when dragging

// Icon dimensions and spacing
private const val ICON_SIZE_DOCKED = 48f  // Icon size in dock (dp) - optimized for 4 icons
private const val ICON_SIZE_DRAGGING = 52f  // Icon size when dragging (dp)
private const val ICON_SPACING = 6f  // Spacing between icons (dp) - optimized for 4 icons

// Visual properties
private const val CORNER_RADIUS_DOCKED = 40f
private const val CORNER_RADIUS_DRAGGING = 48f
private const val SHADOW_RADIUS = 10f
private const val BLUR_RADIUS = 20f
private const val DOCK_OPACITY = 0.5f
private const val BORDER_WIDTH = 0.8f
private const val BORDER_OPACITY = 0.3f

// Positioning
private const val DOCK_MARGIN = 60f  // Margin from screen edges

enum class Edge {
    BOTTOM, LEADING, TRAILING
}

class DragTransformExperimentContent : ExperimentContent {
    @Composable
    override fun Content(modifier: Modifier) {
        DraggableShape(modifier)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun DraggableShape(modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        // Get screen dimensions
        val screenWidth = with(density) { maxWidth.toPx() }
        val screenHeight = with(density) { maxHeight.toPx() }

        // State variables
        var position by remember {
            mutableStateOf(
                Offset(
                    x = screenWidth / 2,
                    y = screenHeight - 100f
                )
            )
        }
        var isDragging by remember { mutableStateOf(false) }
        var dockedEdge by remember { mutableStateOf(Edge.BOTTOM) }

        val iconNames = listOf(
            R.drawable.app_gemini,
            R.drawable.app_chrome,
            R.drawable.app_photos,
            R.drawable.app_calendar
        )

        // Background image
        Image(
            painter = painterResource(id = R.drawable.ocean_bg),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Calculate sizes based on state - using global constants
        val dockedSize = if (dockedEdge == Edge.BOTTOM) {
            Size(DOCK_WIDTH, DOCK_HEIGHT)
        } else {
            Size(DOCK_WIDTH_SIDE, DOCK_HEIGHT_SIDE)
        }
        val draggingSize = Size(DRAGGING_SIZE, DRAGGING_SIZE)

        val targetSize by animateSizeAsState(
            targetValue = if (isDragging) draggingSize else dockedSize,
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "Size animation"
        )

        val cornerRadius by animateFloatAsState(
            targetValue = if (isDragging) CORNER_RADIUS_DRAGGING else CORNER_RADIUS_DOCKED,
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "Corner radius animation"
        )

        val shadowRadius by animateFloatAsState(
            targetValue = if (isDragging) SHADOW_RADIUS else 0f,
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "Shadow animation"
        )

        // Calculate target position
        val targetPosition = if (isDragging) {
            position
        } else {
            getDockedPosition(dockedEdge, screenWidth, screenHeight)
        }

        val animatedPosition by animateFloatAsState(
            targetValue = targetPosition.x,
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "Position X animation"
        )

        val animatedPositionY by animateFloatAsState(
            targetValue = targetPosition.y,
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "Position Y animation"
        )

        // Draggable dock container
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (animatedPosition - targetSize.width / 2).roundToInt(),
                        (animatedPositionY - targetSize.height / 2).roundToInt()
                    )
                }
                .size(
                    width = with(density) { targetSize.width.toDp() },
                    height = with(density) { targetSize.height.toDp() }
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            position = Offset(animatedPosition, animatedPositionY)
                        },
                        onDragEnd = {
                            isDragging = false
                            dockedEdge = findClosestEdge(position, screenWidth, screenHeight)
                        },
                        onDrag = { _, dragAmount ->
                            position = Offset(
                                x = position.x + dragAmount.x,
                                y = position.y + dragAmount.y
                            )
                        }
                    )
                }
        ) {
            // Background blur layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = with(density) { shadowRadius.toDp() },
                        shape = RoundedCornerShape(with(density) { cornerRadius.toDp() }),
                        clip = false
                    )
                    .clip(RoundedCornerShape(with(density) { cornerRadius.toDp() }))
                    .blur(
                        radius = with(density) { BLUR_RADIUS.toDp() },
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    )
                    .background(Color.White.copy(alpha = 0.01f))
            )
            
            // Glass dock container (without blur on content)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(with(density) { cornerRadius.toDp() }))
                    .background(
                        Color.Black.copy(alpha = DOCK_OPACITY)
                    )
                    .border(
                        width = BORDER_WIDTH.dp,
                        color = Color.White.copy(alpha = BORDER_OPACITY),
                        shape = RoundedCornerShape(with(density) { cornerRadius.toDp() })
                    ),
                contentAlignment = Alignment.Center
            ) {
            // App icons
            if (isDragging) {
                // Show only first icon when dragging
                Image(
                    painter = painterResource(id = iconNames[0]),
                    contentDescription = "App icon",
                    modifier = Modifier.size(ICON_SIZE_DRAGGING.dp)
                )
            } else {
                // Layout based on docked edge
                when (dockedEdge) {
                    Edge.BOTTOM -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ICON_SPACING.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            iconNames.forEach { iconRes ->
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = "App icon",
                                    modifier = Modifier.size(ICON_SIZE_DOCKED.dp)
                                )
                            }
                        }
                    }
                    Edge.LEADING, Edge.TRAILING -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(ICON_SPACING.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            iconNames.forEach { iconRes ->
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = "App icon",
                                    modifier = Modifier.size(ICON_SIZE_DOCKED.dp)
                                )
                            }
                        }
                    }
                }
            }
            }
        }
    }
}

private fun getDockedPosition(edge: Edge, screenWidth: Float, screenHeight: Float): Offset {
    return when (edge) {
        Edge.BOTTOM -> Offset(
            x = screenWidth / 2,
            y = screenHeight - DOCK_MARGIN - 41f
        )
        Edge.LEADING -> Offset(
            x = DOCK_MARGIN + 41f,
            y = screenHeight / 2
        )
        Edge.TRAILING -> Offset(
            x = screenWidth - DOCK_MARGIN - 41f,
            y = screenHeight / 2
        )
    }
}

private fun findClosestEdge(position: Offset, screenWidth: Float, screenHeight: Float): Edge {
    val distances = mapOf(
        Edge.BOTTOM to (screenHeight - position.y),
        Edge.LEADING to position.x,
        Edge.TRAILING to (screenWidth - position.x)
    )

    return distances.minByOrNull { it.value }?.key ?: Edge.BOTTOM
}