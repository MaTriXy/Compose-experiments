package com.matrixy.composeexperiments.experiments.particle_text

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ParticleTextScreen() {
    var buttonOffset by remember { mutableStateOf(200f) }

    // Button animation
    val buttonOffsetAnim by animateFloatAsState(
        targetValue = buttonOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Particle text using the reusable component
            ParticleText(
                text = "Welcome",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                config = ParticleTextConfig(
                    particleSize = 2f,
                    particleSpacing = 3f,
                    particleColor = Color.White.copy(alpha = 0.8f),
                    animationSpeed = 0.1f,
                    explosionForce = 1000f,
                    explosionRadius = 30f,
                    fontSize = 80f,
                    initialDelay = 2000L,
                    enableTouchExplosion = true
                ),
                onAnimationComplete = {
                    // Show button after particles assemble
                    buttonOffset = 0f
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Get Started button with animated gradient border
            Box(
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .offset(y = buttonOffsetAnim.dp)
            ) {
                Button(
                    onClick = { /* Action here */ },
                    modifier = Modifier
                        .width(240.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF262626)
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                // Animated gradient border
                Canvas(
                    modifier = Modifier
                        .width(240.dp)
                        .height(60.dp)
                ) {
                    val brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Red,
                            Color(0xFFFFA500), // Orange
                            Color.Yellow,
                            Color.Green,
                            Color.Blue,
                            Color(0xFF800080), // Purple
                            Color.Red
                        ),
                        center = center
                    )

                    drawRoundRect(
                        brush = brush,
                        topLeft = Offset.Zero,
                        size = size,
                        cornerRadius = CornerRadius(30.dp.toPx()),
                        style = Stroke(width = 5f)
                    )
                }
            }
        }
    }
}
