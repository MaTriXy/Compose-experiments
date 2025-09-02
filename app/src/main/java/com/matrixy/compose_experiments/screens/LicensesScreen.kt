package com.matrixy.compose_experiments.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.content.Context
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.matrixy.compose_experiments.data.AssetCredit
import com.matrixy.compose_experiments.data.AssetType
import com.matrixy.compose_experiments.data.CreditsRegistry

data class License(
    val name: String,
    val url: String,
    val licenseType: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    
    val licenses = remember {
        listOf(
            License(
                name = "Jetpack Compose",
                url = "https://developer.android.com/jetpack/androidx/releases/compose",
                licenseType = "Apache 2.0"
            ),
            License(
                name = "Material 3",
                url = "https://m3.material.io/",
                licenseType = "Apache 2.0"
            ),
            License(
                name = "Kotlin",
                url = "https://kotlinlang.org/",
                licenseType = "Apache 2.0"
            ),
            License(
                name = "AndroidX Core",
                url = "https://developer.android.com/jetpack/androidx",
                licenseType = "Apache 2.0"
            ),
            License(
                name = "AndroidX Navigation",
                url = "https://developer.android.com/jetpack/androidx/releases/navigation",
                licenseType = "Apache 2.0"
            ),
            License(
                name = "AndroidX Lifecycle",
                url = "https://developer.android.com/jetpack/androidx/releases/lifecycle",
                licenseType = "Apache 2.0"
            ),
            License(
                name = "Coil",
                url = "https://github.com/coil-kt/coil",
                licenseType = "Apache 2.0"
            ),
            License(
                name = "Google Maps Compose",
                url = "https://github.com/googlemaps/android-maps-compose",
                licenseType = "Apache 2.0"
            )
        )
    }
    
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Licenses & Credits") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Open Source") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Asset Credits") }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> OpenSourceLicensesTab(
                licenses = licenses,
                context = context,
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            1 -> AssetCreditsTab(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseItem(
    license: License,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = license.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = license.licenseType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "→",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun OpenSourceLicensesTab(
    licenses: List<License>,
    context: Context,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "About Open Source Licenses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This app uses the following open source libraries and components. Tap on any item to view more details.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://developers.google.com/android/guides/opensource")
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("View Google Play Services Licenses")
                }
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(licenses) { license ->
                LicenseItem(
                    license = license,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(license.url)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun AssetCreditsTab(
    modifier: Modifier = Modifier
) {
    val credits = remember { CreditsRegistry.getAllCredits() }
    val groupedCredits = remember { credits.groupBy { it.assetType } }
    
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Asset Credits",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We gratefully acknowledge the creators of the assets used in this app:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        groupedCredits.forEach { (type, credits) ->
            item {
                Text(
                    text = type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            items(credits) { credit ->
                AssetCreditItem(credit = credit)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetCreditItem(
    credit: AssetCredit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        onClick = {
            credit.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                }
                context.startActivity(intent)
            }
        },
        enabled = credit.url != null,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = credit.assetName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "by ${credit.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    credit.source?.let {
                        Text(
                            text = "Source: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "License: ${credit.license}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    credit.experimentId?.let {
                        Text(
                            text = "Used in: $it",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                if (credit.url != null) {
                    Text(
                        text = "→",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}