package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PhotoItem
import com.example.data.VariationResult
import com.example.ui.components.AIProviderSheet
import com.example.ui.components.BatchProcessingStatusBar
import com.example.ui.components.BatchQueueDropShelf
import com.example.ui.components.BeforeAfterCompareDialog
import com.example.ui.components.CameraCaptureDialog
import com.example.ui.components.CircularProgressGauge
import com.example.ui.components.DragDropBatchTarget
import com.example.ui.components.GlobalProgressBar
import com.example.ui.components.PhotoCard
import com.example.ui.components.SettingsDrawerContent
import com.example.ui.components.StorageSettingsSheet
import com.example.ui.components.StyleCard
import com.example.ui.components.StylePresetCarousel
import com.example.ui.components.StyleSelectionDialog
import com.example.ui.components.VariationCard
import com.example.ui.dragdrop.DragGhostOverlay
import com.example.ui.dragdrop.rememberDragDropState
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan
import com.example.viewmodel.StyleMorphViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StyleMorphViewModel,
    modifier: Modifier = Modifier
) {
    val photos by viewModel.photos.collectAsState()
    val selectedIds by viewModel.selectedPhotoIds.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()
    val styles = viewModel.styles
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val variations by viewModel.variations.collectAsState()

    // Task and Progress
    val isProcessing by viewModel.isProcessing.collectAsState()
    val progressFraction by viewModel.progressFraction.collectAsState()
    val transformProgress by viewModel.transformProgress.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val queueItemStates by viewModel.queueItemStates.collectAsState()
    val progressTitle by viewModel.progressTitle.collectAsState()
    val progressSubtitle by viewModel.progressSubtitle.collectAsState()
    val activeTask by viewModel.activeBatchTask.collectAsState()

    // Drag states
    val draggedPhoto by viewModel.draggedPhoto.collectAsState()
    val draggedStyle by viewModel.draggedStyle.collectAsState()

    // Configurations
    val aiConfig by viewModel.aiConfig.collectAsState()
    val storageConfig by viewModel.storageConfig.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    // Sheet / Dialog states
    var showAIProviderSheet by remember { mutableStateOf(false) }
    var showStorageSheet by remember { mutableStateOf(false) }
    var showCameraCapture by remember { mutableStateOf(false) }
    var showStyleSelectionDialog by remember { mutableStateOf(false) }
    var inspectVariation by remember { mutableStateOf<VariationResult?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }

    val dragDropState = rememberDragDropState()
    val queuedPhotos = photos.filter { selectedIds.contains(it.id) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Photo Library", "Styles", "Variations (${variations.size})", "Batch Queue")

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    // Android Photo Picker launcher for device library scanning
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addPhotosFromPicker(uris)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                SettingsDrawerContent(
                    aiConfig = aiConfig,
                    storageConfig = storageConfig,
                    onOpenAIProviderSettings = { showAIProviderSheet = true },
                    onOpenStorageSettings = { showStorageSheet = true },
                    onOpenStyleDialog = { showStyleSelectionDialog = true },
                    onLaunchCamera = { showCameraCapture = true },
                    onScanLibrary = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onAutoCurate = { viewModel.autoCuratePhotos() },
                    onSyncAllCloud = { viewModel.syncAllUnsyncedToCloud() },
                    onClose = { coroutineScope.launch { drawerState.close() } }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                },
                                modifier = Modifier.testTag("hamburger_settings_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Settings Menu",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryViolet, SecondaryCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "StyleMorph",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "AI Photo Variation Studio",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // AI Provider badge chip
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showAIProviderSheet = true }
                            .testTag("open_ai_provider_sheet_button"),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (aiConfig.activeProvider.isCloud) SecondaryCyan else AccentEmerald
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = aiConfig.activeProvider.providerBadge,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "AI Settings",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Storage indicator button
                    IconButton(
                        onClick = { showStorageSheet = true },
                        modifier = Modifier.testTag("open_storage_settings_button")
                    ) {
                        Icon(
                            imageVector = if (storageConfig.isCloudSyncEnabled) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                            contentDescription = "Storage Settings",
                            tint = if (storageConfig.isCloudSyncEnabled) SecondaryCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedIds.isNotEmpty() && selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = {
                        viewModel.startBatchProcessing(selectedIds.toList(), selectedStyle)
                    },
                    containerColor = PrimaryViolet,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_batch_morph")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Morph ${selectedIds.size} Photos",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Batch processing status bar with animated circular indicators for transform & upload
            BatchProcessingStatusBar(
                isVisible = isProcessing,
                title = progressTitle,
                subtitle = progressSubtitle,
                transformProgress = transformProgress,
                uploadProgress = uploadProgress,
                queueItems = queueItemStates,
                onCancel = { viewModel.cancelBatchProcessing() }
            )

            // Interactive Visual Drag-and-Drop Batch Shelf
            BatchQueueDropShelf(
                queuedPhotos = queuedPhotos,
                selectedStyle = selectedStyle,
                dragDropState = dragDropState,
                onRemoveFromQueue = { photoId -> viewModel.removeFromQueue(photoId) },
                onReorderQueue = { from, to -> viewModel.reorderQueue(from, to) },
                onStartPipeline = {
                    viewModel.startBatchProcessing(selectedIds.toList(), selectedStyle)
                },
                onClearQueue = { viewModel.clearSelection() },
                onOpenStyleSelection = { showStyleSelectionDialog = true },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = PrimaryViolet
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) PrimaryVioletLight else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("tab_$index")
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: Photo Library
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Action Bar: Auto-Pick Best, Import Library, Select All, Clear
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.autoCuratePhotos() },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("auto_pick_best_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Auto-Pick Best", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { showCameraCapture = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryCyan),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("capture_camera_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Capture Photo",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Capture", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }

                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("scan_device_library_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Scan", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { showStyleSelectionDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("open_style_dialog_action_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Presets",
                                        tint = PrimaryVioletLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Presets", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        if (selectedIds.size == photos.size) {
                                            viewModel.clearSelection()
                                        } else {
                                            viewModel.selectAllPhotos()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("select_all_toggle_button")
                                ) {
                                    Text(
                                        text = if (selectedIds.size == photos.size) "Clear" else "All (${photos.size})",
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // Interactive AI Styling Presets Carousel Component
                        StylePresetCarousel(
                            styles = styles,
                            selectedStyle = selectedStyle,
                            onSelectStyle = { viewModel.selectStyle(it) },
                            onOpenStyleDialog = { showStyleSelectionDialog = true },
                            queuedPhotosCount = queuedPhotos.size,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // Category Filter Chips
                        val categories = listOf("All", "Auto-Picked", "Portrait", "Urban", "Landscape", "Nature", "Architecture", "Pet", "Night")
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories) { cat ->
                                FilterChip(
                                    selected = categoryFilter == cat,
                                    onClick = { viewModel.setCategoryFilter(cat) },
                                    label = { Text(text = cat, fontSize = 12.sp) },
                                    modifier = Modifier.testTag("filter_chip_$cat")
                                )
                            }
                        }

                        // Filtered Photos Grid
                        val filteredPhotos = photos.filter { photo ->
                            when (categoryFilter) {
                                "All" -> true
                                "Auto-Picked" -> photo.isAutoSelected
                                else -> photo.category.equals(categoryFilter, ignoreCase = true)
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredPhotos, key = { it.id }) { photo ->
                                PhotoCard(
                                    photo = photo,
                                    isSelected = selectedIds.contains(photo.id),
                                    onToggleSelect = { viewModel.togglePhotoSelection(photo.id) },
                                    onInspect = { viewModel.togglePhotoSelection(photo.id) },
                                    onDragStart = { p, offset ->
                                        viewModel.setDraggedPhoto(p)
                                        dragDropState.startDragging(p, offset)
                                    },
                                    onDrag = { dragAmount ->
                                        dragDropState.onDrag(dragAmount)
                                    },
                                    onDragEnd = {
                                        val dropTargetId = dragDropState.stopDragging()
                                        val currentDragged = viewModel.draggedPhoto.value
                                        viewModel.setDraggedPhoto(null)
                                        if (currentDragged != null && dropTargetId != null) {
                                            when (dropTargetId) {
                                                "target_queue_drop" -> {
                                                    viewModel.dropPhotoOntoBatchZone(currentDragged)
                                                }
                                                "target_direct_process" -> {
                                                    viewModel.dropPhotoOntoBatchZone(currentDragged)
                                                    viewModel.startBatchProcessing(
                                                        (selectedIds + currentDragged.id).toList(),
                                                        selectedStyle
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 2: Popular AI Styles
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Popular AI Styles",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Select, drag, or browse full presets catalog",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { showStyleSelectionDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("tab_styles_open_dialog_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Preset Catalog", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Style Preset Carousel in Styles tab
                        StylePresetCarousel(
                            styles = styles,
                            selectedStyle = selectedStyle,
                            onSelectStyle = { viewModel.selectStyle(it) },
                            onOpenStyleDialog = { showStyleSelectionDialog = true },
                            queuedPhotosCount = queuedPhotos.size,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(styles, key = { it.id }) { style ->
                                StyleCard(
                                    style = style,
                                    isSelected = selectedStyle.id == style.id,
                                    onSelect = { viewModel.selectStyle(style) },
                                    onApplyToBatch = {
                                        viewModel.selectStyle(style)
                                        if (selectedIds.isNotEmpty()) {
                                            viewModel.startBatchProcessing(selectedIds.toList(), style)
                                        } else {
                                            viewModel.showSnackbar("Style selected! Choose photos in Photo Library to batch transform.")
                                        }
                                    },
                                    onDragStart = { s -> viewModel.setDraggedStyle(s) },
                                    onDragEnd = { viewModel.setDraggedStyle(null) }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 3: Variations Gallery
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Variations header with Cloud Sync All
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${variations.size} Generated Variations",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap any card to compare before / after",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            val unsyncedCount = variations.count { !it.isSyncedToCloud }
                            if (unsyncedCount > 0) {
                                OutlinedButton(
                                    onClick = { viewModel.syncAllUnsyncedToCloud() },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("sync_all_variations_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudQueue,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = SecondaryCyan
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Sync ($unsyncedCount)", fontSize = 12.sp)
                                }
                            }
                        }

                        if (variations.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No Variations Generated Yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Select photos and pick a style to create variations!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(variations, key = { it.id }) { variation ->
                                    VariationCard(
                                        variation = variation,
                                        onClick = { inspectVariation = variation },
                                        onSyncCloud = { viewModel.syncSingleVariationToCloud(variation.id) },
                                        onSaveToDevice = { viewModel.exportVariationToGallery(variation) }
                                    )
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 4: Batch Queue & Task Inspector
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Batch Queue & Tasks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Monitor and control ongoing batch operations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Active Task Card if running
                        if (activeTask != null) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                tonalElevation = 4.dp
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = activeTask?.title ?: "Active Task",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Button(
                                            onClick = { viewModel.cancelBatchProcessing() },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Cancel", fontSize = 12.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Current: ${activeTask?.currentPhotoTitle}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SecondaryCyan
                                    )
                                    Text(
                                        text = activeTask?.currentStepDescription ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Dual Circular Progress Gauges in Tab 4
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressGauge(
                                            label = "AI Transform",
                                            progress = transformProgress,
                                            color = PrimaryVioletLight,
                                            trackColor = PrimaryViolet.copy(alpha = 0.2f),
                                            icon = Icons.Default.AutoAwesome,
                                            testTag = "tab_transform_gauge"
                                        )

                                        CircularProgressGauge(
                                            label = "Cloud Sync",
                                            progress = uploadProgress,
                                            color = SecondaryCyan,
                                            trackColor = SecondaryCyan.copy(alpha = 0.2f),
                                            icon = Icons.Default.CloudUpload,
                                            testTag = "tab_upload_gauge"
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Completed ${activeTask?.completedItems} of ${activeTask?.totalItems}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Checklist,
                                        contentDescription = null,
                                        tint = SecondaryCyan,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No Ongoing Batch Tasks",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${selectedIds.size} photos currently selected. Drop onto batch zone to start!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // AI Provider Bottom Sheet
    if (showAIProviderSheet) {
        AIProviderSheet(
            config = aiConfig,
            onDismiss = { showAIProviderSheet = false },
            onSave = { provider, model, strength, enhancer, key ->
                viewModel.updateAIProvider(provider, model, strength, enhancer, key)
            }
        )
    }

    // Storage Settings Bottom Sheet
    if (showStorageSheet) {
        val unsyncedCount = variations.count { !it.isSyncedToCloud }
        StorageSettingsSheet(
            config = storageConfig,
            unsyncedCount = unsyncedCount,
            onDismiss = { showStorageSheet = false },
            onSave = { cloudEnabled, provider, autoSync ->
                viewModel.updateStorageConfig(cloudEnabled, provider, autoSync)
            },
            onSyncAll = { viewModel.syncAllUnsyncedToCloud() }
        )
    }

    // Before / After Compare Dialog
    inspectVariation?.let { variation ->
        val origPhoto = photos.find { it.id == variation.photoId }
        BeforeAfterCompareDialog(
            variation = variation,
            originalPhoto = origPhoto,
            onDismiss = { inspectVariation = null },
            onSyncCloud = { viewModel.syncSingleVariationToCloud(variation.id) },
            onSaveToDevice = { viewModel.exportVariationToGallery(variation) },
            onDelete = {
                viewModel.deleteVariation(variation.id)
                inspectVariation = null
            }
        )
    }

    // In-app CameraX Capture Dialog
    if (showCameraCapture) {
        CameraCaptureDialog(
            onDismiss = { showCameraCapture = false },
            onPhotoCaptured = { uri ->
                showCameraCapture = false
                viewModel.addCapturedPhoto(uri)
            }
        )
    }

    // AI Styling Presets Selection Dialog
    if (showStyleSelectionDialog) {
        StyleSelectionDialog(
            styles = styles,
            currentStyle = selectedStyle,
            queuedPhotosCount = queuedPhotos.size,
            onDismiss = { showStyleSelectionDialog = false },
            onSelectStyle = { style ->
                viewModel.selectStyle(style)
            },
            onApplyAndMorph = { style ->
                viewModel.selectStyle(style)
                if (selectedIds.isNotEmpty()) {
                    viewModel.startBatchProcessing(selectedIds.toList(), style)
                }
            }
        )
    }

    // Floating drag ghost avatar tracking touch across the screen
    DragGhostOverlay(dragDropState = dragDropState)
        }
    }
}
