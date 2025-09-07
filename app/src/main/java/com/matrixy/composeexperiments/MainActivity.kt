package com.matrixy.composeexperiments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.matrixy.composeexperiments.navigation.ComposeExperimentsApp
import com.matrixy.composeexperiments.ui.theme.ComposeExperimentsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeExperimentsTheme {
                ComposeExperimentsApp(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}