package com.matrixy.compose_experiments.data

data class AssetCredit(
    val assetName: String,
    val assetType: AssetType,
    val author: String,
    val source: String?,
    val license: String,
    val url: String?,
    val experimentId: String? = null
)

enum class AssetType {
    IMAGE,
    ICON,
    SOUND,
    FONT,
    VIDEO,
    OTHER
}

object CreditsRegistry {
    private val credits = mutableListOf<AssetCredit>()
    
    init {
        loadCredits()
    }
    
    private fun loadCredits() {
        credits.addAll(listOf(
            AssetCredit(
                assetName = "Google App Icons (Gmail, Maps, Photos, etc.)",
                assetType = AssetType.ICON,
                author = "Google LLC",
                source = "Google Play Store - Official Google Apps",
                license = "© Google LLC - All rights reserved. These icons are proprietary to Google and used here solely for demonstration purposes. No ownership or rights are claimed.",
                url = "https://play.google.com/store/apps/dev?id=5700313618786177705",
                experimentId = "drag_transform"
            ),
            AssetCredit(
                assetName = "Material Icons",
                assetType = AssetType.ICON,
                author = "Google LLC",
                source = "Material Icons",
                license = "Apache 2.0",
                url = "https://fonts.google.com/icons"
            ),
            AssetCredit(
                assetName = "Ocean Background",
                assetType = AssetType.IMAGE,
                author = "Shifaaz shamoon",
                source = "Unsplash",
                license = "Unsplash License",
                url = "https://unsplash.com/photos/ocean-wave-photography-oR0uERTVyD0",
                experimentId = "ocean_view"
            ),
            AssetCredit(
                assetName = "Material You Icons",
                assetType = AssetType.ICON,
                author = "Google LLC",
                source = "Material Design 3",
                license = "Apache 2.0",
                url = "https://m3.material.io/styles/icons/overview"
            ),
            AssetCredit(
                assetName = "Sample Avatar Images",
                assetType = AssetType.IMAGE,
                author = "UI Faces",
                source = "UI Faces",
                license = "Creative Commons",
                url = "https://uifaces.co",
                experimentId = "avatar_stack"
            ),
            AssetCredit(
                assetName = "Nature Photography",
                assetType = AssetType.IMAGE,
                author = "Pexels Contributors",
                source = "Pexels",
                license = "Pexels License",
                url = "https://www.pexels.com",
                experimentId = "photo_gallery"
            ),
            AssetCredit(
                assetName = "Gradient Backgrounds",
                assetType = AssetType.IMAGE,
                author = "Generated",
                source = "In-app generated",
                license = "MIT",
                url = null
            )
        ))
    }
    
    fun getAllCredits(): List<AssetCredit> = credits.toList()
    
    fun getCreditsForExperiment(experimentId: String): List<AssetCredit> {
        return credits.filter { it.experimentId == experimentId }
    }
    
    fun getCreditsByType(type: AssetType): List<AssetCredit> {
        return credits.filter { it.assetType == type }
    }
    
    fun addCredit(credit: AssetCredit) {
        credits.add(credit)
    }
    
    fun addCredits(newCredits: List<AssetCredit>) {
        credits.addAll(newCredits)
    }
}