package com.matrixy.compose_experiments.experiments.calculator_metric

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixy.compose_experiments.data.ExperimentContent

data class Conversion(
    val label: String,
    val value: Double
)

/**
 * Calculator Metric Experiment
 * 
 * A prototype that converts numbers to the metric system.
 * This recreates the SwiftUI calculator metric experiment using Jetpack Compose.
 * 
 * Features implemented:
 * - Interactive unit conversion interface
 * - Multiple conversion types (temperature, weight, distance)
 * - Real-time conversion with smooth number animations
 * - Long press to toggle between conversion directions
 * - Tap to hide keyboard functionality
 */
class CalculatorMetricExperimentContent : ExperimentContent {
    
    @Composable
    override fun Content(modifier: Modifier) {
        var inputNumber by remember { mutableStateOf("") }
        var hasInputChanged by remember { mutableStateOf(false) }
        var isReversed by remember { mutableStateOf(false) }
        val keyboardController = LocalSoftwareKeyboardController.current
        
        // Get conversions based on current direction
        val conversions = remember(inputNumber, isReversed) {
            getConversions(inputNumber, isReversed)
        }
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures {
                        keyboardController?.hide()
                    }
                }
        ) {
            // Input field
            OutlinedTextField(
                value = inputNumber,
                onValueChange = { newValue ->
                    inputNumber = newValue
                    hasInputChanged = !hasInputChanged
                },
                label = { Text("Enter a number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )
            )
            
            // Conversions list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(conversions) { conversion ->
                    ConversionView(
                        conversion = conversion,
                        hasInputChanged = hasInputChanged,
                        onLongPress = {
                            isReversed = !isReversed
                        }
                    )
                }
            }
        }
    }
    
    private fun getConversions(inputNumber: String, isReversed: Boolean): List<Conversion> {
        // If input is empty or invalid, show 0 for all conversions
        val number = if (inputNumber.isBlank()) 0.0 else inputNumber.toDoubleOrNull() ?: 0.0
        
        return if (isReversed) {
            listOf(
                Conversion("°C to °F", if (inputNumber.isBlank()) 0.0 else celsiusToFahrenheit(number)),
                Conversion("Kilograms to Pounds", if (inputNumber.isBlank()) 0.0 else kilogramsToPounds(number)),
                Conversion("Kilometers to Miles", if (inputNumber.isBlank()) 0.0 else kilometersToMiles(number)),
                Conversion("Meters to Feet", if (inputNumber.isBlank()) 0.0 else metersToFeet(number)),
                Conversion("Centimeters to Inches", if (inputNumber.isBlank()) 0.0 else centimetersToInches(number))
            )
        } else {
            listOf(
                Conversion("°F to °C", if (inputNumber.isBlank()) 0.0 else fahrenheitToCelsius(number)),
                Conversion("Pounds to Kilograms", if (inputNumber.isBlank()) 0.0 else poundsToKilograms(number)),
                Conversion("Miles to Kilometers", if (inputNumber.isBlank()) 0.0 else milesToKilometers(number)),
                Conversion("Feet to Meters", if (inputNumber.isBlank()) 0.0 else feetToMeters(number)),
                Conversion("Inches to Centimeters", if (inputNumber.isBlank()) 0.0 else inchesToCentimeters(number))
            )
        }
    }
    
    // Temperature conversions
    private fun fahrenheitToCelsius(fahrenheit: Double): Double = (fahrenheit - 32) * 5 / 9
    private fun celsiusToFahrenheit(celsius: Double): Double = (celsius * 9 / 5) + 32
    
    // Weight conversions
    private fun poundsToKilograms(pounds: Double): Double = pounds * 0.45359237
    private fun kilogramsToPounds(kilograms: Double): Double = kilograms / 0.45359237
    
    // Distance conversions
    private fun milesToKilometers(miles: Double): Double = miles * 1.60934
    private fun kilometersToMiles(kilometers: Double): Double = kilometers / 1.60934
    private fun feetToMeters(feet: Double): Double = feet * 0.3048
    private fun metersToFeet(meters: Double): Double = meters / 0.3048
    private fun inchesToCentimeters(inches: Double): Double = inches * 2.54
    private fun centimetersToInches(centimeters: Double): Double = centimeters / 2.54
    
    private fun formatNumber(number: Double): String {
        return if (number % 1.0 == 0.0) {
            String.format("%.0f", number)
        } else {
            String.format("%.2f", number)
        }
    }
}

@Composable
fun ConversionView(
    conversion: Conversion,
    hasInputChanged: Boolean,
    onLongPress: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (hasInputChanged) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "scale_animation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = conversion.label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = formatNumber(conversion.value),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color(0xFF00E075), // Green color from SwiftUI version
                textAlign = TextAlign.End,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

private fun formatNumber(number: Double): String {
    return if (number % 1.0 == 0.0) {
        String.format("%.0f", number)
    } else {
        String.format("%.2f", number)
    }
}