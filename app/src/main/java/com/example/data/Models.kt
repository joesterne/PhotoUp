package com.example.data

/**
 * Represents a photo loaded from the library or device gallery.
 */
data class PhotoItem(
    val id: String,
    val title: String,
    val uriString: String? = null,
    val category: String, // Portrait, Landscape, Urban, Nature, Pet, Architecture
    val aestheticScore: Float, // 0.0f .. 1.0f (auto-curation heuristic)
    val sharpnessScore: Float, // 0.0f .. 1.0f
    val lightingScore: Float, // 0.0f .. 1.0f
    val dynamicRangeScore: Float, // 0.0f .. 1.0f
    val dateAdded: String,
    val isAutoSelected: Boolean = false,
    val sampleGradientStart: Long = 0xFF4338CA,
    val sampleGradientEnd: Long = 0xFF06B6D4,
    val samplePatternType: String = "portrait"
)

/**
 * Popular AI art styles for photo transformation.
 */
data class StylePreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val promptModifiers: String,
    val primaryColor: Long,
    val secondaryColor: Long,
    val iconKey: String,
    val tag: String
)

/**
 * AI Providers supported by the application.
 */
enum class AIProvider(
    val displayName: String,
    val providerBadge: String,
    val defaultModel: String,
    val availableModels: List<String>,
    val description: String,
    val isCloud: Boolean
) {
    GEMINI(
        displayName = "Google Gemini",
        providerBadge = "Gemini AI",
        defaultModel = "gemini-2.5-flash-image",
        availableModels = listOf("gemini-2.5-flash-image", "gemini-3.5-flash"),
        description = "High-fidelity multimodal image generation & contextual styling with Google's latest preview model.",
        isCloud = true
    ),
    OPENAI(
        displayName = "OpenAI DALL·E",
        providerBadge = "DALL·E 3",
        defaultModel = "dall-e-3",
        availableModels = listOf("dall-e-3", "gpt-4o-image"),
        description = "Hyper-detailed artistic diffusion with natural language prompt expansion.",
        isCloud = true
    ),
    STABILITY(
        displayName = "Stability AI",
        providerBadge = "Stable Diffusion",
        defaultModel = "stable-diffusion-3.5-large",
        availableModels = listOf("stable-diffusion-3.5-large", "sdxl-turbo"),
        description = "Photorealistic neural rendering with precise lighting and texture synthesis.",
        isCloud = true
    ),
    LOCAL_NEURAL(
        displayName = "Local Neural Engine",
        providerBadge = "On-Device",
        defaultModel = "edge-artistic-shader-v2",
        availableModels = listOf("edge-artistic-shader-v2", "fast-neural-transfer"),
        description = "Instant offline processing using accelerated local artistic shaders and color matrix transforms.",
        isCloud = false
    )
}

/**
 * User configuration for the AI generation pipeline.
 */
data class AIProviderConfig(
    val activeProvider: AIProvider = AIProvider.GEMINI,
    val selectedModel: String = AIProvider.GEMINI.defaultModel,
    val stylizationStrength: Float = 0.85f, // 0.1 .. 1.0
    val enablePromptEnhancer: Boolean = true,
    val customApiKey: String = ""
)

/**
 * Cloud and Local storage sync configurations.
 */
data class StorageConfig(
    val isCloudSyncEnabled: Boolean = true,
    val cloudProviderName: String = "Google Drive",
    val cloudFolderPath: String = "Google Drive/StyleMorph/Variations",
    val localFolderPath: String = "Internal Storage/Pictures/StyleMorph",
    val autoSyncOnGenerate: Boolean = true,
    val localStorageUsedMb: Float = 42.8f,
    val cloudStorageUsedMb: Float = 128.4f
)

/**
 * Result of an AI-generated style variation.
 */
data class VariationResult(
    val id: String,
    val photoId: String,
    val photoTitle: String,
    val styleId: String,
    val styleName: String,
    val provider: AIProvider,
    val modelUsed: String,
    val promptUsed: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSavedLocally: Boolean = true,
    val isSyncedToCloud: Boolean = false,
    val localFilePath: String? = null,
    val cloudSyncTime: Long? = null,
    val primaryColor: Long = 0xFF7C3AED,
    val secondaryColor: Long = 0xFF06B6D4
)

/**
 * Active processing task for determinate and ongoing progress tracking.
 */
data class BatchTask(
    val id: String,
    val title: String,
    val styleName: String,
    val providerName: String,
    val totalItems: Int,
    val completedItems: Int,
    val currentPhotoTitle: String,
    val currentStepDescription: String,
    val progress: Float, // 0.0f .. 1.0f
    val isRunning: Boolean,
    val isCancelled: Boolean = false,
    val startTime: Long = System.currentTimeMillis()
)
