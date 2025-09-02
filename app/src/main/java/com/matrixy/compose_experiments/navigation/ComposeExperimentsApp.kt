package com.matrixy.compose_experiments.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.matrixy.compose_experiments.data.Experiment
import com.matrixy.compose_experiments.data.ExperimentCategory
import com.matrixy.compose_experiments.data.ExperimentDifficulty
import com.matrixy.compose_experiments.data.ExperimentRegistry
import com.matrixy.compose_experiments.screens.LicensesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeExperimentsApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var isRegistryInitialized by remember { mutableStateOf(false) }
    
    // Initialize experiment registry
    LaunchedEffect(Unit) {
        ExperimentRegistry.initialize()
        isRegistryInitialized = true
    }
    
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            var showMenu by remember { mutableStateOf(false) }
            
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Compose Experiments") },
                        actions = {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options"
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Open Source Licenses") },
                                    onClick = {
                                        showMenu = false
                                        navController.navigate("licenses")
                                    }
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                ExperimentsList(
                    onExperimentClick = { experiment ->
                        navController.navigate("experiment/${experiment.metadata.id}")
                    },
                    isRegistryInitialized = isRegistryInitialized,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
        
        composable("experiment/{experimentId}") { backStackEntry ->
            val experimentId = backStackEntry.arguments?.getString("experimentId")
            val experiment = experimentId?.let { ExperimentRegistry.getExperiment(it) }
            
            if (experiment != null) {
                ExperimentScreen(
                    experiment = experiment,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                // Handle experiment not found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Experiment not found")
                }
            }
        }
        
        composable("licenses") {
            LicensesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun ExperimentsList(
    onExperimentClick: (Experiment) -> Unit,
    isRegistryInitialized: Boolean,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExperimentCategory?>(null) }
    
    val allExperiments = if (isRegistryInitialized) {
        ExperimentRegistry.getAllExperiments()
    } else {
        emptyList()
    }
    
    val filteredExperiments = remember(searchQuery, selectedCategory, isRegistryInitialized) {
        var experiments = allExperiments
        
        // Apply search filter
        if (searchQuery.isNotBlank()) {
            experiments = ExperimentRegistry.searchExperiments(searchQuery)
        }
        
        // Apply category filter
        if (selectedCategory != null) {
            experiments = experiments.filter { it.metadata.category == selectedCategory }
        }
        
        experiments.sortedBy { it.metadata.displayName }
    }
    
    Column(modifier = modifier) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search experiments") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        // Category Filter Chips
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(ExperimentCategory.values()) { category ->
                FilterChip(
                    onClick = {
                        selectedCategory = if (selectedCategory == category) null else category
                    },
                    label = { Text(category.displayName) },
                    selected = selectedCategory == category,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        
        
        // Results Summary
        Text(
            text = "${filteredExperiments.size} experiments found",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Experiments List
        if (filteredExperiments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No experiments found",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Try adjusting your search or filters",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn {
                items(filteredExperiments) { experiment ->
                    ExperimentCard(
                        experiment = experiment,
                        onClick = { onExperimentClick(experiment) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentCard(
    experiment: Experiment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = experiment.metadata.displayName,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = experiment.metadata.category.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            Text(
                text = experiment.metadata.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // Tags and difficulty
            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = experiment.metadata.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.CenterVertically)
                )
                
                // Show first few tags
                experiment.metadata.tags.take(3).forEach { tag ->
                    Text(
                        text = "#$tag",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentScreen(
    experiment: Experiment,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(experiment.metadata.displayName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        }
    ) { innerPadding ->
        experiment.content.Content(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}