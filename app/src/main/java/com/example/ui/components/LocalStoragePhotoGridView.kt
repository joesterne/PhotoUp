package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.PhotoItem
import com.example.data.StylePreset
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan

/**
 * GridView component displaying images from local device storage (internal app storage,
 * captured camera files, imported library media), with comprehensive multi-selection
 * controls and direct workflow integration for batch AI morphing.
 */
@Composable
fun LocalStoragePhotoGridView(
    photos: List<PhotoItem>,
    selectedPhotoIds: Set<String>,
    activeStylePreset: StylePreset,
    onToggleSelect: (String) -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onAutoCurate: () -> Unit,
    onStartBatchProcess: (List<String>, StylePreset) -> Unit,
    onLaunchCamera: () -> Unit,
    onImportFromDevice: () -> Unit,
    onScanLocalStorage: () -> Unit,
    onOpenStyleDialog: () -> Unit,
    modifier: Modifier = Modifier,
    onDragStart: ((PhotoItem, Offset) -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null
) {
    var gridColumns by remember { mutableIntStateOf(2) }
    var selectedFilter by remember { mutableStateOf("All Local") }
    var inspectedPhoto by remember { mutableStateOf<PhotoItem?>(null) }

    val filterOptions = listOf(
        "All Local",
        "Auto-Curated ✨",
        "Camera 📸",
        "Imported 📥",
        "Portrait",
        "Urban",
        "Landscape",
        "Nature",
        "Architecture",
        "Pet"
    )

    // Filter local storage photos based on active category
    val filteredPhotos = remember(photos, selectedFilter) {
        photos.filter { photo ->
            when (selectedFilter) {
                "All Local" -> true
                "Auto-Curated ✨" -> photo.isAutoSelected || photo.aestheticScore >= 0.90f
                "Camera 📸" -> photo.category.equals("Camera", ignoreCase = true)
                "Imported 📥" -> photo.category.equals("Imported", ignoreCase = true) || photo.category.equals("Local Storage", ignoreCase = true)
                else -> photo.category.equals(selectedFilter, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("local_storage_grid_view")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: Local Storage Info & View Mode Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryViolet.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SdStorage,
                            contentDescription = "Device Storage",
                            tint = PrimaryVioletLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Local Storage Media",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${photos.size} items · ${selectedPhotoIds.size} queued for batch",
                            fontSize = 12.sp,
                            color = if (selectedPhotoIds.isNotEmpty()) PrimaryVioletLight else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Toggle grid columns: 2 columns vs 3 columns
                    IconButton(
                        onClick = { gridColumns = if (gridColumns == 2) 3 else 2 },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("local_grid_columns_toggle")
                    ) {
                        Icon(
                            imageVector = if (gridColumns == 2) Icons.Default.ViewModule else Icons.Default.GridView,
                            contentDescription = "Toggle Grid Columns (${gridColumns} columns active)",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Auto-Pick Best heuristic curation
                    IconButton(
                        onClick = onAutoCurate,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("auto_pick_best_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Auto Pick Best Photos",
                            tint = AccentAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Select All / Deselect All
                    IconButton(
                        onClick = {
                            if (selectedPhotoIds.size >= photos.size && photos.isNotEmpty()) {
                                onClearSelection()
                            } else {
                                onSelectAll()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("select_all_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (selectedPhotoIds.size >= photos.size && photos.isNotEmpty()) Icons.Default.Deselect else Icons.Default.SelectAll,
                            contentDescription = "Toggle Select All",
                            tint = if (selectedPhotoIds.isNotEmpty()) PrimaryVioletLight else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Quick Actions: Camera, Device Import, and Storage Scan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Launch CameraX
                Button(
                    onClick = onLaunchCamera,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryCyan),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("capture_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Camera",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // Import photos from device library
                OutlinedButton(
                    onClick = onImportFromDevice,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("scan_device_library_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Import", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Scan local app & device files
                OutlinedButton(
                    onClick = onScanLocalStorage,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("local_grid_scan_storage_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan Storage",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Scan", fontSize = 12.sp)
                }
            }

            // Category & Source Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryViolet.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryVioletLight
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("local_grid_filter_$filter")
                    )
                }
            }

            // Main Grid or Empty State
            if (filteredPhotos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = "No Local Photos in '$selectedFilter'",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Import pictures from your device library or snap a fresh photo with CameraX.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onImportFromDevice,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Import from Device", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onLaunchCamera,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Take Photo", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = if (selectedPhotoIds.isNotEmpty()) 90.dp else 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("local_photos_lazy_grid")
                ) {
                    items(filteredPhotos, key = { it.id }) { photo ->
                        val isSelected = selectedPhotoIds.contains(photo.id)
                        LocalStoragePhotoCard(
                            photo = photo,
                            isSelected = isSelected,
                            isCompact = gridColumns == 3,
                            onToggleSelect = { onToggleSelect(photo.id) },
                            onInspect = { inspectedPhoto = photo },
                            onDragStart = onDragStart,
                            onDrag = onDrag,
                            onDragEnd = onDragEnd
                        )
                    }
                }
            }
        }

        // Sticky Batch Workflow Processing Action Bar
        AnimatedVisibility(
            visible = selectedPhotoIds.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                tonalElevation = 8.dp,
                shadowElevation = 10.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryViolet.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("local_grid_batch_action_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selection Badge & Preset summary
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryViolet),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${selectedPhotoIds.size}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Selected for Batch",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Active style preset chip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .clickable { onOpenStyleDialog() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(activeStylePreset.primaryColor))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Style: ${activeStylePreset.name}",
                                fontSize = 11.sp,
                                color = PrimaryVioletLight,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clear Selection
                        IconButton(
                            onClick = onClearSelection,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("local_grid_clear_selection_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Selection",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Primary Action: Start Batch Processing
                        Button(
                            onClick = {
                                onStartBatchProcess(selectedPhotoIds.toList(), activeStylePreset)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("local_grid_process_batch_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AccentAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Batch Morph",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Photo Inspection & Aesthetic Metric Dialog
    inspectedPhoto?.let { photo ->
        val isSelected = selectedPhotoIds.contains(photo.id)
        PhotoInspectionDialog(
            photo = photo,
            isSelected = isSelected,
            activeStylePreset = activeStylePreset,
            onDismiss = { inspectedPhoto = null },
            onToggleSelection = {
                onToggleSelect(photo.id)
            },
            onMorphSingle = {
                onStartBatchProcess(listOf(photo.id), activeStylePreset)
                inspectedPhoto = null
            }
        )
    }
}

/**
 * Individual photo card item rendered within the Local Storage GridView.
 * Supports image loading via Coil (with fallback gradient art), multi-select checkbox,
 * aesthetic heuristic indicators, and long-press inspection.
 */
@Composable
fun LocalStoragePhotoCard(
    photo: PhotoItem,
    isSelected: Boolean,
    isCompact: Boolean,
    onToggleSelect: () -> Unit,
    onInspect: () -> Unit,
    onDragStart: ((PhotoItem, Offset) -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var cardBoundsInWindow by remember { mutableStateOf(Rect.Zero) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.5.dp else 1.dp,
            color = if (isSelected) PrimaryViolet else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        shadowElevation = if (isSelected) 6.dp else 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .onGloballyPositioned { coords ->
                cardBoundsInWindow = coords.boundsInWindow()
            }
            .pointerInput(photo.id) {
                if (onDragStart != null && onDrag != null && onDragEnd != null) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { localOffset ->
                            onDragStart(photo, cardBoundsInWindow.topLeft + localOffset)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onDrag(dragAmount)
                        },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
                    )
                } else {
                    detectTapGestures(
                        onTap = { onToggleSelect() },
                        onLongPress = { onInspect() }
                    )
                }
            }
            .clickable { onToggleSelect() }
            .testTag("local_photo_item_${photo.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (isCompact) 1f else 0.88f)
        ) {
            // Visual Image / Canvas
            if (!photo.uriString.isNullOrBlank()) {
                AsyncImage(
                    model = photo.uriString,
                    contentDescription = photo.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Synthetic photographic composition
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(photo.sampleGradientStart),
                                Color(photo.sampleGradientEnd)
                            ),
                            start = Offset.Zero,
                            end = Offset(size.width, size.height)
                        )
                    )
                }
                // Decorative category watermark icon
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (photo.category.lowercase()) {
                            "portrait" -> Icons.Default.Star
                            "camera" -> Icons.Default.CameraAlt
                            "imported" -> Icons.Default.AddPhotoAlternate
                            "urban" -> Icons.Default.Folder
                            else -> Icons.Default.PhotoLibrary
                        },
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(if (isCompact) 36.dp else 48.dp)
                    )
                }
            }

            // Top Badges Row: Quality Heuristic & Selection Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Aesthetic Score Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${(photo.aestheticScore * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Selection Checkbox Badge
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) PrimaryViolet else Color.Black.copy(alpha = 0.5f)
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .clickable { onToggleSelect() }
                        .testTag("local_photo_checkbox_${photo.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Bottom Gradient Scrim & Metadata
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Column {
                    Text(
                        text = photo.title,
                        color = Color.White,
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!isCompact) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = photo.category,
                                color = SecondaryCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = photo.dateAdded,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inspection modal presenting detailed local storage file attributes,
 * quality heuristics (Sharpness, Lighting, Dynamic Range), and quick batch/single morph actions.
 */
@Composable
private fun PhotoInspectionDialog(
    photo: PhotoItem,
    isSelected: Boolean,
    activeStylePreset: StylePreset,
    onDismiss: () -> Unit,
    onToggleSelection: () -> Unit,
    onMorphSingle: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SdStorage,
                    contentDescription = null,
                    tint = PrimaryVioletLight
                )
                Text(
                    text = photo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Photo Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    if (!photo.uriString.isNullOrBlank()) {
                        AsyncImage(
                            model = photo.uriString,
                            contentDescription = photo.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(photo.sampleGradientStart),
                                        Color(photo.sampleGradientEnd)
                                    )
                                )
                            )
                        }
                    }
                }

                // Storage & Category Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Source: ${photo.category}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Date: ${photo.dateAdded}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quality Heuristics Breakdown
                Text(
                    text = "Aesthetic Quality Analysis",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryVioletLight
                )

                HeuristicMetricBar(label = "Overall Aesthetic", score = photo.aestheticScore)
                HeuristicMetricBar(label = "Sharpness & Focus", score = photo.sharpnessScore)
                HeuristicMetricBar(label = "Lighting & Exposure", score = photo.lightingScore)
                HeuristicMetricBar(label = "Dynamic Range", score = photo.dynamicRangeScore)
            }
        },
        confirmButton = {
            Button(
                onClick = onMorphSingle,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Morph with ${activeStylePreset.name}", fontSize = 12.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onToggleSelection,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isSelected) "Remove from Batch" else "Add to Batch", fontSize = 12.sp)
            }
        }
    )
}

@Composable
private fun HeuristicMetricBar(label: String, score: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "${(score * 100).toInt()}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (score >= 0.90f) AccentEmerald else AccentAmber
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { score.coerceIn(0f, 1f) },
            color = if (score >= 0.90f) AccentEmerald else PrimaryViolet,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        )
    }
}
