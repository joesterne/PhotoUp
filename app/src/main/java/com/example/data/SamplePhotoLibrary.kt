package com.example.data

object SamplePhotoLibrary {

    val popularStyles = listOf(
        StylePreset(
            id = "cyberpunk",
            name = "Cyberpunk Neon",
            subtitle = "Futuristic Night & Holograms",
            description = "Rain-slicked asphalt, vibrant neon cyan & hot magenta edge lighting, reflective surfaces, and high contrast futuristic cityscape aesthetics.",
            promptModifiers = "cyberpunk style, neon cyan and magenta lighting, rain reflections, volumetric fog, futuristic details, ray-traced reflections",
            primaryColor = 0xFF06B6D4,
            secondaryColor = 0xFFEC4899,
            iconKey = "bolt",
            tag = "Trending #1"
        ),
        StylePreset(
            id = "anime_ghibli",
            name = "Studio Anime",
            subtitle = "Ghibli Nostalgia & Hand-Painted",
            description = "Soft painterly clouds, dreamy pastel color grading, warm golden hour sunbeams, and picturesque traditional cell animation textures.",
            promptModifiers = "Studio Ghibli aesthetic, anime background art, vibrant watercolor clouds, lush greens, gentle cel shading, Makoto Shinkai lighting",
            primaryColor = 0xFF10B981,
            secondaryColor = 0xFF38BDF8,
            iconKey = "palette",
            tag = "Fan Favorite"
        ),
        StylePreset(
            id = "renaissance",
            name = "Renaissance Oil",
            subtitle = "Classical Masterpiece Impasto",
            description = "Dramatic chiaroscuro shadow contrasts, rich textured canvas brushstrokes, warm amber glaze, and timeless museum fine-art lighting.",
            promptModifiers = "Renaissance oil painting, chiaroscuro lighting, Rembrandt palette, rich canvas texture, classical masterpiece brushwork, cracked lacquer glaze",
            primaryColor = 0xFFD97706,
            secondaryColor = 0xFF78350F,
            iconKey = "brush",
            tag = "Classical"
        ),
        StylePreset(
            id = "pixar_3d",
            name = "3D Claymation",
            subtitle = "Pixar Whimsy & Tactile Depth",
            description = "Smooth tactile clay surfaces, charming volumetric subsurface scattering, soft ambient shadows, and cheerful animated film appeal.",
            promptModifiers = "3D Pixar render style, claymation plasticine texture, warm ambient occlusion, cute character proportions, Octane 3D lighting, tilt-shift depth of field",
            primaryColor = 0xFFF59E0B,
            secondaryColor = 0xFFEF4444,
            iconKey = "smart_toy",
            tag = "Playful"
        ),
        StylePreset(
            id = "vintage_polaroid",
            name = "80s Polaroid",
            subtitle = "Retro Grain & Light Leaks",
            description = "Authentic analog film grain, gentle chemical light leaks, faded cyan/amber shadows, and nostalgic instant camera warmth.",
            promptModifiers = "Vintage Polaroid photo, 1980s color film stock, warm chemical light leak, subtle lens distortion, nostalgic retro grain, faded shadow tones",
            primaryColor = 0xFFF97316,
            secondaryColor = 0xFF8B5CF6,
            iconKey = "camera_roll",
            tag = "Nostalgic"
        ),
        StylePreset(
            id = "synthwave",
            name = "Synthwave Sunset",
            subtitle = "Retro 80s Grid & Laser Chrome",
            description = "Giant glowing neon sun, wireframe perspectives, deep purple and hot pink gradients, and slick reflective chrome typography accents.",
            promptModifiers = "Synthwave retro 80s art, neon grid horizon, giant glowing magenta sun, electric purple haze, Outrun aesthetic, chrome glow",
            primaryColor = 0xFFA855F7,
            secondaryColor = 0xFFEC4899,
            iconKey = "music_note",
            tag = "Retro Wave"
        ),
        StylePreset(
            id = "pop_art",
            name = "Comic Pop Art",
            subtitle = "Halftone Dots & Bold Inking",
            description = "Striking black ink contour lines, Ben-Day dot matrix patterns, saturated primary color blocks, and retro comic book narrative punch.",
            promptModifiers = "Roy Lichtenstein pop art, comic book screenprint, Ben-Day dots texture, high contrast ink contours, primary color screen printing",
            primaryColor = 0xFFEF4444,
            secondaryColor = 0xFFFACC15,
            iconKey = "auto_awesome",
            tag = "Bold Graphic"
        ),
        StylePreset(
            id = "watercolor",
            name = "Watercolor Dream",
            subtitle = "Fluid Pastel Splashes & Deckle Paper",
            description = "Fluid bleeding pigment washes, deckled cotton paper grain, organic paint splatters, and delicate impressionistic blending.",
            promptModifiers = "Watercolor painting on rough cold-press paper, fluid pigments bleeding into wet paper, delicate gouache splatters, pastel watercolor wash",
            primaryColor = 0xFF0EA5E9,
            secondaryColor = 0xFFA855F7,
            iconKey = "water_drop",
            tag = "Artistic"
        )
    )

