package com.matrixy.compose_experiments.experiments.drag_to_delete

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.matrixy.compose_experiments.R
import com.matrixy.compose_experiments.data.ExperimentContent
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Drag to Delete Experiment
 * 
 * Recreates the SwiftUI drag to delete experiment using Jetpack Compose.
 * Features:
 * - Multiple draggable app icons (1-3 randomly generated)
 * - Trash circle that appears when dragging starts
 * - Size animations based on proximity to trash circle
 * - Delete animation when icon is dropped on trash
 * - Auto-replenishment when total icons < 2
 */

data class AppIcon(
    val id: String = UUID.randomUUID().toString(),
    val initialPosition: Offset,
    val isVisible: Boolean = true
)
class DragToDeleteExperimentContent : ExperimentContent {
    
    @Composable
    override fun Content(modifier: Modifier) {
        DragToDeleteScreen(modifier)
    }
}

@Composable
private fun DragToDeleteScreen(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    
    // Generate initial random icons
    fun generateRandomPosition(): Offset {
        return Offset(
            x = Random.nextFloat() * (screenWidth - 200f) + 100f, // Keep away from edges
            y = Random.nextFloat() * (screenHeight * 0.4f) + 100f // Keep in upper area
        )
    }
    
    fun createNewIcon(): AppIcon {
        return AppIcon(initialPosition = generateRandomPosition())
    }
    
    // Initialize with 1-3 random icons
    val initialIconCount = Random.nextInt(1, 4)
    var appIcons by remember { 
        mutableStateOf(
            (1..initialIconCount).map { createNewIcon() }
        )
    }
    
    var showCircle by remember { mutableStateOf(false) }
    var circleSize by remember { mutableStateOf(120f) }
    var initialCircleGrowth by remember { mutableStateOf(false) }
    var shrinkCircle by remember { mutableStateOf(false) }
    var currentDraggingId by remember { mutableStateOf<String?>(null) }
    
    // Separate position and size tracking for smooth dragging
    val iconPositions = remember { mutableMapOf<String, Offset>() }
    val iconSizes = remember { mutableMapOf<String, Float>() }
    
    // Initialize positions and sizes for all icons
    LaunchedEffect(appIcons) {
        appIcons.forEach { icon ->
            if (iconPositions[icon.id] == null) {
                iconPositions[icon.id] = icon.initialPosition
                iconSizes[icon.id] = 160f
            }
        }
    }
    
    val animatedCircleSize by animateFloatAsState(
        targetValue = circleSize,
        animationSpec = tween(300),
        label = "circleSize"
    )
    
    val circleScale by animateFloatAsState(
        targetValue = when {
            initialCircleGrowth -> 0f
            shrinkCircle -> 0f
            else -> 1f
        },
        animationSpec = tween(if (shrinkCircle) 500 else 300),
        label = "circleScale"
    )
    
    // Function to add new icon if needed
    fun addIconIfNeeded() {
        val visibleCount = appIcons.count { it.isVisible }
        if (visibleCount < 2) {
            val newIcon = createNewIcon()
            appIcons = appIcons + newIcon
            // Initialize position and size for new icon immediately
            iconPositions[newIcon.id] = newIcon.initialPosition
            iconSizes[newIcon.id] = 160f
        }
    }
    
    // Function to delete an icon and schedule replenishment
    fun deleteIcon(iconId: String) {
        // Update only the specific icon to avoid unnecessary recomposition
        val iconIndex = appIcons.indexOfFirst { it.id == iconId }
        if (iconIndex != -1) {
            val updatedList = appIcons.toMutableList()
            updatedList[iconIndex] = updatedList[iconIndex].copy(isVisible = false)
            appIcons = updatedList
        }
    }
    
    LaunchedEffect(initialCircleGrowth) {
        if (initialCircleGrowth) {
            delay(300)
            initialCircleGrowth = false
        }
    }
    
    // Handle replenishment after deletions with stable key
    val visibleIconCount = appIcons.count { it.isVisible }
    LaunchedEffect(visibleIconCount) {
        if (visibleIconCount < 2 && visibleIconCount > 0) {
            delay(1000) // Wait a bit after deletion animation
            addIconIfNeeded()
        }
    }
    
    LaunchedEffect(shrinkCircle) {
        if (shrinkCircle) {
            delay(500)
            showCircle = false
            shrinkCircle = false
            currentDraggingId = null
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        
        if (showCircle) {
            TrashCircle(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-50).dp),
                size = animatedCircleSize,
                scale = circleScale
            )
        }
        
        // Render all app icons with stable keys
        for (icon in appIcons) {
            if (icon.isVisible) {
                val currentPosition = iconPositions[icon.id] ?: icon.initialPosition
                val currentSize = iconSizes[icon.id] ?: 160f
                
                key(icon.id) {
                    DraggableAppIcon(
                        iconId = icon.id,
                        position = currentPosition,
                        size = currentSize,
                        screenWidth = screenWidth,
                        screenHeight = screenHeight,
                        onDragStart = { iconId ->
                            currentDraggingId = iconId
                            if (!showCircle) {
                                showCircle = true
                                initialCircleGrowth = true
                            }
                        },
                        onDrag = { iconId, newPosition ->
                            // Update position without triggering recomposition of appIcons
                            iconPositions[iconId] = newPosition
                            
                            // Update trash can size and icon size based on proximity
                            val trashCenterY = screenHeight - 100f
                            val distance = trashCenterY - newPosition.y
                            val maxDistance = screenHeight * 0.6f
                            
                            if (distance > 0) {
                                val proportion = min(1f, max(0f, (maxDistance - distance) / maxDistance))
                                circleSize = 120f + (60f * proportion)
                                iconSizes[iconId] = 160f - (120f * proportion)
                            } else {
                                circleSize = 180f
                                iconSizes[iconId] = 40f
                            }
                        },
                        onDragEnd = { iconId ->
                            if (circleSize > 160f) {
                                // Delete this icon
                                deleteIcon(iconId)
                                iconPositions.remove(iconId)
                                iconSizes.remove(iconId)
                                shrinkCircle = true
                            } else {
                                // Reset icon to normal state
                                iconSizes[iconId] = 160f
                                showCircle = false
                                circleSize = 120f
                                currentDraggingId = null
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DraggableAppIcon(
    iconId: String,
    position: Offset,
    size: Float,
    screenWidth: Float,
    screenHeight: Float,
    onDragStart: (String) -> Unit,
    onDrag: (String, Offset) -> Unit,
    onDragEnd: (String) -> Unit
) {
    val density = LocalDensity.current
    var currentPosition by remember(iconId) { mutableStateOf(position) }
    
    val animatedSize by animateFloatAsState(
        targetValue = size,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "iconSize_$iconId"
    )
    
    // Update position when it changes from parent (only if different)
    LaunchedEffect(position) {
        if (currentPosition != position) {
            currentPosition = position
        }
    }
    
    Image(
        painter = painterResource(id = R.drawable.ic_app_icon_colorful),
        contentDescription = "App Icon",
        modifier = Modifier
            .size(with(density) { animatedSize.toDp() })
            .offset {
                IntOffset(
                    x = (currentPosition.x - animatedSize / 2f).roundToInt(),
                    y = (currentPosition.y - animatedSize / 2f).roundToInt()
                )
            }
            .clip(RoundedCornerShape(with(density) { (animatedSize * 0.2f).toDp() }))
            .pointerInput(iconId) {
                detectDragGestures(
                    onDragStart = { _ ->
                        onDragStart(iconId)
                    },
                    onDrag = { _, dragAmount ->
                        val newPosition = Offset(
                            x = (currentPosition.x + dragAmount.x).coerceIn(0f, screenWidth),
                            y = (currentPosition.y + dragAmount.y).coerceIn(0f, screenHeight)
                        )
                        currentPosition = newPosition
                        onDrag(iconId, newPosition)
                    },
                    onDragEnd = {
                        onDragEnd(iconId)
                    }
                )
            },
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TrashCircle(
    modifier: Modifier = Modifier,
    size: Float,
    scale: Float
) {
    Box(
        modifier = modifier
            .offset(y = (-46).dp)
            .size(with(LocalDensity.current) { size.toDp() })
            .scale(scale)
            .clip(CircleShape)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_trash_can),
            contentDescription = "Delete",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}

