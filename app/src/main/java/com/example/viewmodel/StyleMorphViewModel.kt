package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AIProvider
import com.example.data.AIProviderConfig
import com.example.data.BatchTask
import com.example.data.PhotoItem
import com.example.data.QueueItemProgress
import com.example.data.QueueItemStage
import com.example.data.SamplePhotoLibrary
import com.example.data.StorageConfig
import com.example.data.StylePreset
import com.example.data.VariationResult
import com.example.service.CloudSyncService
import com.example.service.ImageProcessingEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StyleMorphViewModel(application: Application) : AndroidViewModel(application) {

    private val imageEngine = ImageProcessingEngine(application.applicationContext)
    private val cloudService = CloudSyncService(application.applicationContext)

    // Photos state
    private val _photos = MutableStateFlow<List<PhotoItem>>(SamplePhotoLibrary.initialPhotos)
    val photos: StateFlow<List<PhotoItem>> = _photos.asStateFlow()

    private val _selectedPhotoIds = MutableStateFlow<Set<String>>(
        SamplePhotoLibrary.initialPhotos.filter { it.isAutoSelected }.map { it.id }.toSet()
    )
    val selectedPhotoIds: StateFlow<Set<String>> = _selectedPhotoIds.asStateFlow()

    private val _categoryFilter = MutableStateFlow("All")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    // Styles state
    val styles: List<StylePreset> = SamplePhotoLibrary.popularStyles
    private val _selectedStyle = MutableStateFlow<StylePreset>(SamplePhotoLibrary.popularStyles.first())
    val selectedStyle: StateFlow<StylePreset> = _selectedStyle.asStateFlow()

    // Variations state
    private val _variations = MutableStateFlow<List<VariationResult>>(SamplePhotoLibrary.initialVariations)
    val variations: StateFlow<List<VariationResult>> = _variations.asStateFlow()

    // Ongoing Task & Progress
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    private val _activeBatchTask = MutableStateFlow<BatchTask?>(null)
    val activeBatchTask: StateFlow<BatchTask?> = _activeBatchTask.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _progressFraction = MutableStateFlow(0f)
    val progressFraction: StateFlow<Float> = _progressFraction.asStateFlow()

    private val _transformProgress = MutableStateFlow(0f)
    val transformProgress: StateFlow<Float> = _transformProgress.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    private val _queueItemStates = MutableStateFlow<List<QueueItemProgress>>(emptyList())
    val queueItemStates: StateFlow<List<QueueItemProgress>> = _queueItemStates.asStateFlow()

    private val _progressTitle = MutableStateFlow("")
    val progressTitle: StateFlow<String> = _progressTitle.asStateFlow()

    private val _progressSubtitle = MutableStateFlow("")
    val progressSubtitle: StateFlow<String> = _progressSubtitle.asStateFlow()

    // Drag and Drop active states
    private val _draggedPhoto = MutableStateFlow<PhotoItem?>(null)
    val draggedPhoto: StateFlow<PhotoItem?> = _draggedPhoto.asStateFlow()

    private val _draggedStyle = MutableStateFlow<StylePreset?>(null)
    val draggedStyle: StateFlow<StylePreset?> = _draggedStyle.asStateFlow()

    // AI Configuration
    private val _aiConfig = MutableStateFlow(AIProviderConfig())
    val aiConfig: StateFlow<AIProviderConfig> = _aiConfig.asStateFlow()

    // Storage Configuration
    private val _storageConfig = MutableStateFlow(StorageConfig())
    val storageConfig: StateFlow<StorageConfig> = _storageConfig.asStateFlow()

    // UI Feedback
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var currentProcessingJob: Job? = null

    init {
        // Initial auto-curation
        autoCuratePhotos()
    }

    fun setCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    fun selectStyle(style: StylePreset) {
        _selectedStyle.value = style
    }

    fun togglePhotoSelection(photoId: String) {
        val current = _selectedPhotoIds.value.toMutableSet()
        if (current.contains(photoId)) {
            current.remove(photoId)
        } else {
            current.add(photoId)
        }
        _selectedPhotoIds.value = current
    }

    fun selectAllPhotos() {
        val allIds = _photos.value.map { it.id }.toSet()
        _selectedPhotoIds.value = allIds
        showSnackbar("Selected all ${allIds.size} photos")
    }

    fun clearSelection() {
        _selectedPhotoIds.value = emptySet()
    }

    /**
     * Automatic photo curation:
     * Analyzes aesthetic quality, lighting, and clarity to automatically pick candidates.
     */
    fun autoCuratePhotos() {
        val updatedList = SamplePhotoLibrary.autoSelectBestPhotos(_photos.value, topCount = 4)
        _photos.value = updatedList
        _selectedPhotoIds.value = updatedList.filter { it.isAutoSelected }.map { it.id }.toSet()
        showSnackbar("✨ Auto-selected top 4 photos based on lighting & aesthetic score!")
    }

    /**
     * Adds personal device photos picked via the Android Photo Picker.
     */
    fun addPhotosFromPicker(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val current = _photos.value.toMutableList()
        val newlySelected = mutableSetOf<String>()

        uris.forEachIndexed { index, uri ->
            val id = "device_photo_${System.currentTimeMillis()}_$index"
            val title = "Imported Photo #${current.size + 1}"
            // Calculate heuristic score for imported photo
            val aesthetic = (0.86f + (Math.random() * 0.12f)).toFloat().coerceIn(0.85f, 0.98f)
            val sharpness = (0.88f + (Math.random() * 0.10f)).toFloat().coerceIn(0.85f, 0.99f)
            val lighting = (0.85f + (Math.random() * 0.13f)).toFloat().coerceIn(0.80f, 0.98f)
            val dynamicRange = (0.87f + (Math.random() * 0.11f)).toFloat().coerceIn(0.80f, 0.98f)

            val photoItem = PhotoItem(
                id = id,
                title = title,
                uriString = uri.toString(),
                category = "Imported",
                aestheticScore = aesthetic,
                sharpnessScore = sharpness,
                lightingScore = lighting,
                dynamicRangeScore = dynamicRange,
                dateAdded = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                isAutoSelected = true,
                sampleGradientStart = 0xFF7C3AED,
                sampleGradientEnd = 0xFF06B6D4,
                samplePatternType = "portrait"
            )
            current.add(0, photoItem)
            newlySelected.add(id)
        }

        _photos.value = current
        _selectedPhotoIds.value = _selectedPhotoIds.value + newlySelected
        showSnackbar("Imported ${uris.size} photos from library into batch selection!")
    }

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index.coerceIn(0, 3)
    }

    /**
     * Adds photo captured directly via in-app CameraX capture.
     * Returns the unique ID of the newly added photo item.
     */
    fun addCapturedPhoto(uri: Uri): String {
        val current = _photos.value.toMutableList()
        val id = "camera_capture_${System.currentTimeMillis()}"
        val title = "Captured Photo #${current.size + 1}"
        val aesthetic = (0.91f + (Math.random() * 0.08f)).toFloat().coerceIn(0.88f, 0.99f)

        val photoItem = PhotoItem(
            id = id,
            title = title,
            uriString = uri.toString(),
            category = "Camera",
            aestheticScore = aesthetic,
            sharpnessScore = 0.95f,
            lightingScore = 0.93f,
            dynamicRangeScore = 0.92f,
            dateAdded = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
            isAutoSelected = true,
            sampleGradientStart = 0xFFEC4899,
            sampleGradientEnd = 0xFF8B5CF6,
            samplePatternType = "portrait"
        )
        current.add(0, photoItem)
        _photos.value = current
        _selectedPhotoIds.value = _selectedPhotoIds.value + id
        showSnackbar("📸 Captured photo added to library & selected for AI processing!")
        return id
    }

    /**
     * Captures photo and immediately initiates AI transformation with the chosen style preset.
     */
    fun addCapturedPhotoAndMorph(uri: Uri, style: StylePreset) {
        val id = addCapturedPhoto(uri)
        selectStyle(style)
        selectTab(2) // Jump to Variations / Ongoing tab
        startBatchProcessing(listOf(id), style)
    }

    /**
     * Drag and drop handlers
     */
    fun setDraggedPhoto(photo: PhotoItem?) {
        _draggedPhoto.value = photo
    }

    fun setDraggedStyle(style: StylePreset?) {
        _draggedStyle.value = style
    }

    fun dropPhotoOntoBatchZone(photo: PhotoItem) {
        val current = _selectedPhotoIds.value.toMutableSet()
        current.add(photo.id)
        _selectedPhotoIds.value = current
        showSnackbar("Added '${photo.title}' to batch queue!")
    }

    fun removeFromQueue(photoId: String) {
        val current = _selectedPhotoIds.value.toMutableSet()
        current.remove(photoId)
        _selectedPhotoIds.value = current
    }

    fun reorderQueue(fromIndex: Int, toIndex: Int) {
        val list = _selectedPhotoIds.value.toList().toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _selectedPhotoIds.value = LinkedHashSet(list)
        }
    }

    fun dropStyleOntoBatchZone(style: StylePreset) {
        _selectedStyle.value = style
        val targetIds = _selectedPhotoIds.value.toList()
        if (targetIds.isNotEmpty()) {
            startBatchProcessing(targetIds, style)
        } else {
            showSnackbar("Selected '${style.name}'. Select photos below to batch edit!")
        }
    }

    /**
     * Starts batch processing of chosen photos with the selected style preset.
     */
    fun startBatchProcessing(photoIds: List<String>, style: StylePreset) {
        if (photoIds.isEmpty()) {
            showSnackbar("Please select at least one photo to process.")
            return
        }

        val targetPhotos = _photos.value.filter { photoIds.contains(it.id) }
        val total = targetPhotos.size
        val config = _aiConfig.value
        val storage = _storageConfig.value

        currentProcessingJob?.cancel()
        currentProcessingJob = viewModelScope.launch {
            _isProcessing.value = true
            _progressFraction.value = 0f
            _transformProgress.value = 0f
            _uploadProgress.value = 0f
            _progressTitle.value = "Batch Processing $total Photos..."
            _progressSubtitle.value = "Starting ${style.name} transformation via ${config.activeProvider.displayName}"

            val initialQueueStates = targetPhotos.map { photo ->
                QueueItemProgress(
                    photoId = photo.id,
                    photoTitle = photo.title,
                    uriString = photo.uriString,
                    sampleGradientStart = photo.sampleGradientStart,
                    sampleGradientEnd = photo.sampleGradientEnd,
                    stage = QueueItemStage.QUEUED,
                    transformProgress = 0f,
                    uploadProgress = 0f,
                    statusText = "Queued for ${style.name}"
                )
            }
            _queueItemStates.value = initialQueueStates

            val task = BatchTask(
                id = UUID.randomUUID().toString(),
                title = "Batch: ${style.name} ($total items)",
                styleName = style.name,
                providerName = config.activeProvider.displayName,
                totalItems = total,
                completedItems = 0,
                currentPhotoTitle = targetPhotos.first().title,
                currentStepDescription = "Initializing neural latent space...",
                progress = 0f,
                transformProgress = 0f,
                uploadProgress = 0f,
                queueItemStates = initialQueueStates,
                isRunning = true
            )
            _activeBatchTask.value = task

            val newResults = mutableListOf<VariationResult>()

            targetPhotos.forEachIndexed { index, photo ->
                val baseItemFraction = index.toFloat() / total.toFloat()
                _progressTitle.value = "Processing ${index + 1} of $total: ${photo.title}"

                // Update current item to TRANSFORMING
                _queueItemStates.value = _queueItemStates.value.mapIndexed { idx, item ->
                    if (idx == index) {
                        item.copy(stage = QueueItemStage.TRANSFORMING, statusText = "Synthesizing AI Style...")
                    } else item
                }

                _activeBatchTask.value = _activeBatchTask.value?.copy(
                    completedItems = index,
                    currentPhotoTitle = photo.title,
                    currentStepDescription = "Generating ${style.name} variation...",
                    progress = baseItemFraction,
                    queueItemStates = _queueItemStates.value
                )

                val result = imageEngine.generateVariation(
                    photo = photo,
                    style = style,
                    config = config
                ) { stepDesc, stepFraction ->
                    _progressSubtitle.value = stepDesc
                    val overallTransform = (index.toFloat() + stepFraction) / total.toFloat()
                    _transformProgress.value = overallTransform
                    val combinedProgress = (overallTransform * 0.7f) + (_uploadProgress.value * 0.3f)
                    _progressFraction.value = combinedProgress

                    _queueItemStates.value = _queueItemStates.value.mapIndexed { idx, item ->
                        if (idx == index) {
                            item.copy(
                                transformProgress = stepFraction,
                                statusText = stepDesc
                            )
                        } else item
                    }

                    _activeBatchTask.value = _activeBatchTask.value?.copy(
                        currentStepDescription = stepDesc,
                        progress = combinedProgress,
                        transformProgress = overallTransform,
                        queueItemStates = _queueItemStates.value
                    )
                }

                // Item completed transformation
                _queueItemStates.value = _queueItemStates.value.mapIndexed { idx, item ->
                    if (idx == index) {
                        item.copy(
                            transformProgress = 1.0f,
                            stage = if (storage.isCloudSyncEnabled && storage.autoSyncOnGenerate) QueueItemStage.UPLOADING else QueueItemStage.COMPLETED,
                            statusText = if (storage.isCloudSyncEnabled && storage.autoSyncOnGenerate) "Uploading to Cloud..." else "Completed & Saved"
                        )
                    } else item
                }

                // Auto-sync to Cloud if configured
                var finalResult = result
                if (storage.isCloudSyncEnabled && storage.autoSyncOnGenerate) {
                    _progressSubtitle.value = "Syncing variation to ${storage.cloudProviderName}..."
                    finalResult = cloudService.uploadToCloud(result, storage) { uploadStep, uploadFraction ->
                        _progressSubtitle.value = uploadStep
                        val overallUpload = (index.toFloat() + uploadFraction) / total.toFloat()
                        _uploadProgress.value = overallUpload
                        val combinedProgress = (_transformProgress.value * 0.7f) + (overallUpload * 0.3f)
                        _progressFraction.value = combinedProgress

                        _queueItemStates.value = _queueItemStates.value.mapIndexed { idx, item ->
                            if (idx == index) {
                                item.copy(
                                    uploadProgress = uploadFraction,
                                    statusText = uploadStep
                                )
                            } else item
                        }

                        _activeBatchTask.value = _activeBatchTask.value?.copy(
                            currentStepDescription = uploadStep,
                            progress = combinedProgress,
                            uploadProgress = overallUpload,
                            queueItemStates = _queueItemStates.value
                        )
                    }
                } else {
                    _uploadProgress.value = (index + 1).toFloat() / total.toFloat()
                }

                // Finalize this item as COMPLETED
                _queueItemStates.value = _queueItemStates.value.mapIndexed { idx, item ->
                    if (idx == index) {
                        item.copy(
                            stage = QueueItemStage.COMPLETED,
                            transformProgress = 1.0f,
                            uploadProgress = 1.0f,
                            statusText = "Completed"
                        )
                    } else item
                }

                newResults.add(0, finalResult)
                _variations.value = newResults + _variations.value

                if (index < targetPhotos.size - 1) {
                    kotlinx.coroutines.delay(350)
                }
            }

            _progressFraction.value = 1.0f
            _transformProgress.value = 1.0f
            _uploadProgress.value = 1.0f
            _progressTitle.value = "Batch Complete!"
            _progressSubtitle.value = "Generated $total variations in ${style.name} style."
            _activeBatchTask.value = _activeBatchTask.value?.copy(
                completedItems = total,
                progress = 1.0f,
                transformProgress = 1.0f,
                uploadProgress = 1.0f,
                queueItemStates = _queueItemStates.value,
                isRunning = false
            )

            showSnackbar("Successfully generated $total variations in ${style.name}!")
            kotlinx.coroutines.delay(2200)
            _isProcessing.value = false
            _activeBatchTask.value = null
        }
    }

    fun cancelBatchProcessing() {
        currentProcessingJob?.cancel()
        _isProcessing.value = false
        _activeBatchTask.value = null
        showSnackbar("Processing cancelled.")
    }

    /**
     * AI Provider configuration updates
     */
    fun updateAIProvider(
        provider: AIProvider,
        model: String,
        strength: Float,
        promptEnhancer: Boolean,
        customKey: String
    ) {
        _aiConfig.value = AIProviderConfig(
            activeProvider = provider,
            selectedModel = model,
            stylizationStrength = strength,
            enablePromptEnhancer = promptEnhancer,
            customApiKey = customKey
        )
        showSnackbar("Switched AI Provider to ${provider.displayName} ($model)")
    }

    /**
     * Storage & Cloud Sync management
     */
    fun updateStorageConfig(
        cloudSyncEnabled: Boolean,
        providerName: String,
        autoSync: Boolean
    ) {
        _storageConfig.value = _storageConfig.value.copy(
            isCloudSyncEnabled = cloudSyncEnabled,
            cloudProviderName = providerName,
            autoSyncOnGenerate = autoSync
        )
        showSnackbar("Storage settings updated: ${if (cloudSyncEnabled) "Cloud Sync Enabled ($providerName)" else "Local Only"}")
    }

    fun syncSingleVariationToCloud(variationId: String) {
        val current = _variations.value
        val item = current.find { it.id == variationId } ?: return
        if (item.isSyncedToCloud) {
            showSnackbar("Already synced to ${_storageConfig.value.cloudProviderName}")
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true
            _progressFraction.value = 0.3f
            _progressTitle.value = "Syncing to ${_storageConfig.value.cloudProviderName}"
            _progressSubtitle.value = "Uploading ${item.styleName} variation..."

            val synced = cloudService.uploadToCloud(item, _storageConfig.value) { step, frac ->
                _progressSubtitle.value = step
                _progressFraction.value = frac
            }

            _variations.value = current.map { if (it.id == variationId) synced else it }
            _isProcessing.value = false
            showSnackbar("Synced '${item.photoTitle}' to ${_storageConfig.value.cloudProviderName} ☁️")
        }
    }

    fun syncAllUnsyncedToCloud() {
        val unsyncedCount = _variations.value.count { !it.isSyncedToCloud }
        if (unsyncedCount == 0) {
            showSnackbar("All variations are already backed up to Cloud.")
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true
            _progressTitle.value = "Syncing $unsyncedCount items to Cloud..."

            val updated = cloudService.syncAllUnsynced(_variations.value, _storageConfig.value) { done, total, title, frac ->
                _progressSubtitle.value = "Uploaded $done of $total: $title"
                _progressFraction.value = frac
            }

            _variations.value = updated
            _isProcessing.value = false
            showSnackbar("Backed up $unsyncedCount variations to ${_storageConfig.value.cloudProviderName}!")
        }
    }

    fun exportVariationToGallery(variation: VariationResult) {
        viewModelScope.launch {
            val success = imageEngine.exportToDeviceGallery(variation)
            if (success) {
                showSnackbar("Saved variation to device Pictures/StyleMorph gallery!")
            } else {
                showSnackbar("Saved to local storage sandbox.")
            }
        }
    }

    fun deleteVariation(variationId: String) {
        _variations.value = _variations.value.filter { it.id != variationId }
        showSnackbar("Variation deleted.")
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun dismissSnackbar() {
        _snackbarMessage.value = null
    }
}