    val initialPhotos = listOf(
        PhotoItem(
            id = "photo_1",
            title = "Golden Hour Portrait",
            category = "Portrait",
            aestheticScore = 0.96f,
            sharpnessScore = 0.94f,
            lightingScore = 0.98f,
            dynamicRangeScore = 0.93f,
            dateAdded = "Today, 4:20 PM",
            isAutoSelected = true,
            sampleGradientStart = 0xFFF59E0B,
            sampleGradientEnd = 0xFFEC4899,
            samplePatternType = "portrait"
        ),
        PhotoItem(
            id = "photo_2",
            title = "Neon Tokyo Alley",
            category = "Urban",
            aestheticScore = 0.94f,
            sharpnessScore = 0.92f,
            lightingScore = 0.96f,
            dynamicRangeScore = 0.95f,
            dateAdded = "Yesterday, 9:15 PM",
            isAutoSelected = true,
            sampleGradientStart = 0xFF06B6D4,
            sampleGradientEnd = 0xFF7C3AED,
            samplePatternType = "urban"
        ),
        PhotoItem(
            id = "photo_3",
            title = "Alpine Glacier Lake",
            category = "Landscape",
            aestheticScore = 0.93f,
            sharpnessScore = 0.97f,
            lightingScore = 0.91f,
            dynamicRangeScore = 0.94f,
            dateAdded = "Oct 21, 11:30 AM",
            isAutoSelected = true,
            sampleGradientStart = 0xFF0284C7,
            sampleGradientEnd = 0xFF10B981,
            samplePatternType = "landscape"
        ),
        PhotoItem(
            id = "photo_4",
            title = "Brutalist Concrete Arch",
            category = "Architecture",
            aestheticScore = 0.88f,
            sharpnessScore = 0.95f,
            lightingScore = 0.84f,
            dynamicRangeScore = 0.89f,
            dateAdded = "Oct 20, 2:45 PM",
            isAutoSelected = false,
            sampleGradientStart = 0xFF64748B,
            sampleGradientEnd = 0xFF334155,
            samplePatternType = "architecture"
        ),
        PhotoItem(
            id = "photo_5",
            title = "Emerald Forest Cascade",
            category = "Nature",
            aestheticScore = 0.95f,
            sharpnessScore = 0.91f,
            lightingScore = 0.95f,
            dynamicRangeScore = 0.96f,
            dateAdded = "Oct 19, 10:10 AM",
            isAutoSelected = true,
            sampleGradientStart = 0xFF059669,
            sampleGradientEnd = 0xFF84CC16,
            samplePatternType = "nature"
        ),
        PhotoItem(
            id = "photo_6",
            title = "Ginger Tabby Whiskers",
            category = "Pet",
            aestheticScore = 0.91f,
            sharpnessScore = 0.96f,
            lightingScore = 0.89f,
            dynamicRangeScore = 0.88f,
            dateAdded = "Oct 18, 5:02 PM",
            isAutoSelected = false,
            sampleGradientStart = 0xFFEA580C,
            sampleGradientEnd = 0xFFFBBF24,
            samplePatternType = "pet"
        ),
        PhotoItem(
            id = "photo_7",
            title = "Vintage Parisian Bistro",
            category = "Street",
            aestheticScore = 0.89f,
            sharpnessScore = 0.87f,
            lightingScore = 0.92f,
            dynamicRangeScore = 0.89f,
            dateAdded = "Oct 17, 7:40 PM",
            isAutoSelected = false,
            sampleGradientStart = 0xFFB45309,
            sampleGradientEnd = 0xFF78350F,
            samplePatternType = "street"
        ),
        PhotoItem(
            id = "photo_8",
            title = "Starry Mojave Desert",
            category = "Night",
            aestheticScore = 0.97f,
            sharpnessScore = 0.95f,
            lightingScore = 0.97f,
            dynamicRangeScore = 0.98f,
            dateAdded = "Oct 16, 11:58 PM",
            isAutoSelected = true,
            sampleGradientStart = 0xFF1E1B4B,
            sampleGradientEnd = 0xFF4338CA,
            samplePatternType = "night"
        )
    )

    val initialVariations = listOf(
        VariationResult(
            id = "var_1",
            photoId = "photo_2",
            photoTitle = "Neon Tokyo Alley",
            styleId = "cyberpunk",
            styleName = "Cyberpunk Neon",
            provider = AIProvider.GEMINI,
            modelUsed = "gemini-2.5-flash-image",
            promptUsed = "Neon Tokyo Alley reimagined in cyberpunk neon style with ultra-reflective wet pavement and glowing holographic signs",
            timestamp = System.currentTimeMillis() - 7200000,
            isSavedLocally = true,
            isSyncedToCloud = true,
            primaryColor = 0xFF06B6D4,
            secondaryColor = 0xFFEC4899
        ),
        VariationResult(
            id = "var_2",
            photoId = "photo_1",
            photoTitle = "Golden Hour Portrait",
            styleId = "anime_ghibli",
            styleName = "Studio Anime",
            provider = AIProvider.GEMINI,
            modelUsed = "gemini-2.5-flash-image",
            promptUsed = "Golden Hour Portrait styled as hand-painted Studio Ghibli character art with soft sunset clouds",
            timestamp = System.currentTimeMillis() - 5400000,
            isSavedLocally = true,
            isSyncedToCloud = true,
            primaryColor = 0xFF10B981,
            secondaryColor = 0xFF38BDF8
        ),
        VariationResult(
            id = "var_3",
            photoId = "photo_3",
            photoTitle = "Alpine Glacier Lake",
            styleId = "renaissance",
            styleName = "Renaissance Oil",
            provider = AIProvider.OPENAI,
            modelUsed = "dall-e-3",
            promptUsed = "Alpine Glacier Lake as an 18th-century master oil painting with cracked varnish and heavy impasto strokes",
            timestamp = System.currentTimeMillis() - 3600000,
            isSavedLocally = true,
            isSyncedToCloud = false,
            primaryColor = 0xFFD97706,
            secondaryColor = 0xFF78350F
        ),
        VariationResult(
            id = "var_4",
            photoId = "photo_6",
            photoTitle = "Ginger Tabby Whiskers",
            styleId = "pixar_3d",
            styleName = "3D Claymation",
            provider = AIProvider.LOCAL_NEURAL,
            modelUsed = "edge-artistic-shader-v2",
            promptUsed = "Ginger Tabby Cat converted into tactile 3D claymation animated character with soft subsurface fur lighting",
            timestamp = System.currentTimeMillis() - 1800000,
            isSavedLocally = true,
            isSyncedToCloud = false,
            primaryColor = 0xFFF59E0B,
            secondaryColor = 0xFFEF4444
        )
    )

    /**
     * Intelligent Auto-Curation algorithm:
     * Computes a composite aesthetic fitness score based on:
     * - Sharpness / focus
     * - Dynamic range / lighting exposure
     * - Subject classification diversity
     * Selects top scoring photos automatically for batch styling.
     */
    fun autoSelectBestPhotos(photos: List<PhotoItem>, topCount: Int = 4): List<PhotoItem> {
        val sorted = photos.sortedByDescending { photo ->
            // Weighted formula: Aesthetic 40%, Lighting 30%, Sharpness 20%, Dynamic Range 10%
            (photo.aestheticScore * 0.4f) +
            (photo.lightingScore * 0.3f) +
            (photo.sharpnessScore * 0.2f) +
            (photo.dynamicRangeScore * 0.1f)
        }
        val topIds = sorted.take(topCount).map { it.id }.toSet()
        return photos.map { photo ->
            photo.copy(isAutoSelected = topIds.contains(photo.id))
        }
    }
}
